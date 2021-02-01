package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedPartialFunctionsSpec extends AnyFlatSpec {

  import AdvancedPartialFunctions._

  "A simple partial function" should
    "return only accepted values" in {
    assert(myPartialFunction.isDefinedAt(1))
    assert(myPartialFunction.isDefinedAt(2))
    assert(myPartialFunction.isDefinedAt(5))
    assert(myPartialFunction.isDefinedAt(3) == false)
  }
  "A lifted partial function" should
    "return any defined value inside Some or None when there is no defined value" in {
    val value01: Option[Int] = myPartialFunctionLifted(1)
    assert(Some(11) == value01)
    val value02: Option[Int] = myPartialFunctionLifted(2)
    assert(Some(22) == value02)
    val value03: Option[Int] = myPartialFunctionLifted(3)
    assert(None == value03)
  }
  "A chained partial function with orElse" should
    "return values from the original and chained partial" in {
    assert(myPartialFunctionChained.isDefinedAt(1))
    assert(myPartialFunctionChained.isDefinedAt(2))
    assert(myPartialFunctionChained.isDefinedAt(5))
    assert(myPartialFunctionChained.isDefinedAt(3))
  }
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
