package org.github.felipegutierrez.explore.akka.classic.streams.techniques

import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}

import java.util.Date

object StreamBackpressure {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("StreamBackpressure")

    // control backpresure
    val controlledFlow = Flow[Int].map(_ * 2).buffer(10, OverflowStrategy.dropHead)

    case class PagerEvent(description: String, date: Date, nInstances: Int = 1)
    case class Notification(email: String, pagerEvent: PagerEvent)

    val events = List(
      PagerEvent("Service discovery failed", new Date),
      PagerEvent("Illegal elements in the data pipeline", new Date),
      PagerEvent("Number of HTTP 500 spiked", new Date),
      PagerEvent("A service stopped responding", new Date)
    )

    val eventSource = Source(events)

    val oncallEngineer = "felipe@rockthejvm.com" // a fast service for fetching on call emails

    def sendEmail(notification: Notification) = {
      println(s"dear ${notification.email}, you have and event: ${notification.pagerEvent}")
    }

    def sendEmailSlow(notification: Notification) = {
      Thread.sleep(1000)
      println(s"dear ${notification.email}, you have and event: ${notification.pagerEvent}")
    }

    /** Conflate is an alternative for backpressure. It pre aggregates events as a reduce style. */
    val aggregateNotificationFlow = Flow[PagerEvent]
      .conflate((event1, event2) => {
        val nInstances = event1.nInstances + event2.nInstances
        PagerEvent(s"You have $nInstances events that require your attention", new Date, nInstances)
      })
      .map(resultingEvent => Notification(oncallEngineer, resultingEvent))

    val notificationSink = Flow[PagerEvent].map(event => Notification(oncallEngineer, event))
      .to(Sink.foreach[Notification](notification => sendEmail(notification)))

    // eventSource.to(notificationSink).run()
    eventSource
      .via(aggregateNotificationFlow).async
      .to(Sink.foreach[Notification](notification => sendEmailSlow(notification)))
      .run()

    /** Slow producers: extrapolate/expand */
    import scala.concurrent.duration._
    val slowCounter = Source(Stream.from(1)).throttle(1, 1 second)
    val hungrySink = Sink.foreach[Int](println)

    val extrapolator = Flow[Int].extrapolate(element => Iterator.from(element))
    val repeater = Flow[Int].extrapolate(element => Iterator.continually(element))

    slowCounter.via(repeater).to(hungrySink).run()

    val expander = Flow[Int].expand(element => Iterator.from(element))
  }
}
