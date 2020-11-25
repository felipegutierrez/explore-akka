package org.github.felipegutierrez.explore.akka.persistence.schema

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

import scala.collection.mutable

object GuitarDomain {
  // store for acoustic guitars
  val ACOUSTIC = "acoustic"
  val ELECTRIC = "electric"
}

// data structures
case class Guitar(id: String, model: String, make: String, guitarType: String = GuitarDomain.ACOUSTIC)

// command
case class AddGuitar(guitar: Guitar, quantity: Int)

// event
case class GuitarAdded(guitarId: String, guitarModel: String, guitarMake: String, quantity: Int)

case class GuitarAddedVersion2(guitarId: String, guitarModel: String, guitarMake: String, quantity: Int, guitarType: String)

class InventoryManager extends PersistentActor with ActorLogging {

  val inventory: mutable.Map[Guitar, Int] = new mutable.HashMap[Guitar, Int]()

  override def persistenceId: String = "guitar-inventory-manager"

  override def receiveCommand: Receive = {
    case AddGuitar(guitar@Guitar(id, model, make, guitarType), quantity) =>
      persist(GuitarAddedVersion2(id, model, make, quantity, guitarType)) { _ =>
        addGuitarInventory(guitar, quantity)
        log.info(s"Added $quantity x $guitar to inventory")
      }
    case "print" =>
      log.info(s"Current inventory is: $inventory")
  }

  def addGuitarInventory(guitar: Guitar, quantity: Int) = {
    val existingQuantity = inventory.getOrElse(guitar, 0)
    inventory.put(guitar, existingQuantity + quantity)
  }

  override def receiveRecover: Receive = {
    /** it is not necessary because we have GuitarEventAdapter */
    /*
    case event@GuitarAdded(id, model, make, quantity) =>
      log.info(s"Recovered $event")
      val guitar = Guitar(id, model, make, ACOUSTIC)
      addGuitarInventory(guitar, quantity)
    */
    /** we handle only the latest version of GuitarAdded */
    case event@GuitarAddedVersion2(id, model, make, quantity, guitarType) =>
      log.info(s"Recovered $event")
      val guitar = Guitar(id, model, make, guitarType)
      addGuitarInventory(guitar, quantity)
  }
}

