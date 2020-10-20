package org.github.felipegutierrez.explore.akka.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, TestActorRef, TestProbe}
import org.github.felipegutierrez.explore.akka.actors.CounterActor.Counter
import org.github.felipegutierrez.explore.akka.actors.CounterActor.Counter._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.Duration

class CounterActorSpec extends AnyWordSpecLike with BeforeAndAfterAll {
  implicit val system = ActorSystem("SynchronousCounterActorSpec")

  override def afterAll(): Unit = {
    system.terminate()
  }

  "A counter" should {
    "synchronously increase its counter" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter ! Increment // counter has ALREADY received the message

      // TestActorRef can invoke properties underlying the Actor
      assert(counter.underlyingActor.count == 1)
    }
    "synchronously increase its counter at the call of the receive function" in {
      val counter = TestActorRef[Counter](Props[Counter])
      // instead of sending a message we call receive directly
      counter.receive(Increment)
      assert(counter.underlyingActor.count == 1)
    }
    "work on the calling thread dispatcher" in {
      // makes the actor run in synchronous manner
      val counter = system.actorOf(Props[Counter].withDispatcher(CallingThreadDispatcher.Id))

      // makes the actor run in asynchronous manner. probe.send will take some time and the test will fail
      // val counter = system.actorOf(Props[Counter])
      
      val probe = TestProbe()

      probe.send(counter, Print)
      probe.expectMsg(Duration.Zero, 0) // probe has ALREADY received the message 0
    }
  }
}
