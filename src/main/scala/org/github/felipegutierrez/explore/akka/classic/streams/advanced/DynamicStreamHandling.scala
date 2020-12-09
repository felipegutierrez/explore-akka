package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream.KillSwitches
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object DynamicStreamHandling {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val configString =
      """
        | akka {
        |   loglevel = "DEBUG"
        | }
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("DynamicStreamHandling", ConfigFactory.load(config))
    import system.dispatcher

    val killSwitchFlow = KillSwitches.single[Int]
    val counter = Source(Stream.from(1)).throttle(1, 1 second).log("counter")
    val sink = Sink.ignore


    val killSwitch = counter
      .viaMat(killSwitchFlow)(Keep.right)
      .to(sink)
      .run()
    system.scheduler.scheduleOnce(3 seconds) {
      killSwitch.shutdown()
    }
  }
}
