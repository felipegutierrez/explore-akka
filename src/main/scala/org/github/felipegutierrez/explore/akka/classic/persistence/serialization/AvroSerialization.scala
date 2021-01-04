package org.github.felipegutierrez.explore.akka.classic.persistence.serialization

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.SimplePersistentActor
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.{BankAccount, CompanyRegistry}

object AvroSerialization_Persistence {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    println("run: cd scripts/akka-persistence")
    println("run: docker-compose up")
    println("run: ./psql.sh")
    println("run: rtjvm=# select * from journal;")
    val config = ConfigFactory.load().getConfig("postgresStore")
      .withFallback(ConfigFactory.load("avroSerializablePerson"))
    val system = ActorSystem("postgresStoreSystem", config)

    val simplePersistentActor = system.actorOf(SimplePersistentActor.props("avro-actor"), "personAvroActor")

    val companyRegistryMsg = CompanyRegistry(
      "Google",
      Seq(
        BankAccount("US-1234", "google-bank", 4.3, "gazillion dollars"),
        BankAccount("GB-4321", "google-bank", 0.5, "trillion pounds")
      ),
      "ads",
      523895
    )
    simplePersistentActor ! companyRegistryMsg
  }
}
