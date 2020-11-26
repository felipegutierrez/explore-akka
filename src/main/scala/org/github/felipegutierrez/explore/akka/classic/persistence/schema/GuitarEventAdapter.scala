package org.github.felipegutierrez.explore.akka.classic.persistence.schema

import akka.persistence.journal.{EventSeq, ReadEventAdapter}

class GuitarEventAdapter extends ReadEventAdapter {
  /**
   * journal -> serializer -> read event adapter -> actor
   * (bytes)    (GuitarAdd)   (GuitarAddVersion2)   (receiveRecover)
   */
  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case guitar@GuitarAdded(id, model, maker, qtd) =>
      println(s"reading guitar event: $guitar")
      EventSeq.single(GuitarAddedVersion2(id, model, maker, qtd, GuitarDomain.ACOUSTIC))
    case other =>
      println(s"reading another unknown event: $other")
      EventSeq.single(other)
  }
}
