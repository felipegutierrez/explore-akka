package org.github.felipegutierrez.explore.akka.remote

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object RemoteActor extends App {

  run()

  def run() = {
    val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("remote/remoteActors.conf").getConfig("remoteSystem"))
    val remoteSimpleActor = remoteSystem.actorOf(Props[SimpleActor], "remoteSimpleActor")
    remoteSimpleActor ! "hello REMOTE actor"
  }
}
