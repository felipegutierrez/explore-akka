package org.github.explore.akka

import akka.actor.ActorSystem

object Playground extends App {
  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
