
name := """explore-akka"""
version := "1.1"
scalaVersion := "2.12.7"

val akkaVersion = "2.6.10"
val scalaBinVersion = "2.12"
val scalaTestVersion = "3.2.0"
val logbackVersion = "1.2.3"
lazy val akkaHttpVersion = "10.2.0"
lazy val akkaGrpcVersion = "1.0.2"

enablePlugins(AkkaGrpcPlugin)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-pki" % akkaVersion,
  // The Akka HTTP overwrites are required because Akka-gRPC depends on 10.1.x
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion
)

mainClass in Compile := Some("org.github.felipegutierrez.explore.akka.MainClass")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
dockerUsername := Some("felipeogutierrez")
