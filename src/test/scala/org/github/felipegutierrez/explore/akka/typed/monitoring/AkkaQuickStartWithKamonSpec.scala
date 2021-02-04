package org.github.felipegutierrez.explore.akka.typed.monitoring

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.github.felipegutierrez.explore.akka.typed.monitoring.GreeterMain.SayHello
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AkkaQuickStartWithKamonSpec extends AnyWordSpec
  with BeforeAndAfterAll
  with Matchers {

  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "when the bank account actor typed deposit and withdraw money" should {
    "have the right balance in the end" in {
      val greeterMain = testKit.createTestProbe[GreeterMain.SayHello]("AkkaQuickStartWithKamonSpec")

      val allPerson = List("Charles", "Bob", "Felipe", "Simone", "Fabio")

      allPerson.foreach(p => greeterMain ! SayHello(p))

      allPerson.foreach(p => greeterMain.expectMessage(SayHello(p)))
    }
  }
}
