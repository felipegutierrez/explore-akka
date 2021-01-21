package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorSystem, Props}

import scala.util.Random

object BreakingAkkaConcurrency {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run(): Unit = {
    val actorSystem = ActorSystem("BreakingAkkaConcurrency")
    val unsecureActor = actorSystem.actorOf(Props[UnsecureActor], "unsecureActor")
    unsecureActor ! "1"
    unsecureActor ! "2"
    unsecureActor ! "3"
    unsecureActor ! "4"
    unsecureActor ! "5"
    unsecureActor ! "6"
    unsecureActor ! "7"
    unsecureActor ! "8"
    unsecureActor ! "9"
    unsecureActor ! "10"

    Thread.sleep(10000)
  }
}

class UnsecureActor extends Actor {
  def methodWithThread(): Unit = {
  }

  override def receive: Receive = {
    case msg: String =>
      // println(s"message: $msg")
      var t = new ThreadExample(msg)
      // by starting a new thread inside an Akka actor you break the synchronous pattern provided by Akka
      t.start()
  }
}

class ThreadExample(id: String) extends Thread {
  override def run() {
    // println(s"Thread $id is running...");
    // simulate a random computation which will potentially break the order of messages processed by Akka actors
    Thread.sleep(Random.nextInt(10) * 1000)
    println(s"finished $id");
  }
}
