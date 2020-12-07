package org.github.felipegutierrez.explore.akka

import org.github.felipegutierrez.explore.akka.classic.basics._
import org.github.felipegutierrez.explore.akka.classic.clustering.basic.{ClusteringBasics, ClusteringManualRegistration}
import org.github.felipegutierrez.explore.akka.classic.clustering.chat.ChatApp
import org.github.felipegutierrez.explore.akka.classic.clustering.wordcount.{ClusteringWordCount, ClusteringWordCountAdditionalWorkers}
import org.github.felipegutierrez.explore.akka.classic.falttolerance._
import org.github.felipegutierrez.explore.akka.classic.infra._
import org.github.felipegutierrez.explore.akka.classic.patterns._
import org.github.felipegutierrez.explore.akka.classic.persistence.detaching.DetachingModels
import org.github.felipegutierrez.explore.akka.classic.persistence.event_sourcing._
import org.github.felipegutierrez.explore.akka.classic.persistence.schema.EventAdapters
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.{CassandraStores, LocalStores, PostgresStores}
import org.github.felipegutierrez.explore.akka.classic.remote._
import org.github.felipegutierrez.explore.akka.classic.remote.deployment.{LocalDeployment, RemoteDeployment}
import org.github.felipegutierrez.explore.akka.classic.remote.hello.{LocalActor, RemoteActor}
import org.github.felipegutierrez.explore.akka.classic.remote.wordcount.{MasterApp, WorkerApp}
import org.github.felipegutierrez.explore.akka.classic.streams.basics.{BackpressureStreams, FirstStreamPrinciples, MaterializingStreams, OperatorFusionStreams}
import org.github.felipegutierrez.explore.akka.classic.streams.graphs._
import org.github.felipegutierrez.explore.akka.classic.streams.techniques.StreamIntegrationWithActors
import org.github.felipegutierrez.explore.akka.recap._
import org.github.felipegutierrez.explore.akka.typed.basics.{BankAccountActorTyped, CounterActorTypedDemo}
import org.github.felipegutierrez.explore.akka.typed.patterns.VendingMachineTypedDemo

import java.util.Scanner

object MainClass extends App {
  println(s"0 - out")
  println(s"1 - BasicRecap")
  println(s"2 - AdvancedRecap")
  println(s"2.1 - AdvancedPatternMatching")
  println(s"2.2 - AdvancedPartialFunctions")
  println(s"2.3 - AdvancedFunctionalCollections")
  println(s"2.4 - AdvancedCurriesPAF")
  println(s"2.5 - AdvancedLazyEvaluation")
  println(s"2.6 - AdvancedStreamLazyEvaluation")
  println(s"2.7 - AdvancedMonads")
  println(s"2.8 - AdvancedImplicits")
  println(s"2.9 - AdvancedTypeClasses")
  println(s"2.10 - AdvancedTypeClassJsonSerialization")
  println(s"2.11 - AdvancedTypeClassMagnetPattern")
  println(s"3 - MultiThreadRecap")
  println(s"3.1 - AdvancedThreads")
  println(s"3.2 - AdvancedFutures")
  println(s"3.3 - AdvancedParallelCollections")
  println(s"4 - Simple actor")
  println(s"5 - ActorsIntro")
  println(s"6 - ActorsCapabilities")
  println(s"7 - BankAccountActor")
  println(s"7.1 - BankAccountActorTyped")
  println(s"8 - ChangingActorBehavior")
  println(s"9 - CounterActor")
  println(s"9.1 - CounterActorTypedDemo")
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
  println(s"32 - VendingMachineDemo classic Akka")
  println(s"33 - VendingMachineFSMDemo using Akka Finite State Machine")
  println(s"34 - VendingMachineTypedDemo using Akka typed")
  println(s"35 - ClusteringPlayground")
  println(s"36 - RemoteActor and LocalActor communicating")
  println(s"37 - RemoteActorWordCount master and 5 workers")
  println(s"38 - RemoteDeployment and LocalDeployment of Actors")
  println(s"39 - ClusteringBasics")
  println(s"40 - ClusteringManualRegistration")
  println(s"41 - ClusteringWordCount")
  println(s"42 - Cluster chat")
  println(s"42.1 - Alice chat")
  println(s"42.2 - Bob chat")
  println(s"42.3 - Felipe chat")
  println(s"42.4 - Fabio chat")
  println(s"43 - ")
  println(s"44 - ")
  println(s"45 - ")
  println(s"46 - PersistentActors")
  println(s"47 - PersistentActorsExercise")
  println(s"48 - Snapshots")
  println(s"49 - RecoveryDemo")
  println(s"50 - PersistAsyncDemo")
  println(s"51 - LocalStores")
  println(s"52 - PostgresStores")
  println(s"53 - CassandraStores")
  println(s"54 - EventAdapters")
  println(s"55 - DetachingModels")
  println(s"56 - FirstStreamPrinciples")
  println(s"57 - MaterializingStreams")
  println(s"58 - OperatorFusionStreams")
  println(s"59 - BackpressureStreams")
  println(s"60 - StreamGraphBasics")
  println(s"61 - StreamOpenGraphs")
  println(s"62 - StreamOpenGraphsWithMultipleFlows")
  println(s"63 - StreamOpenGraphsWithNonUniformShapes")
  println(s"64 - StreamOpenGraphMaterializedValues")
  println(s"65 - StreamOpenGraphsBidirectionalFlow")
  println(s"66 - StreamOpenGraphsCycles")
  println(s"67 - StreamOpenGraphsCycles buffers")
  println(s"68 - StreamOpenGraphsCycles fibonacci")
  println(s"69 - StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies")
  println(s"70 - StreamIntegrationWithActors")
  println(s"71 - ")

  var option: String = ""
  if (args.length == 0) {
    println("choose an application: ")
    val scanner = new Scanner(System.in)
    option = scanner.nextLine()
  } else {
    option = args(0)
  }

  println(s"you chose the option $option")
  option match {
    case "0" => println(s"Bye, see you next time.")
    case "1" => BasicRecap.run()
    case "2" => AdvancedRecap.run()
    case "2.1" => AdvancedPatternMatching.run()
    case "2.2" => AdvancedPartialFunctions.run()
    case "2.3" => AdvancedFunctionalCollections.run()
    case "2.4" => AdvancedCurriesPAF.run()
    case "2.5" => AdvancedLazyEvaluation.run()
    case "2.6" => AdvancedStreamLazyEvaluation.run()
    case "2.7" => AdvancedMonads.run()
    case "2.8" => AdvancedImplicits.run()
    case "2.9" => AdvancedTypeClasses.run()
    case "2.10" => AdvancedTypeClassJsonSerialization.run()
    case "2.11" => AdvancedTypeClassMagnetPattern.run()
    case "3" => MultiThreadRecap.run()
    case "3.1" => AdvancedThreads.run()
    case "3.2" => AdvancedFutures.run()
    case "3.3" => AdvancedParallelCollections.run()
    case "4" => Playground.run()
    case "5" => ActorsIntro.run()
    case "6" => ActorCapabilities.run()
    case "7" => BankAccountActor.run()
    case "7.1" => BankAccountActorTyped.run()
    case "8" => ChangingActorBehavior.run()
    case "9" => CounterActor.run()
    case "9.1" => CounterActorTypedDemo.run()
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
    case "35" => ClusteringPlayground.run()
    case "36" =>
      RemoteActor.run()
      Thread.sleep(1000)
      LocalActor.run()
    case "37" =>
      WorkerApp.run()
      Thread.sleep(2000)
      MasterApp.run()
    case "38" =>
      RemoteDeployment.run()
      Thread.sleep(2000)
      LocalDeployment.run()
    case "39" => ClusteringBasics.run()
    case "40" =>
      ClusteringBasics.run()
      Thread.sleep(3000)
      ClusteringManualRegistration.run()
    case "41" =>
      ClusteringWordCount.run()
      Thread.sleep(5000)
      ClusteringWordCountAdditionalWorkers.run()
    case "42" => println("This is the cluster chat. You must chose 42.1, 42.2, 42.3, or 42.4")
    case "42.1" =>
      val alice = new ChatApp("Alice", 2551, true)
      alice.run()
    case "42.2" =>
      val bob = new ChatApp("Bob", 2552, true)
      bob.run()
    case "42.3" =>
      val felipe = new ChatApp("Felipe", 2553, true)
      felipe.run()
    case "42.4" =>
      val fabio = new ChatApp("Fabio", 2554, false)
      fabio.run()
    case "43" => ???
    case "44" => ???
    case "45" => ???
    case "46" => PersistentActors.run()
    case "47" => PersistentActorsExercise.run()
    case "48" => Snapshots.run()
    case "49" => RecoveryDemo.run()
    case "50" => PersistAsyncDemo.run()
    case "51" => LocalStores.run()
    case "52" => PostgresStores.run()
    case "53" => CassandraStores.run()
    case "54" => EventAdapters.run()
    case "55" => DetachingModels.run()
    case "56" => FirstStreamPrinciples.run()
    case "57" => MaterializingStreams.run()
    case "58" => OperatorFusionStreams.run()
    case "59" => BackpressureStreams.run()
    case "60" =>
      val streamGraph = StreamGraphBasics
      streamGraph.run()
      streamGraph.run2()
      streamGraph.run3()
    case "61" => StreamOpenGraphs.run()
    case "62" => StreamOpenGraphsWithMultipleFlows.run()
    case "63" => StreamOpenGraphsWithNonUniformShapes.run()
    case "64" => StreamOpenGraphMaterializedValues.run()
    case "65" => StreamOpenGraphsBidirectionalFlow.run()
    case "66" => StreamOpenGraphsCycles.run()
    case "67" => StreamOpenGraphsCycles.run1()
    case "68" => StreamOpenGraphsCycles.run2()
    case "69" => StreamOpenGraphWithTwoSourcesAndDifferentJoinStrategies.run()
    case "70" => StreamIntegrationWithActors.run()
    case _ => println("option unavailable.")
  }
}
