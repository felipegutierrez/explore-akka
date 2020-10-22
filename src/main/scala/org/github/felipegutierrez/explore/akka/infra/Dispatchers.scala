package org.github.felipegutierrez.explore.akka.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Dispatchers extends App {

  run()

  def run() = {
    val system = ActorSystem("dispatchersDemo") // ConfigFactory.load().getConfig("dispatchersDemo")

    val actors = for (i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$i")
    val r = new Random()
    for (i <- 1 to 100) {
      actors(r.nextInt(10)) ! s"message $i"
    }

    // from config: my_actor_path
    // val my_actor_path = system.actorOf(Props[Counter], "my_actor_path")
    val my_actors = for (i <- 1 to 2) yield system.actorOf(Props[Counter], s"my_actor_path_$i")
    for (i <- 1 to 100) {
      my_actors(r.nextInt(2)) ! s"message from dispatcher at application.conf $i"
    }

    // this is not good because there is a Future inside the actor message handler which makes the thread hang to receive another message
//    val dbActor = system.actorOf(Props[DBActor])
//    dbActor ! "sending an insert and update to the data base"
//    val nonBlockingActor = system.actorOf(Props[Counter])
//    for (i <- 1 to 1000) {
//      val message = s"important message $i"
//      dbActor ! message
//      nonBlockingActor ! message
//    }

    // solution 1 avoid threads waiting for another actor
    val dbActorSolution1 = system.actorOf(Props[DBActorSolution1])
    val nonBlockingSolution1Actor = system.actorOf(Props[Counter])
    for (i <- 1 to 1000) {
      val message = s"important message $i"
      dbActorSolution1 ! message
      nonBlockingSolution1Actor ! message
    }
  }

  class Counter extends Actor with ActorLogging {
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"[$count] $message")
    }
  }

  class DBActor extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext = context.dispatcher

    override def receive: Receive = {
      case message => Future {
        // using Future inside Actors is strongly discoraged
        Thread.sleep(5000)
        log.info(s"success: $message")
      }
    }
  }

  class DBActorSolution1 extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("my-dispatcher")

    override def receive: Receive = {
      case message => Future {
        Thread.sleep(5000)
        log.info(s"success: $message")
      }
    }
  }

}
