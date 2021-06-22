import sbt.ModuleID
import sbt.librarymanagement.{DependencyBuilders, LibraryManagementSyntax}

object Dependencies extends DependencyBuilders with LibraryManagementSyntax {
  object Versions {
    val spark = "3.1.0"
    val postgres = "42.2.2"
    //    val scalaTestVersion = "3.2.1"
    val circe = "0.12.3"
    val log4j = "2.4.1"
    val kafka = "2.8.0"
  }

  private val spark = Seq(
    "org.apache.spark" %% "spark-sql" % Versions.spark % "provided",
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % Versions.spark % "provided",
    "org.apache.spark" %% "spark-streaming" % Versions.spark % "provided",
    "org.apache.spark" %% "spark-sql-kafka-0-10" % Versions.spark % "provided"

    //    "org.apache.spark" %% "spark-core" % Versions.spark,
    //    "org.apache.spark" %% "spark-sql" % Versions.spark % Test,
    //    "org.apache.spark" %% "spark-sql" % Versions.spark % Test classifier "tests",
    //    "org.apache.spark" %% "spark-core" % Versions.spark % Test classifier "tests"
  )

  private val kafka = Seq(
    "org.apache.kafka" % "kafka-clients" % Versions.kafka
  )

  private val postgres = Seq(
    "org.postgresql" % "postgresql" % Versions.postgres
  )

  private val config = Seq(
    "com.typesafe" % "config" % "1.4.0",
    "com.github.pureconfig" %% "pureconfig" % "0.11.0"
  )

  private val circle = Seq(
    "io.circe" %% "circe-core" % Versions.circe,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-parser" % Versions.circe
  )

  private val commons = Seq(
    "org.apache.commons" % "commons-csv" % "1.8"
    //    "org.apache.commons" % "commons-lang3" % "3.12.0"
  )

  private val logging = Seq(
    "org.apache.logging.log4j" % "log4j-api" % "2.4.1",
    "org.apache.logging.log4j" % "log4j-core" % "2.4.1",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"
  )

  private val jackson = Seq(
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"
  )


  val provider: Seq[ModuleID] = config ++ commons ++ circle ++ logging ++ jackson ++ kafka
  val consumer: Seq[ModuleID] = config ++ spark ++ commons ++ circle ++ logging ++ jackson ++ kafka ++ postgres

  val resolvers = Seq(
    "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven",
    "Typesafe Simple Repository" at "https://repo.typesafe.com/typesafe/simple/maven-releases",
    "MavenRepository" at "https://mvnrepository.com"
  )
}
