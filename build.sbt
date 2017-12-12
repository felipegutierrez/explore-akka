name := """scala-akka-stream-kafka"""

version := "1.0"

// scalaVersion := "2.12.4"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.10.2.0",
  "org.apache.kafka" % "kafka-streams" % "0.10.2.0",
  "org.apache.spark" %% "spark-core" % "2.2.0",
  "org.apache.spark" %% "spark-streaming" % "2.2.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.0.0")


resolvers ++= Seq(
 "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

resolvers += Resolver.sonatypeRepo("releases")


