package org.github.felipegutierrez.explore.akka.classic.persistence.serialization

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.SimplePersistentActor
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.Datamodel.OnlineStoreUser

object ProtobufSerialization_Persistence {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    println("run: cd scripts/akka-persistence")
    println("run: docker-compose up")
    println("run: ./psql.sh")
    println("run: rtjvm=# select * from journal;")
    val config = ConfigFactory.load().getConfig("postgresStore")
      .withFallback(ConfigFactory.load("protobufSerializablePerson"))
    val system = ActorSystem("postgresStoreSystem", config)

    val simplePersistentActor = system.actorOf(SimplePersistentActor.props("protobuf-actor"), "personProtobufActor")

    val onlineStoreUser = OnlineStoreUser.newBuilder()
      .setId(1234)
      .setUserName("Felipe-rocktheJVM")
      .setUserEmail("Felipe@rocktheJVM.com")
      .setUserPhone("1234-7890")
      .build()
    simplePersistentActor ! onlineStoreUser
  }
}
