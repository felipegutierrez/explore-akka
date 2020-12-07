package org.github.felipegutierrez.explore.akka.classic.streams.techniques

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.testkit.TestProbe
import akka.util.Timeout

import scala.concurrent.duration._

object StreamIntegrationWithActors extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("StreamIntegrationWithActors")
    val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

    val numbersSource = Source(1 to 10)

    // actor as a flow
    implicit val timeout = Timeout(2 seconds)
    val actorBasedFlow = Flow[Int].ask[Int](parallelism = 4)(simpleActor)

    // numbersSource.via(actorBasedFlow).to(Sink.foreach[Int](println)).run()

    /** Actor as a source */
    // val actorPoweredSource = Source.actorRef[Int](bufferSize = 3, overflowStrategy = OverflowStrategy.dropHead)
    val actorPoweredSource = Source.actorRef(
      completionMatcher = {
        case Done =>
          println("complete stream immediately if we send it Done")
          CompletionStrategy.immediately
      },
      // never fail the stream because of a message
      failureMatcher = PartialFunction.empty,
      bufferSize = 3,
      overflowStrategy = OverflowStrategy.dropHead
    )

    val materializedActorRef = actorPoweredSource.to(Sink.foreach[Int](number => println(s"Actor powered flow got number: $number"))).run()
    materializedActorRef ! 10
    materializedActorRef ! 11
    materializedActorRef ! 12
    materializedActorRef ! 13
    materializedActorRef ! 14
    materializedActorRef ! Done // terminating the stream
    materializedActorRef ! 15

    /*
     Actor as a destination/sink
     - an init message
     - an ack message to confirm the reception
     - a complete message
     - a function to generate a message in case the stream throws an exception
    */

    import DestinationDomain._
    val probe = TestProbe()
    val destinationActor = system.actorOf(Props(new DestinationActor(probe.ref, ackWith = StreamAck)))
    val actorPoweredSink = Sink.actorRefWithBackpressure(
      destinationActor,
      onInitMessage = StreamInit,
      ackMessage = StreamAck,
      onCompleteMessage = StreamComplete,
      onFailureMessage = throwable => StreamFail(throwable)
    )

    Source(1 to 10).to(actorPoweredSink).run()

    //  Sink.actorRef() not recommended, unable to backpressure
  }

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case s: String =>
        log.info(s"Just received a string: $s")
        sender() ! s"$s$s"
      case n: Int =>
        log.info(s"Just received a number: $n")
        sender() ! (2 * n)
      case _ =>
    }
  }

  class DestinationActor(probe: ActorRef, ackWith: Any) extends Actor with ActorLogging {

    import DestinationDomain._

    override def receive: Receive = {
      case StreamInit =>
        log.info("Stream initialized")
        sender() ! StreamAck // ack to allow the stream to proceed sending more elements
      case StreamComplete =>
        log.info("Stream complete")
        context.stop(self)
      case StreamFail(ex) =>
        log.warning(s"Stream failed: $ex")
      case message =>
        log.info(s"Message $message has come to its final resting point.")
        sender() ! StreamAck // ack to allow the stream to proceed sending more elements
    }
  }

  object DestinationDomain {

    case class StreamFail(ex: Throwable)

    case object StreamInit

    case object StreamAck

    case object StreamComplete

  }

}
