package com.example.myproject.consumer

import com.example.myproject.consumer.StreamingJob.{config, ssc, topics}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import pureconfig.generic.auto._


object KafkaStreamUtils {

  def init(ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
    val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> config.bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](Array(config.topic), kafkaParams)
    )
  }

}
