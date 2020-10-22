package org.github.felipegutierrez.explore.akka.patterns

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, Props}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object VendingMachineDemo extends App {

  run()

  def run() = {
    import VendingMachine._
    val system = ActorSystem("VendingMachineDemo")
    val vendingMachine = system.actorOf(Props[VendingMachine])
    // vendingMachine ! RequestProduct("coke")
    vendingMachine ! Initialize(Map("coke" -> 10), Map("coke" -> 3))
    // vendingMachine ! RequestProduct("coke")
    // vendingMachine ! ReceiveMoney(10)
    // vendingMachine ! RequestProduct("coke")
  }

  object VendingMachine {

    case class Initialize(inventory: Map[String, Int], prices: Map[String, Int])

    case class RequestProduct(product: String)

    case class Instruction(instruction: String) // message the VM will show on its "screen"
    case class ReceiveMoney(amount: Int)

    case class Deliver(product: String)

    case class GiveBackChange(amount: Int)

    case class VendingError(reason: String)

    case object ReceiveMoneyTimeout

  }

  class VendingMachine extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext = context.dispatcher

    import VendingMachine._

    override def receive: Receive = idle

    def idle: Receive = {
      case Initialize(inventory, prices) => context.become(operational(inventory, prices))
      case _ => sender() ! VendingError("MachineNotInitialized")
    }

    def operational(inventory: Map[String, Int], prices: Map[String, Int]): Receive = {
      case RequestProduct(product) => inventory.get(product) match {
        case None | Some(0) =>
          sender() ! VendingError("ProductNotAvailable")
        case Some(_) =>
          val price = prices(product)
          sender() ! Instruction(s"Please insert $price dollars")
          // initial money is 0 because it has to be inserted by the customer
          context.become(waitForMoney(inventory, prices, product, 0, startReceiveMoneyTimeoutSchedule, sender()))
      }
    }

    def waitForMoney(inventory: Map[String, Int],
                     prices: Map[String, Int],
                     product: String,
                     money: Int,
                     moneyTimeoutSchedule: Cancellable,
                     requester: ActorRef): Receive = {
      case ReceiveMoneyTimeout =>
        requester ! VendingError("RequestTimedOut")
        if (money > 0) requester ! GiveBackChange(money)
        context.become(operational(inventory, prices))
      case ReceiveMoney(amount) =>
        moneyTimeoutSchedule.cancel()
        val price = prices(product)
        if (money + amount >= price) {
          // user buys product
          requester ! Deliver(product)
          // deliver the change
          if (money + amount - price > 0) requester ! GiveBackChange(money + amount - price)
          // updating inventory
          val newStock = inventory(product) - 1
          val newInventory = inventory + (product -> newStock)
          context.become(operational(newInventory, prices))
        } else {
          val remainingMoney = price - money - amount
          requester ! Instruction(s"Please insert $remainingMoney dollars")
          context.become(waitForMoney(
            inventory, prices, product, // don't change
            money + amount, // user has inserted some money
            startReceiveMoneyTimeoutSchedule, // I need to set the timeout again
            requester))
        }
    }

    def startReceiveMoneyTimeoutSchedule = context.system.scheduler.scheduleOnce(1 second) {
      self ! ReceiveMoneyTimeout
    }
  }

}
