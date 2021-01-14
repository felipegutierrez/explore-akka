package org.github.felipegutierrez.explore.recap

object AdvancedPathDependetTypes {

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int]

  trait StringItem extends Item[String]

  get[IntItem](42)
  get[StringItem]("this is a string")
  // get[IntItem]("this is not an Int")
}
