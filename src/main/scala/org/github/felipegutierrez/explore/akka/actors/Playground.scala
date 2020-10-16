package org.github.felipegutierrez.explore.akka.actors

import akka.actor.ActorSystem

object Playground extends App {
  def run() = {
    val actorSystem = ActorSystem("HelloAkka")
    println(actorSystem.name)
  }
}
