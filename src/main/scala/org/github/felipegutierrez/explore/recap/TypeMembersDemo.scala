package org.github.felipegutierrez.explore.recap

object TypeMembersDemo {

  /** a locked trait */
  trait LockTrait {
    type A
    def head: A
    def tail: LockTrait
  }

  trait AllowedOnlyNumberTypes {
    type A <: Number
  }

  /** a class that should not compile by using type members */
  //  class Customlist(hd: String, tl: Customlist) extends LockTrait with AllowedOnlyNumberTypes {
  //    type A = String
  //    def head = hd
  //    def tail = tl
  //  }

  /** a class that must compile by using type members */
  class Numberlist(hd: Number, tl: Numberlist) extends LockTrait with AllowedOnlyNumberTypes {
    type A = Number
    def head = hd
    def tail = tl
  }

}
