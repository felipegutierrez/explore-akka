package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object DynamicMergingStream {
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
    implicit val system = ActorSystem("DynamicMergingStream", ConfigFactory.load(config))

    // MergeHub
    val counter = Source(Stream.from(1)).throttle(1, 1 second).log("counter")
    val dynamicMerge = MergeHub.source[Int]
    val materializedSink = dynamicMerge.to(Sink.foreach[Int](println)).run()

    // use this sink any time we like
    Source(1 to 10).runWith(materializedSink)
    counter.runWith(materializedSink)

    // BroadcastHub
    val dynamicBroadcast = BroadcastHub.sink[Int]
    val materializedSource = Source(1 to 100).runWith(dynamicBroadcast)
    materializedSource.runWith(Sink.ignore)
    materializedSource.runWith(Sink.foreach[Int](println))

    /**
     * Challenge - combine a mergeHub and a broadcastHub.
     *
     * A publisher-subscriber component
     */
    val mergeHub = MergeHub.source[String]
    val broadcastHub = BroadcastHub.sink[String]
    val (publisherPort, subscriberPort) = mergeHub.toMat(broadcastHub)(Keep.both).run()

    subscriberPort.runWith(Sink.foreach(e => println(s"I received: $e")))
    subscriberPort.map(string => string.length).runWith(Sink.foreach(n => println(s"I got a number: $n")))

    Source(List("Akka", "is", "amazing")).runWith(publisherPort)
    Source(List("I", "love", "Scala")).runWith(publisherPort)
    Source.single("STREEEEEEAMS").runWith(publisherPort)
  }
}
