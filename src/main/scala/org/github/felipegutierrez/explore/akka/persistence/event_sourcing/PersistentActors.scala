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

  // COMMANDS
  case class Invoice(recipient: String, date: Date, amount: Int)

  // EVENTS
  case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)

  case class InvoiceACK(msg: String)

  class AccountActor extends PersistentActor with ActorLogging {

    var latestInvoiceId = 0
    var totalAmount = 0

    // best-practice: make this unique
    override def persistenceId: String = "simple-account"

    // the normal receive method
    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        /* When we receive a command
         * 1 - we create an EVENT to persist into the store
         * 2 - we persist the event, pass a callback that will get triggered once the event is written
         * 3 - we update the actor's state when the event has persisted
         */
        log.info(s"received invoice for amount : $amount")
        persist(InvoiceRecorded(latestInvoiceId, recipient, date, amount)) { e =>
          /* update the state. SAFE to access mutable state here because
           * there is NO RACE CONDITION inside the persist method. All the
           * messages sent to this actor are STASHED
           *
           * */
          latestInvoiceId += 1
          totalAmount += amount
          sender() ! InvoiceACK("your invoice was persisted")
          log.info(s"Persisted $e as invoice #${e.id}, for total amount $totalAmount")
        }
    }

    // handler that will be called on recovery
    override def receiveRecover: Receive = {
      /** Best practice: follow the logic in the persist step of receiveCommand */
      case InvoiceRecorded(id, _, _, amount) =>
        latestInvoiceId = id
        totalAmount += amount
        log.info(s"recovered invoice #$id for amount $amount, total amount: $totalAmount")
    }
  }

}
