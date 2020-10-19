package org.github.felipegutierrez.explore.akka

import java.util.Scanner

import org.github.felipegutierrez.explore.akka.actors._
import org.github.felipegutierrez.explore.akka.recap.{AdvancedRecap, BasicRecap, MultiThreadRecap}

object MainClass extends App {
  println(s"0 - out")
  println(s"1 - BasicRecap")
  println(s"2 - AdvancedRecap")
  println(s"3 - MultiThreadRecap")
  println(s"4 - Simple actor")
  println(s"5 - ActorsIntro")
  println(s"6 - ActorsCapabilities")
  println(s"7 - BankAccountActor")
  println(s"8 - ChangingActorBehavior")
  println(s"9 - CounterActor")
  println(s"10 - CounterChangeBehaviorActor")
  println(s"11 - VotingSystemStateful")
  println(s"12 - VotingSystemStateless")
  println(s"13 - ChildActors")
  println(s"14 - ChildActorsNaiveBank")
  println(s"15 - ")
  println(s"16 - ")
  println(s"17 - ")
  println(s"18 - ")

  println("choose an application: ")
  val scanner = new Scanner(System.in)
  val option = scanner.nextLine()
  println(s"you chose the option $option")
  option match {
    case "0" => println(s"Bye, see you next time.")
    case "1" => BasicRecap.run()
    case "2" => AdvancedRecap.run()
    case "3" => MultiThreadRecap.run()
    case "4" => Playground.run()
    case "5" => ActorsIntro.run()
    case "6" => ActorCapabilities.run()
    case "7" => BankAccountActor.run()
    case "8" => ChangingActorBehavior.run()
    case "9" => CounterActor.run()
    case "10" => CounterChangeBehaviorActor.run()
    case "11" => VotingSystemStateful.run()
    case "12" => VotingSystemStateless.run()
    case "13" => ChildActors.run()
    case "14" => ChildActorsNaiveBank.run()
    case _ => println("option unavailable.")
  }
}
