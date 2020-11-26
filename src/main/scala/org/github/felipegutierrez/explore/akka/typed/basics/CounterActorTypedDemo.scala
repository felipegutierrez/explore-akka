package org.github.felipegutierrez.explore.akka.typed.basics

import akka.actor.typed._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object CounterActorTypedDemo {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {

    import Counter._

    val countActor = ActorSystem(CounterActorTyped(), "CounterSystem")
    (1 to 5).foreach(_ => countActor ! Increment)
    (1 to 3).foreach(_ => countActor ! Decrement)
    countActor ! Print

    Thread.sleep(5000)
    countActor.terminate
  }
}

object Counter {

  trait CounterMsg

  final case object Print extends CounterMsg

  final case object Increment extends CounterMsg

  final case object Decrement extends CounterMsg

}

object CounterActorTyped {
  def apply(): Behavior[Counter.CounterMsg] = Behaviors.setup[Counter.CounterMsg](context => new CounterActorTyped(context))
}

class CounterActorTyped(context: ActorContext[Counter.CounterMsg]) extends AbstractBehavior[Counter.CounterMsg](context) {
  context.log.info("Counter Application started")
  var count = 0

  import Counter._

  override def onMessage(msg: CounterMsg): Behavior[CounterMsg] = msg match {
    case Increment =>
      context.log.info(s"incrementing $count ...")
      count += 1
      Behaviors.same
    case Decrement =>
      context.log.info(s"decrementing $count ...")
      count -= 1
      Behaviors.same
    case Print =>
      context.log.info(s"current count is: $count")
      Behaviors.same
  }

  override def onSignal: PartialFunction[Signal, Behavior[CounterMsg]] = {
    case PostStop =>
      context.log.info("Counter Application stopped")
      this
  }
}
