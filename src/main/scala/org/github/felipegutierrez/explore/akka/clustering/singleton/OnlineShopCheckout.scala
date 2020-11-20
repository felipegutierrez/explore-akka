package org.github.felipegutierrez.explore.akka.clustering.singleton

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class OnlineShopCheckout(paymentSystem: ActorRef) extends Actor with ActorLogging {
  var orderId = 0

  override def receive: Receive = {
    case Order(_, totalAmount) =>
      log.info(s"Received order $orderId for amount $totalAmount, sending transaction to validate")
      val newTransaction = Transaction(orderId, UUID.randomUUID().toString, totalAmount)
      paymentSystem ! newTransaction
      orderId += 1
  }
}

object OnlineShopCheckout {
  def props(paymentSystem: ActorRef) = Props(new OnlineShopCheckout(paymentSystem))
}
