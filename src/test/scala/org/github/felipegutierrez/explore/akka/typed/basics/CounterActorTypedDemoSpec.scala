package org.github.felipegutierrez.explore.akka.typed.basics

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CounterActorTypedDemoSpec extends AnyWordSpec
  with BeforeAndAfterAll
  with Matchers {

  import Counter._

  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "when sending incrementing, decrementing, and print messages to the counter actor, the actor" should {
    "increment, decrement, and print the counter" in {
      val counterActor = testKit.createTestProbe[Counter.CounterMsg]("CounterActorTypedSpec")

      counterActor ! Increment
      counterActor ! Increment
      counterActor ! Decrement
      counterActor ! Increment
      counterActor ! Print

      counterActor.expectMessage(Increment)
      counterActor.expectMessage(Increment)
      counterActor.expectMessage(Decrement)
      counterActor.expectMessage(Increment)
      counterActor.expectMessage(Print)
    }
  }
}
