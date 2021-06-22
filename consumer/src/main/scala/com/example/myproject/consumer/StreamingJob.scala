package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StreamingJob extends App with LazyLogging {
  val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

  val spark: SparkSession = SparkSession.builder()
    .appName(config.structuredConsumerAppName)
    //    .config("spark.driver.memory", "3g")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  logger.info("Initializing Structured consumer")
  spark.sparkContext.setLogLevel("WARN")

  val sc = spark.sparkContext
  val ssc = new StreamingContext(sc, Seconds(5))
  val topics = Array(config.topic)
  ssc.checkpoint("/spark")

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> config.bootstrapServers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "group1",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val directKafkaStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  val transformed: DStream[(String, Int)] = directKafkaStream.transform { rdd =>
    val df = spark.read.json(rdd.map(x => x.value))

    if (df.columns.nonEmpty) {
      df.as[YouTubeRecord]
        .rdd
        .map(_.tags)
        .filter(!_.equals("[none]"))
        .map(_.toLowerCase)
        .map(_.mkString.stripPrefix("\"").stripSuffix("\""))
        .flatMap(_.split("\\|"))
        .map((_, 1))
    } else {
      spark.sparkContext.emptyRDD[(String, Int)]
    }
  }

  // Now count them up over a 5 minute window sliding every 10 seconds
  val tagCounts = transformed.reduceByKeyAndWindow((x, y) => x + y, (x, y) => x - y, Seconds(60 * 5), Seconds(10))

  // Sort the results by the count values
  val sortedResults = tagCounts.transform(rdd => rdd.sortBy(x => x._2, ascending = false))

  // Print top 10
  sortedResults.print

  ssc.start()
  ssc.awaitTermination()
  spark.stop()
}
