package org.github.felipegutierrez.explore.akka.patterns

import akka.actor.ActorRef
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object VendingMachineTypedDemo extends App {

  run()

  def run() = {

    import VendingMachineTyped._
    val functionalVendingMachineSystem = ActorSystem(functionalVendingMachine(), "FunctionalVendingMachine")

    functionalVendingMachineSystem ! Initialize(Map("coke" -> 10), Map("coke" -> 3))
    functionalVendingMachineSystem ! RequestProduct("coke")
    functionalVendingMachineSystem ! ReceiveMoney(10)

    Thread.sleep(1000)
    functionalVendingMachineSystem.terminate()
  }

  object VendingMachineTyped {
    // the states are the receive methods of the old VendingMachine
    trait VendingState
    case object Idle extends VendingState
    case object Operational extends VendingState
    case object WaitingForMoney extends VendingState

    // the data are the properties of the receive methods of the old VendingMachine.
    // the WaitingForMoneyData does NOT need 'moneyTimeoutSchedule: Cancellable' because the FSM Actor implements it out of the box for us
    trait VendingData {
      def inventory(): Map[String, Int]
      def prices(): Map[String, Int]
      def product(): Option[String]
      def money(): Int
      def requester(): Option[ActorRef]
    }
    case object Uninitialized extends VendingData {
      override def inventory(): Map[String, Int] = Map[String, Int]()
      override def prices(): Map[String, Int] = Map[String, Int]()
      override def product(): Option[String] = None
      override def money(): Int = 0
      override def requester(): Option[ActorRef] = None
    }
    case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData {
      override def product(): Option[String] = None
      override def money(): Int = 0
      override def requester(): Option[ActorRef] = None
    }
    case class WaitingForMoneyData(inventory: Map[String, Int], prices: Map[String, Int], product: Option[String], money: Int, requester: Option[ActorRef]) extends VendingData

    // messages
    trait VendingMessage
    case class Initialize(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingMessage
    case class RequestProduct(product: String) extends VendingMessage
    case class Instruction(instruction: String) extends VendingMessage // message the VM will show on its "screen"
    case class ReceiveMoney(amount: Int) extends VendingMessage
    case class Deliver(product: String) extends VendingMessage
    case class GiveBackChange(amount: Int) extends VendingMessage
    case class VendingError(reason: String) extends VendingMessage
    case object ReceiveMoneyTimeout extends VendingMessage
  }

  import VendingMachineTyped._

  def functionalVendingMachine(state: VendingState = Idle, data: VendingData = Uninitialized): Behavior[VendingMessage] = Behaviors.receive { (context, message) =>
    message match {
      case Initialize(inventory, prices) =>
        context.log.info(s"Vending machine initializing")
        functionalVendingMachine(Operational, Initialized(inventory, prices))
      case RequestProduct(product) =>
        context.log.info(s"Vending machine receive request")
        data.inventory().get(product) match {
          case None | Some(0) =>
            // sender() ! VendingError("ProductNotAvailable")
            Behaviors.same
          case Some(_) =>
            val price = data.prices().get(product)
            // sender() ! Instruction(s"Please insert $price dollars")
            functionalVendingMachine(WaitingForMoney, WaitingForMoneyData(data.inventory(), data.prices(), Some(product), 0, None))
        }
      case ReceiveMoney(amount) =>
        context.log.info(s"Vending machine received money")
        val price = data.prices().get(data.product().get).getOrElse(0)
        if (data.money() + amount >= price) {
          // user buys product
          data.requester().get ! Deliver(data.product().get)
          // deliver the change
          if (data.money() + amount - price > 0) data.requester().get ! GiveBackChange(data.money() + amount - price)
          // updating inventory
          val newStock = data.inventory()(data.product().get) - 1
          val newInventory = data.inventory() + (data.product().get -> newStock)
          functionalVendingMachine(Operational, Initialized(newInventory, data.prices()))
        } else {
          val remainingMoney = price - data.money() - amount
          data.requester().get ! Instruction(s"Please insert $remainingMoney dollars")
          // Behaviors.same
          functionalVendingMachine(WaitingForMoney,
            WaitingForMoneyData(
              data.inventory(), data.prices(), data.product(), // don't change
              data.money() + amount, // user has inserted some money
              data.requester())
          )
        }
    }
  }
}
