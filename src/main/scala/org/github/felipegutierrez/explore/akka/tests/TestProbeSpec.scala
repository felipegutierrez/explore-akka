package org.github.felipegutierrez.explore.akka.tests

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
  import TestProbeSpec._
  "a master actor" should {
    "register a worker" in {
      val master = system.actorOf(Master.propsMaster)
      val worker = TestProbe("worker")
      master ! Master.Register(worker.ref)
      expectMsg(Master.RegistrationAck)
    }
    "send the task to the worker actor" in {
      val master = system.actorOf(Master.propsMaster)
      val worker = TestProbe("worker")
      master ! Master.Register(worker.ref)
      expectMsg(Master.RegistrationAck)
      val workloadString = "I love Akka"
      master ! Master.Task(workloadString)
      // testing the interaction with master and worker
      worker.expectMsg(Master.TaskToWorker(workloadString, testActor))
      worker.reply(Master.TaskCompleted(3, testActor))
      expectMsg(Master.TaskReport(3))
    }
    "aggregate data correctly" in {
      val master = system.actorOf(Master.propsMaster)
      val worker = TestProbe("worker")
      master ! Master.Register(worker.ref)
      expectMsg(Master.RegistrationAck)
      val workloadString = "I love Akka"
      master ! Master.Task(workloadString)
      master ! Master.Task(workloadString)

      // in the meantime that we don't have a worker implementation
      worker.receiveWhile() {
        case Master.TaskToWorker(`workloadString`, `testActor`) => worker.reply(Master.TaskCompleted(3, testActor))
      }
      expectMsg(Master.TaskReport(3))
      expectMsg(Master.TaskReport(6))
    }
  }
}

object TestProbeSpec {
  object Master {
    case class Register(workerActorRef: ActorRef)
    case object RegistrationAck
    case class Task(text: String)
    case class TaskToWorker(text: String, originalActor: ActorRef)
    case class TaskCompleted(count: Int, originalRequester: ActorRef)
    case class TaskReport(newTotalWordCount: Int)
    val propsMaster = {Props(new Master)}
  }

  class Master extends Actor {
    import Master._
    override def receive: Receive = {
      case Register(workerActorRef) =>
        sender() ! RegistrationAck
        context.become(registerWorkers(workerActorRef, 0))
      case TaskReport(totalWordCount) => println(s"report count is: $totalWordCount words")
      case _ => println(s"unrecognized message")
    }

    def registerWorkers(workerActor: ActorRef, totalWordCount: Int): Receive = {
      case Task(text) => workerActor ! TaskToWorker(text, sender())
      case TaskCompleted(count, originalRequester) =>
        val newTotalWordCount = count + totalWordCount
        originalRequester ! TaskReport(newTotalWordCount)
        context.become(registerWorkers(workerActor, newTotalWordCount))
    }
  }
  class Worker extends Actor {
    import Master._
    override def receive: Receive = {
      case TaskToWorker(text, master) =>
        val totalCount = text.split(" ").length
        sender() ! TaskCompleted(totalCount, master)
    }
  }

}
