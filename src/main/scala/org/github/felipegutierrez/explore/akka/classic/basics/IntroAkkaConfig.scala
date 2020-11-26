package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  run()

  def run() = {
    // inline configuration, try with INFO, DEBUG, ERROR, WARNING
    val configString =
      """
        | akka {
        |   loglevel = "INFO"
        | }
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    val system = ActorSystem("ConfigDemo", ConfigFactory.load(config))
    val actor = system.actorOf(Props[SimpleActorWithLogger])
    actor ! "a message to remember"

    // from the application.conf
    val anotherSystem = ActorSystem("AnotherSystem")
    val anotherActor = anotherSystem.actorOf(Props[SimpleActorWithLogger])
    anotherActor ! "another message to remember"

    // from a special configuration at application.conf
    val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
    val specialSystem = ActorSystem("SpecialSystem", specialConfig)
    val specialActor = specialSystem.actorOf(Props[SimpleActorWithLogger])
    specialActor ! "special message to remember because sometimes it is necessary to be different"

    // secret nested configuration from secret/secretConfig.conf
    val secretConfig = ConfigFactory.load("secret/secretConfig.conf")
    println(s"secret config log level: ${secretConfig.getString("akka.loglevel")}")

    // different file formats: JSON or properties
    val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
    println(s"JSON config: ${jsonConfig.getString("aJsonProperty")}")
    println(s"JSON config: ${jsonConfig.getString("akka.loglevel")}")
    val propsConfig = ConfigFactory.load("props/propsConfig.properties")
    println(s"PROPERTIES config: ${propsConfig.getString("my.simpleProperty")}")
    println(s"PROPERTIES config: ${propsConfig.getString("akka.loglevel")}")
  }

  class SimpleActorWithLogger extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
      // case (level, message) => log.info("level: {} - message: {}", level, message)
    }
  }

}
