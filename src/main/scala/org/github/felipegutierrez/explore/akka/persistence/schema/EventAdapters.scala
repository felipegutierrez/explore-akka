package org.github.felipegutierrez.explore.akka.persistence.schema

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * cqlsh> select * from akka.messages;
 */
object EventAdapters extends App {

  run()

  def run() = {
    val system = ActorSystem("eventAdapters", ConfigFactory.load().getConfig("eventAdapters"))
    val inventoryManager = system.actorOf(Props[InventoryManager], "inventoryManager")

//    val guitars = for (i <- 1 to 10) yield Guitar(s"$i", s"Hakker $i", "RockTheJVM", if (i % 2 == 0) GuitarDomain.ACOUSTIC else GuitarDomain.ELECTRIC)
//    guitars.foreach { guitar =>
//      inventoryManager ! AddGuitar(guitar, 5)
//    }
    inventoryManager ! "print"
  }

}
