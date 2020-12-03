package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, FanOutShape2}

import java.util.{Date, UUID}
import scala.concurrent.duration._
import scala.util.Random

object StreamOpenGraphsWithNonUniformShapes extends App {

  run()

  def run() = {
    /*
     Non-uniform fan out shape
     Processing bank transactions
     Txn suspicious if amount > 10000
     Streams component for txns
     - output1: let the transaction go through
     - output2: suspicious txn ids
    */

    val randomTxns: List[Transaction] = (1 to 10).map { x =>
      Transaction(generateTxnId(), gerRamdomAccount(), gerRamdomAccount(), getRandomAmmount(), new Date)
    }.toList

    val transactionSource = Source(randomTxns).throttle(1, 1 second)
    val bankProcessor = Sink.foreach[Transaction](println)
    val suspiciousAnalysisService = Sink.foreach[String](txnId => println(s"Suspicious transaction ID: $txnId"))

    implicit val system = ActorSystem("StreamOpenGraphsWithNonUniformShapes")

    // Step 1 - create the GraphDSL
    val suspiciousTxnStaticGraph = GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      // Step 2 - define SHAPES
      val broadcast = builder.add(Broadcast[Transaction](2))
      val suspiciousTxnFilter = builder.add(Flow[Transaction].filter(txn => txn.amount > 10000))
      val txnIdExtractor = builder.add(Flow[Transaction].map[String](txn => txn.id))

      // Step 3 - tying up the components
      broadcast.out(0) ~> suspiciousTxnFilter ~> txnIdExtractor

      // Step 4 - return a closed shape
      new FanOutShape2(broadcast.in, broadcast.out(1), txnIdExtractor.out)
    }

    // create the runnable graph
    // Step 1
    val suspiciousTxnRunnableGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // step 2
        val suspiciousTxnShape = builder.add(suspiciousTxnStaticGraph)
        // step 3
        transactionSource ~> suspiciousTxnShape.in
        suspiciousTxnShape.out0 ~> bankProcessor
        suspiciousTxnShape.out1 ~> suspiciousAnalysisService
        // step 4
        ClosedShape
      }
    )
    suspiciousTxnRunnableGraph.run()
  }

  def gerRamdomAccount(): String = {
    val accounts = List("Paul", "Daniel", "Jim", "Alice", "John", "Felipe", "Fabio", "Simone")
    accounts(Random.nextInt(accounts.size))
  }

  def getRandomAmmount(): Int = {
    Random.nextInt(20000)
  }

  def generateTxnId() = UUID.randomUUID().toString

  case class Transaction(id: String, source: String, recipient: String, amount: Int, date: Date) {
    override def toString: String = s"id[$id] from[$source] to[$recipient] amount[$amount] date[$date]"
  }

}
