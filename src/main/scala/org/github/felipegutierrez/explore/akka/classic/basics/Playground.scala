package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.ActorSystem

object Playground {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val actorSystem = ActorSystem("HelloAkka")
    println(actorSystem.name)
  }
}
