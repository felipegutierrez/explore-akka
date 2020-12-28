
name := """explore-akka"""
version := "1.1"
scalaVersion := "2.12.7"

val akkaVersion = "2.6.10"
val scalaBinVersion = "2.12"
val scalaTestVersion = "3.2.0"
val logbackVersion = "1.2.3"
lazy val akkaHttpVersion = "10.2.2"
lazy val akkaGrpcVersion = "1.0.2"
lazy val protobufVersion = "3.11.4"
lazy val aeronVersion = "1.30.0"
lazy val leveldbVersion = "0.7"
lazy val leveldbjniVersion = "1.8"
lazy val postgresVersion = "42.2.2"
lazy val cassandraVersion = "0.91"
lazy val json4sVersion = "3.2.11"
lazy val kamonVersion = "2.1.9"

// some libs are available in Bintray's JCenter
resolvers += Resolver.jcenterRepo

enablePlugins(JavaAppPackaging, JavaServerAppPackaging, AkkaGrpcPlugin, DockerPlugin)

akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java)

libraryDependencies ++= Seq(
  // Akka basics
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

  // Akka typed
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,

  // Akka remote and cluster
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "io.aeron" % "aeron-driver" % aeronVersion,
  "io.aeron" % "aeron-client" % aeronVersion,

  // Akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  // Akka locating services
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-pki" % akkaVersion,

  // Akka persistence
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,

  // Serialization frameworks
  "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1",
  "com.sksamuel.avro4s" %% "avro4s-core" % "2.0.4",
  "com.google.protobuf" % "protobuf-java" % "3.6.1",
  "io.spray" %%  "spray-json" % "1.3.5",

  // local levelDB stores
  "org.iq80.leveldb" % "leveldb" % leveldbVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniVersion,

  // JDBC with PostgreSQL
  "org.postgresql" % "postgresql" % postgresVersion,
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",

  // Cassandra
  "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % cassandraVersion % Test,

  // Google Protocol Buffers
  "com.google.protobuf" % "protobuf-java"  % protobufVersion,

  // Akka HTTP: overwrites are required because Akka-gRPC depends on 10.1.x
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

  // Akka log
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,

  // Scala test
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "junit" % "junit" % "4.13" % Test,

  // JWT
  "com.pauldijou" %% "jwt-spray-json" % "4.3.0",

  // Metrics: Kamon + Prometheus
  "io.kamon" %% "kamon-bundle" % kamonVersion,
  "io.kamon" %% "kamon-prometheus" % kamonVersion,
)

mainClass in Compile := Some("org.github.felipegutierrez.explore.akka.MainClass")

dockerUsername := Some("felipeogutierrez")
