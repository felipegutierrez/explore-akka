package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{BidiShape, ClosedShape}

object StreamOpenGraphsBidirectionalFlow {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphsBidirectionalFlow")

    val words = List("akka", "is", "awesome", "testing", "bidirectional", "flows")
    val source = Source(words)
    val sourceEncrypted = Source(words.map(encrypt(5)))
    val bidirectionalFlow = myCriptoBidirectionFlow()
    val encryptedSink = Sink.foreach[String](s => println(s"encrypted: $s"))
    val decryptedSink = Sink.foreach[String](s => println(s"decrypted: $s"))

    val cryptoBidiGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val unencryptedSourceShape = builder.add(source)
        val encryptedSourceShape = builder.add(sourceEncrypted)
        val bidiShape = builder.add(bidirectionalFlow)
        val encryptedSinkShape = builder.add(encryptedSink)
        val decryptedSinkShape = builder.add(decryptedSink)

        unencryptedSourceShape ~> bidiShape.in1
        bidiShape.out1 ~> encryptedSinkShape
        encryptedSourceShape ~> bidiShape.in2
        bidiShape.out2 ~> decryptedSinkShape

        ClosedShape
      }
    )

    cryptoBidiGraph.run()
  }

  def myCriptoBidirectionFlow() = {
    GraphDSL.create() { implicit builder =>
      val encryptionFlowShape = builder.add(Flow[String].map(word => encrypt(5)(word)))
      val decryptionFlowShape = builder.add(Flow[String].map(word => decrypt(5)(word)))

      // BidiShape(encryptionFlowShape.in, encryptionFlowShape.out, decryptionFlowShape.in, decryptionFlowShape.out)
      // or use the BidiShape
      BidiShape.fromFlows(encryptionFlowShape, decryptionFlowShape)
    }
  }

  /** cryptography functions */
  def encrypt(offset: Int)(value: String) = value.map(c => (c + offset).toChar)

  def decrypt(offset: Int)(value: String) = value.map(c => (c - offset).toChar)
}
