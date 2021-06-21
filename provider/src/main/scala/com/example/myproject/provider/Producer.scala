package com.example.myproject.provider

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic

import java.util
import java.util.Properties
import pureconfig.generic.auto._

object Producer {
  def init: KafkaProducer[String, String] = {
    val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]
    val props = new Properties()

    props.put("bootstrap.servers", config.bootstrapServers)
    props.put("client.id", "producer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//    props.put("acks", "all")
//    props.put("metadata.max.age.ms", "10000")

    new KafkaProducer[String, String](props)
  }

  def createTopic(topicName: String, partitions: Int, replication: Int): Unit = {
    val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]
    val props = new Properties()
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers)

    val localKafkaAdmin = AdminClient.create(props)
    val listTopicsResult = localKafkaAdmin.listTopics()

    if (listTopicsResult.listings().get().stream()
      .anyMatch(t => t.name().equalsIgnoreCase(topicName)))
      return

    val topic = new NewTopic(topicName, partitions, replication.toShort)
    val topics = util.Arrays.asList(topic)

    //    val topicStatus = localKafkaAdmin.createTopics(topics).values()
    //    topicStatus.values()
    //    println(topicStatus.keySet())
  }

}
