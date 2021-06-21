package com.example.myproject.provider

import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.ProducerRecord
import pureconfig.generic.auto._


object Generator extends App with StrictLogging {
  val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

  logger.info("Initializing data generator")

  val producer = Producer.init
  producer.flush()
  Producer.createTopic(config.topic, config.partitions, config.replication)
  Thread.sleep(3000)

  logger.info("Kafka producer initialized")

  val records: Iterable[YouTubeRecord] = Reader.getRecords("/data/RUvideos.csv")

  var count = 0

  while (true) {
    records.foreach(record => {

      try {
        val data = new ProducerRecord[String, String](config.topic, record.asJson.toString())
        producer.send(data)
        count += 1
        Thread.sleep(1000)
      } catch {
        case e: Exception => //logger.error("Exception: " + e.getMessage)
      }

      if (count % 100 == 0) {
        logger.info(s"Messages sent: $count")
      }
    })
  }

  producer.close()
}
