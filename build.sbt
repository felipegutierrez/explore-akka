name := """explore-akka"""

version := "1.1"

scalaVersion := "2.12.7"
val sparkVersion = "3.0.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "1.0.0",
  "org.apache.kafka" % "kafka-streams" % "1.0.0",
  "org.apache.kafka" % "kafka-clients" % "1.0.0",
  // "org.apache.kafka" %% "kafka-tools" % "0.9.0.0",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion)

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-module-scala" % "2.6.5"

resolvers ++= Seq(
 "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

resolvers += Resolver.sonatypeRepo("releases")


