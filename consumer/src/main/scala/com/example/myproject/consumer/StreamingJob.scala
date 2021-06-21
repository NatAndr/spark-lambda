package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._

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

  val inputStream = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", config.bootstrapServers)
    .option("subscribe", config.topic)
//    .option("startingOffsets", "earliest")
    .load()
    .selectExpr("CAST(value as STRING)")
    .as[String]
//    .map(_.replace("\"", "").split(","))
//    .map(YouTubeRecord(_))

//  val rows = inputStream
//    .map(_.tags)
//    .filter(!_.equals("[none]"))
//    .map(_.toLowerCase)
//    .map(_.mkString.stripPrefix("\"").stripSuffix("\""))
//    .flatMap(_.split("\\|"))
//    .groupBy("value")
//    .count()

  val query = inputStream.writeStream
    .outputMode("append")
    .format("console")
//    .format("delta")
    .option("checkpointLocation", config.checkpointLocation)
    .start()

  query.awaitTermination()
  spark.stop()
}
