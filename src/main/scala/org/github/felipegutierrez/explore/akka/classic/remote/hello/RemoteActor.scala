package org.github.felipegutierrez.explore.akka.classic.remote.hello

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object RemoteActor {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("remote/remoteActors.conf").getConfig("remoteSystem"))
    val remoteSimpleActor = remoteSystem.actorOf(Props[SimpleActor], "remoteSimpleActor")
    remoteSimpleActor ! "hello REMOTE actor"
  }
}
