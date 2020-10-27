name := """explore-akka"""
version := "1.1"
scalaVersion := "2.12.7"
val akkaVersion = "2.6.10"
val scalaBinVersion = "2.12"
val scalaTestVersion = "3.2.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % scalaTestVersion
)

mainClass in Compile := Some("org.github.felipegutierrez.explore.akka.MainClass")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
dockerUsername := Some("felipeogutierrez")
