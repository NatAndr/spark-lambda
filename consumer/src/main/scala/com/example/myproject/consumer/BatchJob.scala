package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import pureconfig.generic.auto._

import java.time.Duration
import scala.collection.JavaConverters.{asJavaCollectionConverter, iterableAsScalaIterableConverter}


object BatchJob extends App with LazyLogging {
  val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

  val spark: SparkSession = SparkSession
    .builder()
    .appName(config.batchConsumerAppName)
    .config("spark.master", "local")
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")

  import spark.implicits._

  val consumer: KafkaConsumer[String, String] = KafkaBatchUtils.getConsumer

  consumer.subscribe(List(config.topic).asJavaCollection)

  consumer
    .poll(Duration.ofSeconds(100))
    .asScala
    .map(r => r.value())
    .map(_.replace("\"", "").split(","))
    .map(YouTubeRecord(_))
    .filter(r => r.tags.nonEmpty)
    .filter(r => r.tags != "[none]")
    .flatMap(r => r.tags.split("\\|"))
    .map(tag => tag.mkString.stripPrefix("\"").stripSuffix("\""))
    .map(tag => tag.toLowerCase())
    .map(tag => (tag, 1))
//    .
//    .sortBy(-_._2)

//  df.show()
//
//  val topTags: RDD[(String, Int)] = df.as[YouTubeRecord]
//    .rdd
//    .filter(r => r.tags.nonEmpty)
//    .filter(r => r.tags != "[none]")
//    .flatMap(r => r.tags.split("\\|"))
//    .map(tag => tag.mkString.stripPrefix("\"").stripSuffix("\""))
//    .map(tag => tag.toLowerCase())
//    .map(tag => (tag, 1))
//    .reduceByKey(_ + _)
//    .sortBy(-_._2)
//
//  topTags.take(15).foreach(println)

  spark.close()

}
