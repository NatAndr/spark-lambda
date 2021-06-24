package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{Encoders, SaveMode, SparkSession}
import pureconfig.generic.auto._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.functions.{col, expr}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.Properties

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
  val ssc: StreamingContext = new StreamingContext(sc, Seconds(5))
  val topics = Array(config.topic)
  ssc.checkpoint("/spark")

  val directKafkaStream: InputDStream[ConsumerRecord[String, String]] = KafkaStreamUtils.init(ssc)

  val transformed: DStream[(String, Int)] = directKafkaStream.transform { rdd =>
    val schema = Encoders.product[YouTubeRecord].schema
    val df = spark.read
      .schema(schema)
      .json(rdd.map(x => x.value))

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

  val tagCounts = transformed.reduceByKeyAndWindow((x, y) => x + y, (x, y) => x - y, Seconds(60 * 5), Seconds(10))

  val sortedResults = tagCounts.transform(rdd => {
    val list = rdd.sortBy(-_._2).take(config.topCount)
    rdd.filter(list.contains)
  }
  )

  val connectionProperties: Properties = JdbcUtils.getConnectionProperties
  val jdbcUrl = s"jdbc:postgresql://postgres:5432/${config.postgresDatabase}"

  sortedResults.foreachRDD { rdd =>
    val result = rdd
      .toDF("tags", "count")
      .select(
        col("tags").as("tag"),
        col("count"),
        expr("CURRENT_TIMESTAMP()").as("created_at")
      )
    result
      .write
      .mode(SaveMode.Append)
      .jdbc(jdbcUrl, config.postgresStreamTable, connectionProperties)

    logger.warn("New chunk of data saved to postgres")
  }

  ssc.start()
  ssc.awaitTermination()
  spark.stop()
}
