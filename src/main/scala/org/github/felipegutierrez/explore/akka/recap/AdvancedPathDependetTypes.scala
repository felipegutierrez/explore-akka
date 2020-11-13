package org.github.felipegutierrez.explore.akka.recap

object AdvancedPathDependetTypes extends App {

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int]

  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42)
  get[StringItem]("this is a string")
  // get[IntItem]("this is not an Int")
}
