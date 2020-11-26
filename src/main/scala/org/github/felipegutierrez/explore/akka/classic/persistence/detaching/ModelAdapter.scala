package org.github.felipegutierrez.explore.akka.classic.persistence.detaching

import akka.persistence.journal.{EventAdapter, EventSeq}

class ModelAdapter extends EventAdapter {

  import DataModel._
  import DomainModel._

  override def manifest(event: Any): String = "CMA"

  /** deserializing: journal -> serializer -> fromJournal -> to the actor */
  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case event@WrittenCouponApplied(code, userId, userEmail) =>
      println(s"Converting $event to DOMAIN model")
      EventSeq.single(CouponApplied(code, User(userId, userEmail, "")))
    case event@WrittenCouponAppliedVersion2(code, userId, userEmail, userName) =>
      println(s"Converting $event to DOMAIN model")
      EventSeq.single(CouponApplied(code, User(userId, userEmail, userName)))
    case other =>
      println(s"other unknown event: $other")
      EventSeq.single(other)
  }

  /** serializing: actor -> toJournal -> serializer -> journal */
  override def toJournal(event: Any): Any = event match {
    case event@CouponApplied(code, user) =>
      println(s"Converting $event to DATA model")
      WrittenCouponAppliedVersion2(code, user.id, user.email, user.name)
  }
}
