package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val actorSystem = ActorSystem("ActorsIntro")
    println(actorSystem.name)

    val worldCounter = actorSystem.actorOf(Props[WordCountActor], "WordCounter")
    val anotherWorldCounter = actorSystem.actorOf(Props[WordCountActor], "AnotherWordCounter")

    worldCounter ! "I am reviewing Akka using Scala and it is pretty damn awesome !"
    worldCounter ! "asynchronous message Akka Scala"
    anotherWorldCounter ! "asynchronous message Akka Scala"

    val person = actorSystem.actorOf(Person.props("Bob"))
    person ! "hi"
  }

  class WordCountActor extends Actor {
    var totalWords = 0

    override def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"Message received[ $message ]")
        totalWords += message.split(" ").length
        println(s"Total words counted: $totalWords")
      case msg => println(s"word count. I cannot understand ${msg.toString}")
    }
  }

  object Person {
    def props(name: String) = Props(new Person(name))

    val propsPersonActor = {
      Props(new Person(""))
    }
  }

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" =>
        val reply = s"Hi, my name is $name"
        println(reply)
        sender() ! reply
      case message => sender() ! message
    }
  }

}
