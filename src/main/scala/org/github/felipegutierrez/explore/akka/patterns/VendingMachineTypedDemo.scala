package org.github.felipegutierrez.explore.akka.patterns

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object VendingMachineTypedDemo extends App {

  run()

  def run() = {

    import VendingMachineTyped._
    val functionalVendingMachineSystem = ActorSystem(functionalVendingMachine(), "FunctionalVendingMachine")

    functionalVendingMachineSystem ! RequestProduct("coke")
    functionalVendingMachineSystem ! Initialize(Map("coke" -> 10), Map("coke" -> 3))
    functionalVendingMachineSystem ! RequestProduct("coke")
    functionalVendingMachineSystem ! ReceiveMoney(10)
    functionalVendingMachineSystem ! RequestProduct("coke")

    Thread.sleep(5000)
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
      // def requester(): Option[ActorRef[VendingMessage]]
    }
    case object Uninitialized extends VendingData {
      override def inventory(): Map[String, Int] = Map[String, Int]()
      override def prices(): Map[String, Int] = Map[String, Int]()
      override def product(): Option[String] = None
      override def money(): Int = 0
      // override def requester(): Option[ActorRef[VendingMessage]] = None
    }
    case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData {
      override def product(): Option[String] = None
      override def money(): Int = 0
      // override def requester(): Option[ActorRef[VendingMessage]] = None
    }
    case class WaitingForMoneyData(inventory: Map[String, Int],
                                   prices: Map[String, Int],
                                   product: Option[String],
                                   money: Int
                                   // , requester: Option[ActorRef[VendingMessage]]
                                  ) extends VendingData

    // messages
    trait VendingMessage
    case class Message(msg: String) extends VendingMessage
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

  def functionalVendingMachine(state: VendingState = Idle, data: VendingData = Uninitialized): Behavior[VendingMessage] =
    Behaviors.receive { (context, message) =>
      val actorRef: ActorRef[VendingMessage] = context.self
      message match {
        case Initialize(inventory, prices) =>
          context.log.info(s"Vending machine initializing")
          if (state == Idle) {
            actorRef ! Message("Vending machine initialized")
            functionalVendingMachine(Operational, Initialized(inventory, prices))
          } else {
            actorRef ! VendingError("MachineNotInitialized")
            Behaviors.same
          }
        case RequestProduct(product) =>
          context.log.info(s"Vending machine receive request")
          if (state == Operational) {
            data.inventory().get(product) match {
              case None | Some(0) =>
                context.self ! VendingError("ProductNotAvailable")
                Behaviors.same
              case Some(_) =>
                val price = data.prices().get(product)
                context.self ! Instruction(s"Please insert $price dollars")
                functionalVendingMachine(WaitingForMoney, WaitingForMoneyData(data.inventory(), data.prices(), Some(product), 0
                  //, None
                ))
            }
          } else {
            context.self ! VendingError("MachineNotInitialized")
            Behaviors.same
          }
        case ReceiveMoney(amount) =>
          context.log.info(s"Vending machine received money")
          if (state == WaitingForMoney) {
            val price = data.prices().get(data.product().get).getOrElse(0)
            if (data.money() + amount >= price) {
              // user buys product
              context.self ! Deliver(data.product().get)
              // deliver the change
              if (data.money() + amount - price > 0) context.self ! GiveBackChange(data.money() + amount - price)
              // updating inventory
              val newStock = data.inventory()(data.product().get) - 1
              val newInventory = data.inventory() + (data.product().get -> newStock)
              functionalVendingMachine(Operational, Initialized(newInventory, data.prices()))
            } else {
              val remainingMoney = price - data.money() - amount
              context.self ! Instruction(s"Please insert $remainingMoney dollars")
              // Behaviors.same
              functionalVendingMachine(WaitingForMoney,
                WaitingForMoneyData(
                  data.inventory(), data.prices(), data.product(), // don't change
                  data.money() + amount // user has inserted some money
                  // , data.requester()
                )
              )
            }
          } else {
            context.self ! VendingError("MachineNotInitialized")
            Behaviors.same
          }
        case msg: VendingMessage =>
          context.log.info(msg.toString)
          Behaviors.same
      }
  }
}
