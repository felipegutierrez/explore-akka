package org.github.felipegutierrez.explore

import org.github.felipegutierrez.explore.akka.classic.basics._
import org.github.felipegutierrez.explore.akka.classic.clustering.basic.{ClusteringBasics, ClusteringManualRegistration}
import org.github.felipegutierrez.explore.akka.classic.clustering.chat.ChatApp
import org.github.felipegutierrez.explore.akka.classic.clustering.k8s.SimpleClusterK8sMain
import org.github.felipegutierrez.explore.akka.classic.clustering.wordcount.{ClusteringWordCount, ClusteringWordCountAdditionalWorkers}
import org.github.felipegutierrez.explore.akka.classic.falttolerance._
import org.github.felipegutierrez.explore.akka.classic.http.client.{ConnectionLevel, HostLevel, PaymentSystem, RequestLevel}
import org.github.felipegutierrez.explore.akka.classic.http.k8s.WebServerK8s
import org.github.felipegutierrez.explore.akka.classic.http.server.highlevel._
import org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel.{BasicServerLowLevel, GuitarRestApi, HttpsRestApi}
import org.github.felipegutierrez.explore.akka.classic.infra._
import org.github.felipegutierrez.explore.akka.classic.patterns.{AskPatternDemo, StashDemo, VendingMachineDemo, VendingMachineFSMDemo}
import org.github.felipegutierrez.explore.akka.classic.persistence.detaching.DetachingModels
import org.github.felipegutierrez.explore.akka.classic.persistence.event_sourcing._
import org.github.felipegutierrez.explore.akka.classic.persistence.schema.EventAdapters
import org.github.felipegutierrez.explore.akka.classic.persistence.serialization._
import org.github.felipegutierrez.explore.akka.classic.persistence.stores.{CassandraStores, LocalStores, PostgresStores}
import org.github.felipegutierrez.explore.akka.classic.remote.ClusteringPlayground
import org.github.felipegutierrez.explore.akka.classic.remote.deployment.{LocalDeployment, RemoteDeployment}
import org.github.felipegutierrez.explore.akka.classic.remote.hello.{LocalActor, RemoteActor}
import org.github.felipegutierrez.explore.akka.classic.remote.serialization._
import org.github.felipegutierrez.explore.akka.classic.remote.wordcount.{MasterApp, WorkerApp}
import org.github.felipegutierrez.explore.akka.classic.streams.advanced._
import org.github.felipegutierrez.explore.akka.classic.streams.basics.{BackpressureStreams, FirstStreamPrinciples, MaterializingStreams, OperatorFusionStreams}
import org.github.felipegutierrez.explore.akka.classic.streams.graphs._
import org.github.felipegutierrez.explore.akka.classic.streams.monitoring.FirstStreamMonitoring
import org.github.felipegutierrez.explore.akka.classic.streams.techniques.{StreamBackpressure, StreamFaultTolerance, StreamIntegrationWithActors, StreamIntegrationWithExternalServices}
import org.github.felipegutierrez.explore.akka.typed.basics.{BankAccountActorTyped, CounterActorTypedDemo}
import org.github.felipegutierrez.explore.akka.typed.monitoring.AkkaQuickStartWithKamon
import org.github.felipegutierrez.explore.akka.typed.patterns.VendingMachineTypedDemo

import java.util.Scanner

object MainClass {
  def main(args: Array[String]): Unit = {
    println(s"0 - out")
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
    println(s"39.1 - Simple Cluster on to K8s")
    println(s"40 - ClusteringManualRegistration")
    println(s"41 - ClusteringWordCount")
    println(s"42 - Cluster chat")
    println(s"42.1 - Alice chat")
    println(s"42.2 - Bob chat")
    println(s"42.3 - Felipe chat")
    println(s"42.4 - Fabio chat")
    println(s"43 - Remote custom serialization with Spray JSON")
    println(s"44 - Remote custom serialization with Kryo")
    println(s"45 - Remote custom serialization with Avro")
    println(s"46 - PersistentActors")
    println(s"47 - PersistentActorsExercise")
    println(s"48 - Snapshots")
    println(s"49 - RecoveryDemo")
    println(s"50 - PersistAsyncDemo")
    println(s"51 - LocalStores")
    println(s"52 - PostgresStores")
    println(s"52.1 - PostgresStores custom serialization with Spray JSON")
    println(s"52.2 - PostgresStores custom serialization with Kryo")
    println(s"52.3 - PostgresStores custom serialization with Avro")
    println(s"53 - CassandraStores")
    println(s"54 - EventAdapters")
    println(s"55 - DetachingModels")
    println(s"56 - Streams FirstStreamPrinciples")
    println(s"57 - Streams MaterializingStreams")
    println(s"58 - Streams OperatorFusionStreams")
    println(s"59 - Streams BackpressureStreams")
    println(s"60 - Stream GraphBasics")
    println(s"61 - Stream OpenGraphs")
    println(s"62 - Stream OpenGraphsWithMultipleFlows")
    println(s"63 - Stream OpenGraphsWithNonUniformShapes")
    println(s"64 - Stream OpenGraphMaterializedValues")
    println(s"65 - Stream OpenGraphsBidirectionalFlow")
    println(s"66 - Stream OpenGraphsCycles")
    println(s"67 - Stream OpenGraphsCycles buffers")
    println(s"68 - Stream OpenGraphsCycles fibonacci")
    println(s"69 - Stream OpenGraphWithTwoSourcesAndDifferentJoinStrategies")
    println(s"70 - Stream IntegrationWithActors")
    println(s"71 - Stream IntegrationWithExternalServices")
    println(s"72 - Stream Backpressure")
    println(s"73 - Stream FaultTolerance")
    println(s"74 - Stream StreamOpenGraphWithSubStream")
    println(s"75 - Stream StreamCustomGraphShapes")
    println(s"76 - Stream StreamCustomGraphOperators")
    println(s"77 - Stream Flow operator in mini-batches based on number of elements")
    println(s"78 - Stream Flow operator in mini-batches based on time")
    println(s"79 - Stream window Flow based on time or events")
    println(s"80 - Stream window Flow to group events with same ID")
    println(s"81 - Monitoring actors with Kamon and Prometheus")
    println(s"82 - Monitoring Akka stream with Kamon and Prometheus")
    println(s"83 - Akka HTTP hello word")
    println(s"83.1 - Akka HTTP hello word using K8S")
    println(s"84 - Basic low level Akka HTTP server to accept connections")
    println(s"85 - (un)marshalling JSON to Akka HTTP")
    println(s"86 - Akka HTTP secure using HTTPS")
    println(s"87 - GuitarRestHighLevelApi")
    println(s"88 - PersonRestApi")
    println(s"89 - MarshallingJSON")
    println(s"90 - Akka-HTTP web sockets")
    println(s"91 - Akka-HTTP upload files using Akka-stream")
    println(s"92 - Akka-HTTP with JSON Web Token authentication & authorization")
    println(s"93 - Akka-HTTP client request -> response")
    println(s"94 - Akka-HTTP client request -> response PaymentSystem")
    println(s"95 - Akka-HTTP HostLevel")
    println(s"96 - Akka-HTTP RequestLevel")
    println(s"97 - Protobuffer: using Akka remote serialization with protobuf")
    println(s"98 - Protobuffer: using Akka persistence serialization with protobuf")
    println(s"99.1 - Benchmarking serialization with Java, Avro, Kryo, and Protobuffer using Akka-remote")
    println(s"99.2 - Benchmarking serialization with Java, Avro, Kryo, and Protobuffer using Akka-persistence")

    var option01: String = ""
    var option02: String = ""
    if (args.length == 0) {
      println("choose an application: ")
      val scanner = new Scanner(System.in)
      option01 = scanner.nextLine()
    } else {
      option01 = args(0)
      if (args.length >= 2) option02 = args(1)
    }

    println(s"option01 (app): $option01")
    println(s"option02      : $option02")
    option01 match {
      case "0" => println(s"Bye, see you next time.")
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
      case "39.1" => SimpleClusterK8sMain.run(option02)
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
      case "43" =>
        CustomSerialization_Remote.run()
        CustomSerialization_Local.run()
      case "44" =>
        KryoSerialization_Remote.run()
        KryoSerialization_Local.run()
      case "45" =>
        AvroSerialization_Remote.run()
        AvroSerialization_Local.run()
      case "46" => PersistentActors.run()
      case "47" => PersistentActorsExercise.run()
      case "48" => Snapshots.run()
      case "49" => RecoveryDemo.run()
      case "50" => PersistAsyncDemo.run()
      case "51" => LocalStores.run()
      case "52" => PostgresStores.run()
      case "52.1" => CustomSerialization_Persistence.run()
      case "52.2" => KryoSerialization_Persistence.run()
      case "52.3" => AvroSerialization_Persistence.run()
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
      case "69" => StreamOpenGraphWithTwoSourcesAndPartitionStrategies.run()
      case "70" => StreamIntegrationWithActors.run()
      case "71" => StreamIntegrationWithExternalServices.run()
      case "72" => StreamBackpressure.run()
      case "73" => StreamFaultTolerance.run()
      case "74" => StreamOpenGraphWithSubStream.run()
      case "75" => StreamCustomGraphShapes.run()
      case "76" => StreamCustomGraphOperators.run()
      case "77" => StreamBatchFlowGraphOperators.run()
      case "78" => StreamBatchTimerFlowGraphOperators.run()
      case "79" => StreamOpenGraphWindow.run()
      case "80" => WindowGroupEventFlow.run()
      case "81" => AkkaQuickStartWithKamon.run()
      case "82" => FirstStreamMonitoring.run()
      case "83" => org.github.felipegutierrez.explore.akka.classic.http.Playground.run()
      case "83.1" => WebServerK8s.run()
      case "84" => BasicServerLowLevel.run()
      case "85" => GuitarRestApi.run()
      case "86" => HttpsRestApi.run()
      case "87" => GuitarRestHighLevelApi.run()
      case "88" => PersonRestApi.run()
      case "89" => MarshallingJSON.run()
      case "90" => WebSocketDemo.run()
      case "91" => UploadingFiles.run()
      case "92" => JSONWebTokenDemo.run()
      case "93" => ConnectionLevel.run()
      case "94" =>
        PaymentSystem.run()
        ConnectionLevel.run()
      case "95" =>
        PaymentSystem.run()
        HostLevel.run()
      case "96" =>
        PaymentSystem.run()
        RequestLevel.run()
      case "97" =>
        ProtobufSerialization_Remote.run()
        ProtobufSerialization_Local.run()
      case "98" => ProtobufSerialization_Persistence.run()
      case "99.1" =>
        VotingCentralizer.run()
        VotingStation.run()
      case "99.2" => BenchmarkSerialization.run()
      case _ => println("option unavailable.")
    }
  }
}
