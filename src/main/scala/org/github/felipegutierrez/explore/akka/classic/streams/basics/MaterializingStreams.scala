package org.github.felipegutierrez.explore.akka.classic.streams.basics

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}

object MaterializingStreams extends App {

  run()

  def run() = {
    implicit val system = ActorSystem("MaterializingStreams")

    // replaces the import scala.concurrent.ExecutionContext.Implicits.global
    import system.dispatcher

    val source = Source(1 to 10)
    val sink = Sink.reduce[Int]((a, b) => a + b)
    val sumFuture = source.runWith(sink)
    sumFuture.onComplete {
      case Success(value) => println(s"The sum of all elements is: $value")
      case Failure(ex) => println(s"The sum of the elements failed because: $ex")
    }

    // Choosing materialized values
    val simpleSource = Source(1 to 10)
    val simpleFlow = Flow[Int].map(x => x + 1)
    val simpleSink = Sink.foreach[Int](println)
    val graph = simpleSource
      .viaMat(simpleFlow)(Keep.right)
      .toMat(simpleSink)(Keep.right)
    graph.run().onComplete {
      case Success(value) => println(s"stream processing finished: $value")
      case Failure(exception) => println(s"Stream processing failed because: $exception")
    }

    // Syntax sugar
    Source(1 to 10).to(Sink.reduce[Int](_ + _))
    Source(20 to 30).runWith(Sink.reduce[Int](_ + _))
    Source(10 to 20).runReduce(_ + _)

    // backwards
    Sink.foreach[Int](println).runWith(Source.single(42))
    // both ways
    Flow[Int].map(x => 2 + x).runWith(simpleSource, simpleSink)

    // word count
    val aListOfSentences = List("Felipe Oliveira Gutierrez",
      "Felipe Oliveira Gutierrez learning Akka",
      "Felipe Oliveira Gutierrez studying Akka",
      "Felipe Oliveira Gutierrez doing stream processing with Akka",
      "It is cold today",
      "today I will finish one more experiment")
    val sourceWords = Source[String](aListOfSentences)
    val sinkWordCount = Sink.fold[Int, String](0)((currentWords, newSentence) => currentWords + newSentence.split(" ").length)
    val g1 = sourceWords.toMat(sinkWordCount)(Keep.right).run()
    g1.onComplete {
      case Success(value) => println(s"final word count result: $value")
      case Failure(exception) => println(s"word count failed: $exception")
    }

    val g2 = sourceWords.runWith(sinkWordCount)
    val g3 = sourceWords.runFold(0)((currentWords, newSentence) => currentWords + newSentence.split(" ").length)

    val wordCountFlow = Flow[String].fold[Int](0)((currentWords, newSentence) => currentWords + newSentence.split(" ").length)
    val g4 = sourceWords.via(wordCountFlow).toMat(Sink.head)(Keep.right).run()
    val g5 = sourceWords.viaMat(wordCountFlow)(Keep.left).toMat(Sink.head)(Keep.right).run()
    val g6 = sourceWords.via(wordCountFlow).runWith(Sink.head)
    val g7 = wordCountFlow.runWith(sourceWords, Sink.head)._2
  }

}
