package org.github.felipegutierrez.explore.akka.persistence.detaching

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object DetachingModels extends App {
  run()

  def run() = {
    import DomainModel._
    val system = ActorSystem("DetachingModelsSystem", ConfigFactory.load().getConfig("detachingModels"))
    val couponManagerActor = system.actorOf(Props[CouponManagerActor], "couponManagerActor")

//    for (i <- 10 to 15) {
//      val coupon = Coupon(s"MEGA_COUPON_$i", 100)
//      val user = User(s"$i", s"user_$i@rtjvm.com")
//
//      couponManagerActor ! ApplyCoupon(coupon, user)
//    }
  }
}
