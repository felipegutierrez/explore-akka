package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class CartActorDemoSpec extends TestKit(ActorSystem("CartActorDemoSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import CartActorDemo._

  "The ActorsIntro actor" should {
    "send back hi replay" in {
      import CartActorDemo.CartActor._
      val cart = TestActorRef[CartActor]

      val item = "MyItem"
      cart ! AddItem(item)
      expectMsg(s"item $item added.")
      cart ! StartCheckout
      expectMsg(OrderManager.ConfirmCheckoutStarted(cart))
    }
  }
}
