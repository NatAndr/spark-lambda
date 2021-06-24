package com.example.myproject.consumer

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object BatchJob extends App with LazyLogging {
  val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

  val spark: SparkSession = SparkSession
    .builder()
    .appName("Demo")
    .config("spark.master", "local")
    .getOrCreate()

  import spark.implicits._

  private val df: DataFrame = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("provider/data/RUvideos.csv")

  df.show()

  val topTags: RDD[(String, Int)] = df.as[YouTubeRecord]
    .rdd
    .filter(r => r.tags.nonEmpty)
    .filter(r => r.tags != "[none]")
    .flatMap(r => r.tags.split("\\|"))
    .map(tag => tag.mkString.stripPrefix("\"").stripSuffix("\""))
    .map(tag => tag.toLowerCase())
    .map(tag => (tag, 1))
    .reduceByKey(_ + _)
    .sortBy(-_._2)

  topTags.take(15).foreach(println)

  spark.close()

}
