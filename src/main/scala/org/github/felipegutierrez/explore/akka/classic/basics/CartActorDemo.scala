package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object CartActorDemo {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    import CartActor._

    val actorSystem = ActorSystem("CartActorDemo")
    val cart = actorSystem.actorOf(Props[CartActor], "CartActor")
    // val cart = TestActorRef[CartActor]

    cart ! AddItem("Item")
    cart ! StartCheckout
  }

  object CartActor {
    sealed trait Command
    case class AddItem(item: Any) extends Command
    case class RemoveItem(item: Any) extends Command
    case object ExpireCart extends Command
    case object StartCheckout extends Command
    case object ConfirmCheckoutCancelled extends Command
    case object ConfirmCheckoutClosed extends Command
    case object GetItems extends Command // command made to make testing easier
    sealed trait Event
    case class CheckoutStarted(checkoutRef: ActorRef) extends Event
    def props() = Props(new CartActor())
  }

  object OrderManager {
    sealed trait Command
    case class AddItem(id: String) extends Command
    case class RemoveItem(id: String) extends Command
    case class SelectDeliveryAndPaymentMethod(delivery: String, payment: String) extends Command
    case object Buy extends Command
    case object Pay extends Command
    case class ConfirmCheckoutStarted(checkoutRef: ActorRef) extends Command
    case class ConfirmPaymentStarted(paymentRef: ActorRef) extends Command
    case object ConfirmPaymentReceived extends Command
    sealed trait Ack
    case object Done extends Ack //trivial ACK
  }

  class CartActor extends Actor {

    import CartActor._

    override def receive: PartialFunction[Any, Unit] = {
      case AddItem(item) => sender ! s"item $item added."
      case StartCheckout => sender ! OrderManager.ConfirmCheckoutStarted(self)
      case msg => println(s"I cannot understand ${msg.toString}")
    }
  }

}
