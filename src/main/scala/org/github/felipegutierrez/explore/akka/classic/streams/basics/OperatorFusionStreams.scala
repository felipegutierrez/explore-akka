package org.github.felipegutierrez.explore.akka.classic.streams.basics

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusionStreams extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("MaterializingStreams")

    // replaces the import scala.concurrent.ExecutionContext.Implicits.global
    // import system.dispatcher

    val source = Source(1 to 1000)
    val simpleFlow1 = Flow[Int].map(_ + 1)
    val simpleFlow2 = Flow[Int].map(_ + 10)
    val simpleSink = Sink.foreach[Int](println)

    // this runs on the SAME ACTOR. then it is called operator FUSION
    source.via(simpleFlow1).via(simpleFlow2).to(simpleSink).run()

    // "equivalent" behavior
    class SimpleActor extends Actor {
      override def receive: Receive = {
        case x: Int =>
          // flow operations
          val x2 = x + 1
          val y = x2 * 10
          // sink operation
          println(y)
      }
    }
    val simpleActor = system.actorOf(Props[SimpleActor])
    //  (1 to 1000).foreach(simpleActor ! _)

    val complexFlow1 = Flow[Int].map { x =>
      // (1) it is computed in the same actor of (2)
      Thread.sleep(1000)
      x + 1
    }
    val complexFlow2 = Flow[Int].map { x =>
      // (2) it is computed in the same actor of (1)
      Thread.sleep(1000)
      x + 10
    }
    // source.via(complexFlow1).via(complexFlow2).to(simpleSink).run()

    // Async boundary. Disable the fusion of operators
    source
      .via(complexFlow1).async
      .via(complexFlow2).async
      .to(simpleSink).run()

    // ordering guarantees.
    // Using Async boundary does not preserve ordering guarantees between operators
    // but only inside the same operator.
    Source(1 to 5)
      .map(value => {
        println(s"Flow A $value");
        value
      }).async
      .map(value => {
        println(s"Flow B $value");
        value
      }).async
      .map(value => {
        println(s"Flow C $value");
        value
      }).async
      .runWith(Sink.ignore)
  }
}
