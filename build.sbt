name := """explore-akka"""

version := "1.1"

scalaVersion := "2.12.7"
val akkaVersion = "2.6.10"
val scalaBinVersion = "2.12"

libraryDependencies ++= Seq(
  // akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  // test unit
  "org.scalatest" %% "scalatest" % "3.2.2" % Test
)

mainClass in Compile := Some("org.github.explore.MainClass")

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

