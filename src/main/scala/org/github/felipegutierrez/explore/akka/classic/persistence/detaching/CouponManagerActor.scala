package org.github.felipegutierrez.explore.akka.classic.persistence.detaching

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

import scala.collection.mutable

object DomainModel {

  case class User(id: String, email: String, name: String)

  case class Coupon(code: String, promotionAmount: Int)

  // command
  case class ApplyCoupon(coupon: Coupon, user: User)

  // event
  case class CouponApplied(code: String, user: User)

}

object DataModel {

  case class WrittenCouponApplied(code: String, userId: String, userEmail: String)

  case class WrittenCouponAppliedVersion2(code: String, userId: String, userEmail: String, username: String)

}

class CouponManagerActor extends PersistentActor with ActorLogging {

  import DomainModel._

  val coupons: mutable.Map[String, User] = new mutable.HashMap[String, User]()

  override def persistenceId: String = "coupon-manager"

  override def receiveCommand: Receive = {
    case ApplyCoupon(coupon, user) =>
      if (!coupons.contains(coupon.code)) {
        persist(CouponApplied(coupon.code, user)) { e =>
          log.info(s"Persisted $e")
          coupons.put(coupon.code, user)
        }
      }
  }

  override def receiveRecover: Receive = {
    case event@CouponApplied(code, user) =>
      log.info(s"Recovered $event")
      coupons.put(code, user)
  }
}
