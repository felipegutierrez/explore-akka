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

//enablePlugins(GitVersioning)
//dockerExposedPorts := Seq(8080)
//git.formattedShaVersion := git.gitHeadCommit.value map { sha =>
//  s"$sha".substring(0, 7)
//}
//dockerUpdateLatest := true
//dockerUsername := Some("felipeogutierrez")
//dockerAlias := DockerAlias(None, dockerUsername.value, (packageName in Docker).value, git.gitDescribedVersion.value)

