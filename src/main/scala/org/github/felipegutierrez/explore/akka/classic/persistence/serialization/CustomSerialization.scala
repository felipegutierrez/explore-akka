package org.github.felipegutierrez.explore.akka.classic.persistence.serialization

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.SimplePersistentActor
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.Person

object CustomSerialization_Persistence {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    println("run: cd scripts/akka-persistence")
    println("run: docker-compose up")
    println("run: ./psql.sh")
    println("run: rtjvm=# select * from journal;")
    val config = ConfigFactory.load().getConfig("postgresStore")
      .withFallback(ConfigFactory.load("customSerializablePerson"))
    val system = ActorSystem("postgresStoreSystem", config)

    val simplePersistentActor = system.actorOf(SimplePersistentActor.props("json-actor"), "personJsonActor")

    simplePersistentActor ! Person("Alice", 23)
  }
}
