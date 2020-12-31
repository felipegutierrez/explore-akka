package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorLogging, Props}

object InterceptingLogs {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

  }

  case class Checkout(item: String, creditCard: String)

  case class AuthorizeCard(creditCard: String)

  case object PaymentAccepted

  case object PaymentDenied

  case class DispatchOrder(item: String)

  case object OrderConfirmed

  class CheckoutActor extends Actor {
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, card) =>
        paymentManager ! AuthorizeCard(card)
        context.become(pendingPayment(item))
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfillment(item))
      case PaymentDenied =>
        throw new RuntimeException("I can't handle this anymore")
    }

    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout)
    }
  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(card) =>
        if (card.startsWith("0")) sender() ! PaymentDenied
        else {
          Thread.sleep(4000)
          sender() ! PaymentAccepted
        }
    }
  }

  class FulfillmentManager extends Actor with ActorLogging {
    var orderId = 43

    override def receive: Receive = {
      case DispatchOrder(item: String) =>
        orderId += 1
        log.info(s"Order $orderId for item $item has been dispatched.")
        sender() ! OrderConfirmed
    }
  }

}
