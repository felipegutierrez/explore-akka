package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLogging extends App {

  run()

  def run() = {
    val system = ActorSystem("ActorLogging")
    val actorExplicitLogger = system.actorOf(Props[SimpleActorWithExplicitLogger])
    val actorLogger = system.actorOf(Props[SimpleActorWithLogger])
    actorExplicitLogger ! "logging a simple message"
    actorLogger ! "logging a simple message by extending a trait"
    actorLogger ! ("42", "585")
    actorLogger ! ("DEBUG", "this is a debug message")

  }

  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => {
        /*
         * log the message
         * 1 - DEBUG
         * 2 - INFO
         * 3 - WARNING/WARN
         * 4 - ERROR
         */
        logger.info(message.toString)
      }
    }
  }

  class SimpleActorWithLogger extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
      case (level, message) => log.info("level: {} - message: {}", level, message)
    }
  }

}
