ThisBuild / scalaVersion := "2.12.6"

lazy val root = (project in file("."))
  .settings(
    name := "lambda",
  )
  .aggregate(provider, consumer)
  .dependsOn(provider, consumer)

lazy val provider = (project in file("provider"))
  .settings(
    name := "provider",
    libraryDependencies ++= Dependencies.provider,
    //    resolvers ++= Dependencies.resolvers,
    commonSettings
  )

lazy val consumer = (project in file("consumer"))
  .settings(
    name := "consumer",
    libraryDependencies ++= Dependencies.consumer,
    //    resolvers ++= Dependencies.resolvers,
    commonSettings
  )


lazy val commonSettings = Seq(
  assembly / assemblyMergeStrategy := {
    case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    //      case PathList(xs @ _*) if xs.last == "UnusedStubClass.class" => MergeStrategy.first
    //      case PathList("org", "apache", "spark", "spark-streaming-kafka-0-10_2.12", "UnusedStubClass.class") =>
    //        println("1! ")
    //        MergeStrategy.first
    //      case PathList("org", "apache", "spark", "spark-tags_2.12", "UnusedStubClass.class") =>
    //        println("2! ")
    //        MergeStrategy.first
    //      case PathList("org", "apache", "spark", "spark-token-provider-kafka-0-10_2.12", "UnusedStubClass.class") =>
    //        println("3! ")
    //        MergeStrategy.first
    //      case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") =>
    //        println("4! ")
    //        MergeStrategy.first
    case PathList("spark-streaming-kafka-0-10_2.12-3.1.0.jar", xs@_*) => MergeStrategy.last
    case PathList("spark-tags_2.12-3.1.0.jar", xs@_*) => MergeStrategy.last
    case PathList("spark-token-provider-kafka-0-10_2.12-3.1.0.jar", xs@_*) => MergeStrategy.last
    case PathList("unused-1.0.0.jar", xs@_*) => MergeStrategy.last
    case _ => MergeStrategy.first
  },

  assembly / assemblyJarName := s"${name.value}.jar",
  assembly / target := file("build")
)

