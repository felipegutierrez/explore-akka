package org.github.felipegutierrez.explore.akka.classic.persistence.serialization

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.SimplePersistentActor
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.VoteGenerator

object BenchmarkSerialization {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val config = ConfigFactory.load().getConfig("postgresStore")
      .withFallback(ConfigFactory.load("serializersBenchmark"))
    val system = ActorSystem("postgresStoreSystem", config)

    val simplePersistentActor = system.actorOf(SimplePersistentActor.props("benchmark-java"), "benchmark")
    // val simplePersistentActor = system.actorOf(SimplePersistentActor.props("benchmark-avro"), "benchmark")
    // val simplePersistentActor = system.actorOf(SimplePersistentActor.props("benchmark-kryo"), "benchmark")
    // val simplePersistentActor = system.actorOf(SimplePersistentActor.props("benchmark-protobuf"), "benchmark")

    val votes = VoteGenerator.generateVotes(10000) // java,avro,kryo
    // val votes = VoteGenerator.generateProtobufVotes(10000) // protobuffer
    votes.foreach(simplePersistentActor ! _)
  }
}
