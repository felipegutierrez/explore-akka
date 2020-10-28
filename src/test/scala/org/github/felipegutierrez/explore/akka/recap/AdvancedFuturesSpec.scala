package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedFuturesSpec extends AnyFlatSpec {

  import AdvancedFutures.BankingApp

  "a future with timeout" should
    "return success when there is a AWAIT timeout inside" in {
    val res = BankingApp.purchase("Daniel", "iPhone 12", "rock the jvm store", 3000)
    println(res.equals("SUCCESS"))
  }
}
