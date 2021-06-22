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
    case PathList("org", "apache", "spark", "UnusedStubClass.class") => MergeStrategy.last
    case _ => MergeStrategy.first
  },

  assembly / assemblyJarName := s"${name.value}.jar",
  assembly / target := file("build")
)

