package org.github.felipegutierrez.explore.akka.classic.persistence.detaching

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object DetachingModels extends App {
  run()

  def run() = {
    import DomainModel._
    val system = ActorSystem("DetachingModelsSystem", ConfigFactory.load().getConfig("detachingModels"))
    val couponManagerActor = system.actorOf(Props[CouponManagerActor], "couponManagerActor")

//    for (i <- 1 to 10) {
//      val coupon = Coupon(s"MEGA_COUPON_$i", 100)
//      val user = User(s"$i", s"user_$i@rtjvm.com", s"user_name_$i")
//
//      couponManagerActor ! ApplyCoupon(coupon, user)
//    }
  }
}
