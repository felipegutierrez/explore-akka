package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

object StreamOpenGraphWithSubStream {
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
    implicit val system = ActorSystem("StreamOpenGraphWithSubStream", ConfigFactory.load(config))
    import system.dispatcher

    // 1 -
    val wordSource = Source(List("akka", "is", "awesome", "learning", "stream", "substream", "amazing", "scala"))
    val groupBy = wordSource.groupBy(30, word => if (word.isEmpty) '\0' else word.toLowerCase.charAt(0))
    groupBy.to(Sink.fold(0)((count, word) => {
      val newCount = count + 1
      println(s"received word [$word] count = $newCount")
      newCount
    }))
      .run()

    // 2 - merge substreams
    val textSource = Source(List(
      "I love Akka streams", // odd
      "this has even characters", // even
      "this is amazing", // odd
      "learning Akka at the Rock the JVM", // odd
      "Let's rock the JVM", // even
      "123", // odd
      "1234" // even
    ))
    val totalCharCountFuture = textSource
      .groupBy(2, string => string.length % 2)
      .map { c =>
        println(s"I am running on thread [${Thread.currentThread().getId}]")
        c.length
      }.async // this operator runs in parallel
      .mergeSubstreamsWithParallelism(2)
      .toMat(Sink.reduce[Int](_ + _))(Keep.right)
      .run()
    totalCharCountFuture.onComplete {
      case Success(value) => println(s"total char count: $value")
      case Failure(exception) => println(s"failed computation: $exception")
    }

    // 3 - splitting a stream into substreams, when a condition is met
    val text =
      "I love Akka streams\n" + // odd
        "this has even characters\n" + // even
        "this is amazing\n" + // odd
        "learning Akka at the Rock the JVM\n" + // odd
        "Let's rock the JVM\n" + // even
        "123\n" + // odd
        "1234\n" // even
    val anotherCharCountFuture = Source(text.toList)
      .splitWhen(c => c == '\n')
      .filter(_ != '\n')
      .map { v =>
        println(s"alternative - I am running on thread [${Thread.currentThread().getId}]")
        1
      }.async // run in parallel
      .mergeSubstreams
      .toMat(Sink.reduce[Int](_ + _))(Keep.right)
      .run()
    anotherCharCountFuture.onComplete {
      case Success(value) => println(s"Total char count alternative: $value")
      case Failure(ex) => println(s"Char computation failed: $ex")
    }
  }
}
