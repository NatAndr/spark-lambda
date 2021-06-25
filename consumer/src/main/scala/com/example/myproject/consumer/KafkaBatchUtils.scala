package com.example.myproject.consumer


import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import pureconfig.generic.auto._

import java.util.Properties

object KafkaBatchUtils {

  def getConsumer: KafkaConsumer[String, String] = {
    val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

    val properties = new Properties()
    properties.put("bootstrap.servers", config.bootstrapServers)
    properties.put("group.id", "group2")
    properties.put("enable.auto.commit", "true")
    properties.put("auto.commit.interval.ms", "1000")

    new KafkaConsumer(properties, new StringDeserializer, new StringDeserializer)
  }

}
