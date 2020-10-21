package org.github.felipegutierrez.explore.akka.falttolerance

import java.io.File

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{BackoffOpts, BackoffSupervisor}

import scala.concurrent.duration._
import scala.io.Source

object BackoffSupervisorPattern extends App {

  run()

  def run() = {
    import FileBasedPersistentActor._
    val system = ActorSystem("BackoffSupervisorPattern")
    val simpleActor = system.actorOf(Props[FileBasedPersistentActor], "simpleActor")
    simpleActor ! ReadFile("src/main/resources/testfiles/important.txt")
    simpleActor ! ReadFile("src/main/resources/testfiles/important_data_not_found.txt")

    val simpleSupervisorProps = BackoffSupervisor.props(
      BackoffOpts.onFailure(
        Props[FileBasedPersistentActor],
        "simpleBackoffActor",
        3 seconds, // then 6s, 12s, 24s
        30 seconds,
        0.2
      )
    )
    val simpleBackoffSupervisor = system.actorOf(simpleSupervisorProps, "simpleSupervisor")
    simpleBackoffSupervisor ! ReadFile("src/main/resources/testfiles/important_data_not_found.txt")

    /*
    simpleSupervisor
      - child called simpleBackoffActor (props of type FileBasedPersistentActor)
      - supervision strategy is the default one (restarting on everything)
        - first attempt after 3 seconds
        - next attempt is 2x the previous attempt
   */
    val stopSupervisorProps = BackoffSupervisor.props(
      BackoffOpts.onStop(
        Props[FileBasedPersistentActor],
        "stopBackoffActor",
        3 seconds,
        30 seconds,
        0.2
      ).withSupervisorStrategy(
        OneForOneStrategy() {
          case _ => Stop
        }
      )
    )
    val stopSupervisor = system.actorOf(stopSupervisorProps, "stopSupervisor")
    stopSupervisor ! ReadFile("src/main/resources/testfiles/important_data_not_found.txt")
  }

  object FileBasedPersistentActor {

    case class ReadFile(fileName: String)

  }

  class FileBasedPersistentActor extends Actor with ActorLogging {

    import FileBasedPersistentActor._

    var dataSource: Source = null
    var dataSourceFile: String = ""

    override def preStart(): Unit = log.info("Persistent actor starting")

    override def postStop(): Unit = log.warning("Persistent actor has stopped")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.warning("Persistent actor restarting")

    override def receive: Receive = {
      case ReadFile(fileName) =>
        if (dataSource == null || !dataSourceFile.equals(fileName)) {
          dataSourceFile = fileName
          dataSource = Source.fromFile(new File(dataSourceFile))
        }
        log.info("I've just read some IMPORTANT data: " + dataSource.getLines().toList)
    }
  }

}
