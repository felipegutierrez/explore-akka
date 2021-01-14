package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedPartialFunctionsSpec extends AnyFlatSpec {

  import AdvancedPartialFunctions._

  "A partial function" should
    "accept only partial values" in {
    val advancedPartialFunctions = new AdvancedPartialFunctions()
    val res01 = advancedPartialFunctions.myInstancePartialFunction.isDefinedAt(1)
    assert(res01.equals(true))
    val res11 = advancedPartialFunctions.myInstancePartialFunction(1)
    assert(res11.equals(11))
    val res03 = advancedPartialFunctions.myInstancePartialFunction.isDefinedAt(3)
    assert(res03.equals(false))
  }
  "a chatbot with partial function" should
    "answer only limited questions" in {
    val advancedPartialFunctions = new AdvancedPartialFunctions()

    val res01 = advancedPartialFunctions.myDumbChatbot("Hi")
    assert(res01.equals(advancedPartialFunctions.msgHiReply))

    val res02 = advancedPartialFunctions.myDumbChatbot("How old are you?")
    assert(res02.equals(advancedPartialFunctions.msgHowOldAreYouReply))

    val res03 = advancedPartialFunctions.myDumbChatbot.isDefinedAt("what a weird question")
    assert(res03 == false)
  }
}
