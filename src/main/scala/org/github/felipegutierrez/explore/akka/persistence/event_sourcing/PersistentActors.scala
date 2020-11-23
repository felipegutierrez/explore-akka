package org.github.felipegutierrez.explore.akka.persistence.event_sourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

  run()

  def run() = {

    val system = ActorSystem("PersistentActors")
    val accountActor = system.actorOf(Props[AccountActor], "accountActor")
    for (i <- 1 to 10) {
      accountActor ! Invoice("the sofa company", new Date(), i * 1000)
    }
  }

  case class Invoice(recipient: String, date: Date, amount: Int)

  class AccountActor extends PersistentActor with ActorLogging {

    // best-practice: make this unique
    override def persistenceId: String = "simple-account"

    // the normal receive method
    override def receiveCommand: Receive = ???

    // handler that will be called on recovery
    override def receiveRecover: Receive = ???
  }

}
