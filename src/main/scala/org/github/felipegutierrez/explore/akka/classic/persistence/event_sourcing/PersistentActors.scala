package org.github.felipegutierrez.explore.akka.classic.persistence.event_sourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    val system = ActorSystem("PersistentActors")
    val accountActor = system.actorOf(Props[AccountActor], "accountActor")

    // persisting single events
    for (i <- 1 to 10) {
      accountActor ! Invoice("the sofa company", new Date(), i * 1000)
    }
    // persisting multiple events
    val newInvoices = for (i <- 1 to 5) yield Invoice("a invoice in a bulk", new Date, i * 2000)
    accountActor ! InvoiceBulk(newInvoices.toList)

    /** Don't use PoisonPill to shutdown persistent actors because
     * this messages are handles in a separated mailbox and
     * may corrupt the state of the actor.
     * Best practice is to use your own message to shutdown the
     * persistent actor and call context.stop(self) */
    // accountActor ! PoisonPill
    Thread.sleep(10000)
    accountActor ! ShutdownAccount
  }

  // COMMANDS
  case class Invoice(recipient: String, date: Date, amount: Int)

  case class InvoiceBulk(invoices: List[Invoice])

  object ShutdownAccount

  // EVENTS
  case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)

  case class InvoiceACK(msg: String)

  class AccountActor extends PersistentActor with ActorLogging {
    /**
     * NEVER EVER CALL PERSIST OR PERSISTALL FROM FUTURES.
     * Because you will break the Akka concurrency encapsulation.
     */

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
           **/
          latestInvoiceId += 1
          totalAmount += amount
          // sender() ! InvoiceACK("your invoice was persisted")
          log.info(s"Persisted $e as invoice #${e.id}, for total amount $totalAmount")
        }
      case InvoiceBulk(invoices) =>
        /* Persisting multiple events
         * 1 - create events
         * 2 - persist all events
         * 3 - update the actor state when each event is persisted
         */
        val invoiceIds = latestInvoiceId to (latestInvoiceId + invoices.size)
        val events = invoices.zip(invoiceIds).map { pair =>
          val invoice = pair._1
          val id = pair._2
          InvoiceRecorded(id, invoice.recipient, invoice.date, invoice.amount)
        }
        persistAll(events) { e =>
          latestInvoiceId += 1
          totalAmount += e.amount
          log.info(s"Persisted single $e as invoice #${e.id}, for total amount $totalAmount")
        }
      case ShutdownAccount => context.stop(self)
    }

    // handler that will be called on recovery
    override def receiveRecover: Receive = {
      /** Best practice: follow the logic in the persist step of receiveCommand */
      case InvoiceRecorded(id, recipient, date, amount) =>
        latestInvoiceId = id
        totalAmount += amount
        log.info(s"recovered invoice #$id [$recipient] for amount $amount at $date, total amount: $totalAmount")
    }

    /**
     * this method is called if persisting failed. The actor will be stooped.
     * BEst practice is to start the actor again after a while and use Backoff supervisor.
     */
    override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"fail to persist $event because of: $cause")
      super.onPersistFailure(cause, event, seqNr)
    }

    /**
     * Called if the JOURNAl fails to persist the event. The actor is RESUMED.
     */
    override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"persist rejected for $event because of: $cause")
      super.onPersistRejected(cause, event, seqNr)
    }
  }

}
