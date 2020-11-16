package org.github.felipegutierrez.explore.akka.remote.deployment

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object RemoteDeployment extends App {

  run()

  def run() = {
    val system = ActorSystem("RemoteActorSystem", ConfigFactory.load("remote/deployingActorsRemotely.conf").getConfig("remoteApp"))
  }

}
