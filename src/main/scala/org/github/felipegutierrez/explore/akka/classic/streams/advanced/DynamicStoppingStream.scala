package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream.KillSwitches
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object DynamicStoppingStream {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val configString =
      """
        | akka {
        |   loglevel = "DEBUG"
        | }
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("DynamicStoppingStream", ConfigFactory.load(config))
    import system.dispatcher

    val killSwitchFlow = KillSwitches.single[Int]
    val counter = Source(Stream.from(1)).throttle(1, 1 second).log("counter")
    val sink = Sink.ignore

    //    val killSwitch = counter
    //      .viaMat(killSwitchFlow)(Keep.right)
    //      .map(i => {
    //        system.log.info(s"Start task $i")
    //        Thread.sleep(100)
    //        system.log.info(s"End task $i")
    //        i
    //      })
    //      .to(sink)
    //      .run()
    //    system.scheduler.scheduleOnce(3 seconds) {
    //      killSwitch.shutdown()
    //    }

    // shared kill switch
    val anotherCounter = Source(Stream.from(1)).throttle(2, 1 second).log("anotherCounter")
    val sharedKillSwitch = KillSwitches.shared("oneButtonToRuleThemAll")

    counter.via(sharedKillSwitch.flow).runWith(Sink.ignore)
    anotherCounter.via(sharedKillSwitch.flow).runWith(Sink.ignore)
    system.scheduler.scheduleOnce(3 seconds) {
      sharedKillSwitch.shutdown()
    }
  }
}
