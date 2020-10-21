package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.{ActorSystem, Props}
import akka.pattern.{BackoffOpts, BackoffSupervisor}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

class BackoffSupervisorPatternSpec
  extends TestKit(ActorSystem("BackoffSupervisorPatternSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import BackoffSupervisorPattern._

  val minBackOff: Int = 3
  val maxBackOff: Int = 20
  "A backoff supervisor" should {
    s"start again in ${minBackOff} seconds, ${minBackOff * 2} seconds, and ${minBackOff * 2 * 2} seconds in case of failure until ${maxBackOff} seconds" in {
      EventFilter.info(pattern = s"Persistent actor starting", occurrences = 2) intercept {
        val simpleSupervisorProps = BackoffSupervisor.props(
          BackoffOpts.onFailure(
            Props[FileBasedPersistentActor],
            "simpleBackoffActor",
            minBackOff seconds, // 3s, then 6s, 12s
            maxBackOff seconds, // until 20s
            0.2
          )
        )
        val simpleBackoffSupervisor = system.actorOf(simpleSupervisorProps, "simpleSupervisor")
        simpleBackoffSupervisor ! FileBasedPersistentActor.ReadFile("src/main/resources/testfiles/important_data_not_found.txt")
        Thread.sleep((minBackOff + 1) * 1000)
      }
    }
  }
}
