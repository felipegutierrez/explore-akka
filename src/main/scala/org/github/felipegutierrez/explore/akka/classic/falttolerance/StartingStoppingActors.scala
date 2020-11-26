package org.github.felipegutierrez.explore.akka.classic.falttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App {

  run()

  def run() = {
    import Parent._
    val system = ActorSystem("StartingStoppingActors")
    val parentActor = system.actorOf(Props[Parent], "parent")

    ///*  stopping child after some time
    parentActor ! StartChild("child1")
    val child = system.actorSelection("/user/parent/child1")
    child ! "hi kid!"
    Thread.sleep(1000)
    parentActor ! StopChild("child1")
    for (_ <- 1 to 50) child ! "are you still there?"
    //*/

    /* stopping parent makes all its children stop as well
    parentActor ! StartChild("child2")
    val child2 = system.actorSelection("/user/parent/child2")
    child2 ! "hi kid 2!"
    parentActor ! Stop
    for (_ <- 1 to 10) parentActor ! "Parent, are you still there?"
    for (i <- 1 to 100) child2 ! s"[$i] are you still there second kid?"
    */

    /*  stopping actors using the PoisonPill
    val childWithoutParent = system.actorOf(Props[Child], "childWithoutParent")
    childWithoutParent ! "hello child without a parent, are you there?"
    childWithoutParent ! PoisonPill
    childWithoutParent ! "hello child without a parent, are you still...... there?"
    */

    /* killing the actor with kill message
    val abruptlyTerminatedActor = system.actorOf(Props[Child])
    abruptlyTerminatedActor ! "you are about to be terminated!"
    abruptlyTerminatedActor ! Kill
    abruptlyTerminatedActor ! "have you been terminated?"
    */
  }

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        val childActor: ActorRef = context.actorOf(Props[Child], name)
        context.become(withChildren(children + (name -> childActor)))
      case StopChild(name) =>
        log.info(s"Stopping child: $name")
        val childOption = children.get(name)
        // context.stop is a non-blocking method
        childOption.foreach(childRef => context.stop(childRef))
      case Stop =>
        log.info("Stopping myself")
        // the stops 'self' starts by stopping all children and then stops the parent actor
        context.stop(self)
      case message => log.info(message.toString)
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

}
