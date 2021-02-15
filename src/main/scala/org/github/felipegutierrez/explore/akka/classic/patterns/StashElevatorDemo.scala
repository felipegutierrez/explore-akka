package org.github.felipegutierrez.explore.akka.classic.patterns

import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props, Stash}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object StashElevatorDemo {

//  def main(args: Array[String]): Unit = {
//    run()
//  }

  def run() = {
    val system = ActorSystem("StashElevatorDemo")
    val coordinatorActor = system.actorOf(Props[Coordinator], "coordinatorActor")

    coordinatorActor ! Coordinator.PickUpRequest()
    coordinatorActor ! Coordinator.PickUpRequest()
    coordinatorActor ! Coordinator.PickUpRequest()
  }

  object Coordinator {
    case class PickUpRequest()
    case class DropOffRequest()
  }
  class Coordinator extends Actor with ActorLogging {
    import Coordinator._
    import context.dispatcher
    implicit val timeout = Timeout(3 seconds)
    override def preStart(): Unit = {
      val elevator = context.actorOf(Props[ElevatorActor], s"elevator")
      context.watch(elevator)
    }

    override def receive: Receive = {
      case PickUpRequest() =>
        val elevatorActor: ActorSelection = context.actorSelection(s"/user/coordinatorActor/elevator")
        // val statusFuture = elevatorActor ? ElevatorActor.ReadStatus
        (elevatorActor ? ElevatorActor.ReadStatus)
          .mapTo[ElevatorActor.Status].flatMap(currentFloor => elevatorActor ? ElevatorActor.RequestMove(currentFloor.floor + 5))
          .mapTo[ElevatorActor.RequestMoveSuccess].map(targetFloor => elevatorActor ! ElevatorActor.MakeMove(targetFloor.floor, "I love stash"))

        // elevatorActor ! ElevatorActor.ReadStatus // it will stash
        // elevatorActor ! ElevatorActor.RequestMove // it will stash
        // elevatorActor ! ElevatorActor.MakeMove(1, "I love stash too") // it will read because the second RequestMove will unstash
      // elevatorActor ! ElevatorActor.ReadStatus
      case DropOffRequest() => ???
    }
  }

  object ElevatorActor {
    case class MakeMove(targetFloor: Int, data: String)
    case class RequestMove(floor: Int)
    case class RequestMoveSuccess(floor: Int)
    case object Close
    case object ReadStatus
    case class Status(floor: Int)
  }
  class ElevatorActor extends Actor with ActorLogging with Stash {
    import ElevatorActor._
    private var floor: Int = 0
    override def receive: Receive = openAndStopped
    def openAndStopped: Receive = {
      case ReadStatus =>
        println(s"I am on floor: $floor")
        sender() ! Status(floor)
      case msg@RequestMove(floor) =>
        println(msg)
        sender() ! RequestMoveSuccess(floor)
        // step 3 - unstashAll when you switch the message handler
        unstashAll()
        context.become(closedAndMoving)

      case message =>
        println(s"Stashing $message because I can't handle it in the openAndStopped state")
        stash()
    }
    def closedAndMoving: Receive = {
      case msg@MakeMove(targetFloor, data) =>
        println(s"I am $msg in 500 milliseconds: $data")
        Thread.sleep(500)
        floor = targetFloor
        unstashAll()
        context.become(openAndStopped)
      case message =>
        println(s"Stashing $message because I can't handle it in the closedAndMoving state")
        stash()
    }
  }
}
