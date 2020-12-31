package org.github.felipegutierrez.explore.akka.classic.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object Mailboxes {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    val system = ActorSystem("dispatchersDemo", ConfigFactory.load().getConfig("mailboxesDemo"))
    // CASE 1
    val priorityActor = system.actorOf(Props[Counter].withDispatcher("support-ticket-dispatcher"), s"counter_")
    priorityActor ! "[P3] this is nice to have one day in the future"
    priorityActor ! "[P0] we need to get this done now"
    priorityActor ! "what should I do now?"
    priorityActor ! "[P1] we need to solve this because it is blocking other things"
    priorityActor ! "[P2] this is necessary to be done"

    // CASE 2
    val controlAwareActor = system.actorOf(Props[Counter].withDispatcher("control-mailbox"))
    controlAwareActor ! "[P3] this is nice to have one day in the future"
    controlAwareActor ! "[P0] we need to get this done now"
    controlAwareActor ! "what should I do now?"
    controlAwareActor ! "[P1] we need to solve this because it is blocking other things"
    controlAwareActor ! "[P2] this is necessary to be done"
    controlAwareActor ! ManagementTicket

    // CASE 3 - using deployment config at application.conf
    val controlAwareDeploymentActor = system.actorOf(Props[Counter], "altControlAwareActor")
    controlAwareDeploymentActor ! "[P3] this is nice to have one day in the future"
    controlAwareDeploymentActor ! "[P0] we need to get this done now"
    controlAwareDeploymentActor ! "what should I do now?"
    controlAwareDeploymentActor ! "[P1] we need to solve this because it is blocking other things"
    controlAwareDeploymentActor ! "[P2] this is necessary to be done"
    controlAwareDeploymentActor ! ManagementTicket
  }

  /**
   * CASE 1 - a custom priority mailbox
   * step 1 - the mailbox definition
   * step 2 - define the configuration at application.conf with org.github.felipegutierrez.explore.akka.classic.infra.Mailboxes$SupportTicketPriorityMailbox
   * step 3 - attach the dispatcher to an actor
   */
  // step 1 and 2
  class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config)
    extends UnboundedPriorityMailbox(PriorityGenerator {
      case message: String if message.startsWith("[P0]") => 0
      case message: String if message.startsWith("[P1]") => 1
      case message: String if message.startsWith("[P2]") => 2
      case message: String if message.startsWith("[P3]") => 3
      case _ => 4
    })

  /**
   * CASE 2 - control-aware mailbox
   * We will use the UnboundedControlAwareMailbox
   * step 1 - mark important messages as control messages
   * step 2 - configure who gets the mailbox
   *        - make the actor attach to the mailbox
   */
  // step 1
  case object ManagementTicket extends ControlMessage


  class Counter extends Actor with ActorLogging {
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"Counter [$count] $message")
    }
  }

}
