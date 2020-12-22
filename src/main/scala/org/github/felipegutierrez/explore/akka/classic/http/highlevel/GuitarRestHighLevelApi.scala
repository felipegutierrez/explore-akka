package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
import org.github.felipegutierrez.explore.akka.classic.http.lowlevel.GuitarRestApi.run

object GuitarRestHighLevelApi {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    println("http GET localhost:8080/api/guitar")
    println("http GET localhost:8080/api/guitar?id=2")
    println("http GET localhost:8080/api/guitar?id=10")
    println("http GET localhost:8080/api/guitar/inventory?inStock=false|true")
    println("http POST localhost:8080/api/guitar < src/main/resources/json/guitar.json")
    println("http POST \"localhost:8080/api/guitar/inventory?id=1&quantity=3\"")
    implicit val system = ActorSystem("GuitarRestHighLevelApi")
    import system.dispatcher
  }

}
