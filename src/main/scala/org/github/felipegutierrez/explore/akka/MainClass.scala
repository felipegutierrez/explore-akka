package org.github.felipegutierrez.explore.akka

import java.util.Scanner

import org.github.felipegutierrez.explore.akka.actors._
import org.github.felipegutierrez.explore.akka.falttolerance._
import org.github.felipegutierrez.explore.akka.infra._
import org.github.felipegutierrez.explore.akka.patterns._
import org.github.felipegutierrez.explore.akka.recap.{AdvancedPartialFunctions, AdvancedPatternMatching, AdvancedRecap, BasicRecap, MultiThreadRecap}

object MainClass extends App {
  println(s"0 - out")
  println(s"1 - BasicRecap")
  println(s"2 - AdvancedRecap")
  println(s"2.1 - AdvancedPatternMatching")
  println(s"2.2 - AdvancedPartialFunctions")
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
  println(s"15 - WordCountUsingChildActors")
  println(s"16 - ActorLogging")
  println(s"17 - IntroAkkaConfig")
  println(s"18 - StartingStoppingActors")
  println(s"19 - WatchingActors")
  println(s"20 - DefaultSupervisionStrategy")
  println(s"21 - OneForOneSupervisionStrategy")
  println(s"22 - AllForOneSupervisionStrategy")
  println(s"23 - BackoffSupervisorPattern")
  println(s"24 - TimersSchedulers")
  println(s"25 - SelfClosingScheduler")
  println(s"26 - TimerBasedHeartbeatDemo")
  println(s"27 - Routers")
  println(s"28 - Dispatchers")
  println(s"29 - Mailboxes")
  println(s"30 - StashDemo")
  println(s"31 - AskPatternDemo")
  println(s"32 - VendingMachineDemo")
  println(s"33 - VendingMachineFSMDemo")
  println(s"34 - VendingMachineTypedDemo")
  println(s"35 - ")
  println(s"36 - ")
  println(s"37 - ")
  println(s"38 - ")
  println(s"39 - ")
  println(s"40 - ")

  println("choose an application: ")
  val scanner = new Scanner(System.in)
  val option = scanner.nextLine()
  println(s"you chose the option $option")
  option match {
    case "0" => println(s"Bye, see you next time.")
    case "1" => BasicRecap.run()
    case "2" => AdvancedRecap.run()
    case "2.1" => AdvancedPatternMatching.run()
    case "2.2" => AdvancedPartialFunctions.run()
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
    case "15" => WordCountUsingChildActors.run()
    case "16" => ActorLogging.run()
    case "17" => IntroAkkaConfig.run()
    case "18" => StartingStoppingActors.run()
    case "19" => WatchingActors.run()
    case "20" => DefaultSupervisionStrategy.run()
    case "21" => OneForOneSupervisionStrategy.run()
    case "22" => AllForOneSupervisionStrategy.run()
    case "23" => BackoffSupervisorPattern.run()
    case "24" => TimersSchedulers.run()
    case "25" => SelfClosingScheduler.run()
    case "26" => TimerBasedHeartbeatDemo.run()
    case "27" => Routers.run()
    case "28" => Dispatchers.run()
    case "29" => Mailboxes.run()
    case "30" => StashDemo.run()
    case "31" => AskPatternDemo.run()
    case "32" => VendingMachineDemo.run()
    case "33" => VendingMachineFSMDemo.run()
    case "34" => VendingMachineTypedDemo.run()
    case "35" => ???
    case "36" => ???
    case "37" => ???
    case "38" => ???
    case "39" => ???
    case "40" => ???
    case "41" => ???
    case "42" => ???
    case "43" => ???
    case _ => println("option unavailable.")
  }
}
