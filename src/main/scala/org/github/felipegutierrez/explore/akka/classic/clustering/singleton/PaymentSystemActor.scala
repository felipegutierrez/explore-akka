package org.github.felipegutierrez.explore.akka.classic.clustering.singleton

import akka.actor.{Actor, ActorLogging}

case class Order(items: List[String], total: Double)

case class Transaction(orderId: Int, txnId: String, amount: Double)

class PaymentSystemActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case t: Transaction => log.info(s"validating transaction $t")
    case m => log.info(s"received unknown message $m")
  }
}
