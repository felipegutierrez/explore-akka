package org.github.felipegutierrez.explore.akka.recap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AdvancedTypeClassMagnetPattern extends App {

  run()

  class P2PRequest

  class P2PResponse

  class Serializer[T]

  /** overloading a lot of methods */
  trait Actor {
    def receive(statusCode: Int): Int

    def receive(request: P2PRequest): Int

    def receive(response: P2PResponse): Int

    def receive[T: Serializer](message: T): Int

    def receive[T: Serializer](message: T, statusCode: Int): Int

    def receive(future: Future[P2PRequest]): Int

    // def receive(future: Future[P2PResponse]): Int // does not compile because generics are not seen in compile time
  }

  /** the Magnet patter avoids overloaded methods aby using implicit classes */
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  class MessageMagnetImpl {
    def receive[R](magnet: MessageMagnet[R]): R = magnet.apply()
  }

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("Handling P2P responset")
      24
    }
  }

  /** no more type erasure problems */
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("Handling P2P Future Response")
      11
    }
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("Handling P2P Future Request")
      22
    }
  }

  def run() = {
    val msgMagnet = new MessageMagnetImpl
    println(msgMagnet.receive(new P2PRequest))
    println(msgMagnet.receive(new P2PResponse))
    // overloading implicit classes with Future and generics
    println(msgMagnet.receive(Future(new P2PRequest())))
    println(msgMagnet.receive(Future(new P2PResponse())))
  }
}
