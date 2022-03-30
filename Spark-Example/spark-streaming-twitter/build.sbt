ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.5"

lazy val root = (project in file("."))
  .settings(
    name := "spark-streaming-twitter",
    idePackagePrefix := Some("com.github.firasesbai.spark")
  )

libraryDependencies ++=  Seq(
  "org.apache.spark" %% "spark-core" % "3.1.2",
  "org.apache.spark" %% "spark-streaming" % "3.1.2",
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  "com.twitter" % "jsr166e" % "1.1.0",
)
