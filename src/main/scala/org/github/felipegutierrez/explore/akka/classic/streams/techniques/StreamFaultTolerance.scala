package org.github.felipegutierrez.explore.akka.classic.streams.techniques

import akka.actor.ActorSystem
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import akka.stream.{ActorAttributes, RestartSettings}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

object StreamFaultTolerance {
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
    implicit val system = ActorSystem("StreamFaultTolerance", ConfigFactory.load(config))

    // 1 - Logging
    val faultySource = Source(1 to 10).map(e => if (e == 6) throw new RuntimeException else e)
    faultySource
      .log("tracking elements")
      .to(Sink.foreach(v => println(s"value: $v")))
      .run()

    // 2 - gracefully terminating a stream
    faultySource.recover {
      case _: RuntimeException => Int.MinValue
    }.log("gracefully source")
      .to(Sink.foreach(v => println(s"value: $v")))
      .run()

    // 3 - recover with another stream
    faultySource.recoverWithRetries(3, {
      case _: RuntimeException => Source(90 to 99)
    })
      .log("recoverWithRetries")
      .to(Sink.foreach(v => println(s"value: $v")))
      .run()

    // 4 - backoff supervision
    val restartSource = RestartSource
      .onFailuresWithBackoff(RestartSettings(1 second, 30 seconds, 0.2))(() => {
        val randomNumber = new Random().nextInt(20)
        Source(1 to 10).map(elem => if (elem == randomNumber) throw new RuntimeException else elem)
      })
    restartSource
      .log("onFailuresWithBackoff")
      .to(Sink.foreach(v => println(s"value: $v")))
      .run()

    // 5 - supervision strategy
    val numbers = Source(1 to 20).map(n => if (n == 13) throw new RuntimeException("bad luck") else n).log("supervision")
    val supervisedNumbers = numbers.withAttributes(ActorAttributes.supervisionStrategy {
      /*
       Resume = skips the faulty element
       Stop = stop the stream
       Restart = resume + clears internal state
      */
      case _: RuntimeException => akka.stream.Supervision.Resume
      case _ => akka.stream.Supervision.Stop
    })
    supervisedNumbers
      .to(Sink.foreach(v => println(s"value: $v")))
      .run()
  }
}
