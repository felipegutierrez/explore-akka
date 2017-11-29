name := """scala-akka-stream-kafka"""

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.8",
  "com.typesafe.akka" %% "akka-stream" % "2.4.8",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.17",
  "org.apache.kafka" % "kafka_2.11" % "0.10.2.0",
  "org.apache.kafka" % "kafka-streams" % "0.10.2.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.iq80.leveldb"            % "leveldb"          % "0.7",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion)


resolvers ++= Seq(
 "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

resolvers += Resolver.sonatypeRepo("releases")


