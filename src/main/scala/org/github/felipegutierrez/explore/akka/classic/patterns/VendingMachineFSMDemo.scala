package org.github.felipegutierrez.explore.akka.classic.patterns

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, FSM, Props}

import scala.concurrent.duration._

object VendingMachineFSMDemo extends App {

  run()

  def run() = {

    import VendingMachineFSMDemo.VendingMachineFSM._

    val system = ActorSystem("VendingMachineFSMDemo")

    val customer = system.actorOf(Props[Customer], "Customer")
    customer ! RequestProduct("coke")
    customer ! Initialize(Map("coke" -> 10), Map("coke" -> 3))
    customer ! RequestProduct("coke")
    customer ! "some unknown message"
    customer ! ReceiveMoney(10)
    customer ! RequestProduct("coke")

    Thread.sleep(5000)
    system.terminate()
  }

  class Customer extends Actor with ActorLogging {
    protected val vendingMachineFSMActor = context.actorOf(Props[VendingMachineFSM], "VendingMachineFSM")
    import VendingMachineFSM._
    override def receive: Receive = {
      case msg: OperationMessage => vendingMachineFSMActor ! msg
      case msg: ReplyMessage => log.info(s"reply message: ${msg.toString}")
      case msg => log.info(s"something else: ${msg.toString}")
    }
  }

  object VendingMachineFSM {
    trait OperationMessage
    trait ReplyMessage
    // Step 1 - define state and data for the Finite State Machine Actor
    trait VendingState
    // the states are the receive methods of the old VendingMachine
    case object Idle extends VendingState
    case object Operational extends VendingState
    case object WaitingForMoney extends VendingState
    // the data are the properties of the receive methods of the old VendingMachine.
    // the WaitingForMoneyData does NOT need 'moneyTimeoutSchedule: Cancellable' because the FSM Actor implements it out of the box for us
    trait VendingData extends OperationMessage
    case object Uninitialized extends VendingData
    case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData
    case class WaitingForMoneyData(inventory: Map[String, Int], prices: Map[String, Int], product: String, money: Int, requester: ActorRef) extends VendingData
    // messages
    case class Initialize(inventory: Map[String, Int], prices: Map[String, Int]) extends OperationMessage
    case class RequestProduct(product: String) extends OperationMessage
    case class Instruction(instruction: String) extends ReplyMessage // message the VM will show on its "screen"
    case class ReceiveMoney(amount: Int) extends OperationMessage
    case class Deliver(product: String) extends ReplyMessage
    case class GiveBackChange(amount: Int) extends ReplyMessage
    case class VendingError(reason: String) extends ReplyMessage
    case object ReceiveMoneyTimeout extends OperationMessage
  }

  import VendingMachineFSM._

  class VendingMachineFSM extends FSM[VendingState, VendingData] {
    // FSM's dont have a receive handler. FSM's handle events with messages and data -> EVENT(message, data)
    // initial state
    startWith(Idle, Uninitialized)
    // FSM at state Idle
    when(Idle) {
      case Event(Initialize(inventory, prices), Uninitialized) =>
        //case Initialize(inventory, prices) => context.become(operational(inventory, prices))
        goto(Operational) using Initialized(inventory, prices)
      case _ =>
        //case _ => sender() ! VendingError("MachineNotInitialized")
        sender() ! VendingError("MachineNotInitialized")
        stay()
    }
    // FSM at state Operational
    when(Operational) {
      case Event(RequestProduct(product), Initialized(inventory, prices)) => {
        inventory.get(product) match {
          case None | Some(0) =>
            sender() ! VendingError("ProductNotAvailable")
            stay()
          case Some(_) =>
            val price = prices(product)
            sender() ! Instruction(s"Please insert $price dollars")
            goto(WaitingForMoney) using WaitingForMoneyData(inventory, prices, product, 0, sender())
        }
      }
    }
    // FSM at state WaitingForMoney
    when(WaitingForMoney, stateTimeout = 1 second) {
      case Event(StateTimeout, WaitingForMoneyData(inventory, prices, product, money, requester)) => {
        requester ! VendingError("RequestTimedOut")
        if (money > 0) requester ! GiveBackChange(money)
        goto(Operational) using Initialized(inventory, prices)
      }
      case Event(ReceiveMoney(amount), WaitingForMoneyData(inventory, prices, product, money, requester)) => {
        // moneyTimeoutSchedule.cancel() // dont need in FSM Actors, intead we use stateTimeout at the FSM.when declaration =)
        val price = prices(product)
        if (money + amount >= price) {
          // user buys product
          requester ! Deliver(product)
          // deliver the change
          if (money + amount - price > 0) requester ! GiveBackChange(money + amount - price)
          // updating inventory
          val newStock = inventory(product) - 1
          val newInventory = inventory + (product -> newStock)
          // context.become(operational(newInventory, prices))
          goto(Operational) using Initialized(newInventory, prices)
        } else {
          val remainingMoney = price - money - amount
          requester ! Instruction(s"Please insert $remainingMoney dollars")
          //context.become(waitForMoney(inventory, prices, product, money + amount, startReceiveMoneyTimeoutSchedule, requester))
          stay() using WaitingForMoneyData(
            inventory, prices, product, // don't change
            money + amount, // user has inserted some money
            requester)
        }
      }
    }
    // FSM at a state not found
    whenUnhandled {
      case Event(_, _) =>
        sender() ! VendingError("CommandNotFound")
        stay()
    }
    // FSM can log during transition of events
    onTransition {
      case stateA -> stateB => log.info(s"Transitioning from $stateA to $stateB")
    }
    // it is necessary to start the FSM
    initialize()
  }

}
