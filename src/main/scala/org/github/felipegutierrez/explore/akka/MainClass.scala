package org.github.felipegutierrez.explore.akka

import java.util.Scanner

import org.github.felipegutierrez.explore.akka.actors.{ActorCapabilities, ActorsIntro, BankAccountActor, Playground}
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
  println(s"8 - ")
  println(s"9 - ")

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
    case _ => println("option unavailable.")
  }
}
