package org.github.felipegutierrez.explore.akka.classic.streams.basics

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

object FirstStreamPrinciples {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    implicit val system = ActorSystem("FirstStreamPrinciples")
    // Since Akka 2.6.10 ActorMaterializer becames Materializer and it is not necessary to declare it on the code
    // implicit val materializer = Materializer

    // Sources
    val source = Source(1 to 10)
    // Sinks
    val sink = Sink.foreach[Int](println)

    // DAG
    val graph = source.to(sink)
    graph.run()

    // Flows
    val flow = Flow[Int].map(x => x * 10)
    val sourceWithFlow = source.via(flow)
    // the new DAG
    val graphWithFlow = sourceWithFlow.to(sink)
    graphWithFlow.run()

    // or chain all operators
    source
      .via(flow)
      .to(sink)
      .run()

    // Null's are not allowed
    // val illegalSource = Source.single(null)
    // illegalSource.to(Sink.foreach(println)).run()

    val finiteSource = Source.single(1)
    val anotherFiniteSource = Source[Int](List(1, 2, 3, 4, 5, 6))
    val emptySource = Source.empty[Int]
    // using infinite Stream from Scala to emit ints from 1 to infinity
    val infiniteSource = Source(Stream.from(1))

    // Futures
    import scala.concurrent.ExecutionContext.Implicits.global
    val futureSource = Source.future(Future(42))
    futureSource.via(flow).to(sink).run()

    // Sinks
    val theMostBoringSink = Sink.ignore
    val foreachSink = Sink.foreach[Int](println)
    val headSink = Sink.head[Int] // retrieves the head and then close the stream
    val foldSink = Sink.fold[Int, Int](0)((a, b) => a + b)

    // Flows - usually mapped to collection operators
    val mapFlow = Flow[Int].map(x => x * 2)
    val takeFlow = Flow[Int].take(5)
    val dropFlow = Flow[Int].drop(4)
    val filterFlow = Flow[Int].filter(x => x > 3)
    // there is NO flatmap in Akka Stream
    val listSource = Source(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
    listSource
      .via(filterFlow)
      .to(foreachSink)
      .run()

    val aList = List("Felipe", "Daniel", "Bob", "Fabio", "Alice", "Peter", "John")
    val dagWithFilter = Source[String](aList)
      .via(Flow[String].filter(v => v.length > 5))
      .via(Flow[String].take(2))
      .to(Sink.foreach[String](println))
    dagWithFilter.run()
  }
}
