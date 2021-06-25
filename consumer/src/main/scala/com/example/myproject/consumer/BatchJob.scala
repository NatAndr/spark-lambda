package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.spark.sql.{Encoders, SaveMode, SparkSession}
import pureconfig.generic.auto._
import io.circe._
import io.circe.parser.decode
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._

import java.time.Duration
import java.util.Properties
import scala.collection.JavaConverters.{asJavaCollectionConverter, iterableAsScalaIterableConverter}

object BatchJob extends App with LazyLogging {
  val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

  val spark: SparkSession = SparkSession
    .builder()
    .appName(config.batchConsumerAppName)
    .config("spark.master", "local")
    .getOrCreate()

  logger.info("Batch job starting")
  spark.sparkContext.setLogLevel("WARN")

  import spark.implicits._

  val consumer: KafkaConsumer[String, String] = KafkaBatchUtils.getConsumer
  val connectionProperties: Properties = JdbcUtils.getConnectionProperties
  val jdbcUrl = s"jdbc:postgresql://postgres:5432/${config.postgresDatabase}"

  implicit val decoderConfig: Configuration = Configuration.default.withDefaults
  implicit val decodeSettings: Decoder[YouTubeRecord] = deriveConfiguredDecoder

  while (true) {
    consumer.subscribe(List(config.topic).asJavaCollection)
    val records = consumer.poll(Duration.ofSeconds(10))
    for (record <- records.asScala) {
      logger.debug(s"Topic: ${record.topic()}, Key: ${record.key()}, " +
        s"Offset: ${record.offset()}, Partition: ${record.partition()}, Value: ${record.value()}")

      val youtubeRecord = parser.decode[YouTubeRecord](record.value()) match {
        case Right(youTubeRecord) => youTubeRecord
//        case Left(ex) => logger.error(s"Something happened ${ex}")
      }

      spark.sparkContext.parallelize(List(youtubeRecord))
        .toDF()
        .selectExpr("*", "CURRENT_TIMESTAMP() as created_at")
        .write
        .mode(SaveMode.Append)
        .jdbc(jdbcUrl, config.postgresBatchTable, connectionProperties)

      logger.warn(s"New record saved to ${config.postgresBatchTable}")
    }
  }

  consumer.close()
  spark.close()
}
