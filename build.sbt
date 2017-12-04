name := """scala-akka-stream-kafka"""

version := "1.0"

scalaVersion := "2.12.4"

val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.10.2.0",
  "org.apache.kafka" % "kafka-streams" % "0.10.2.0")


resolvers ++= Seq(
 "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

resolvers += Resolver.sonatypeRepo("releases")


