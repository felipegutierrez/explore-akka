package org.github.felipegutierrez.explore.recap

object AdvancedPartialFunctions {

  val myPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 11
    case 2 => 22
    case 5 => 55
  }

  val myPartialFunctionLifted = myPartialFunction.lift

  val myPartialFunctionChained = myPartialFunction.orElse[Int, Int] {
    case 3 => 33
  }

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def executeMyPartialFunction(value: Int): Int = {
    val res = myPartialFunction(value)
    res
  }

  def run() = {
    println(myPartialFunction(2))
    if (myPartialFunction.isDefinedAt(3)) {
      println(myPartialFunction(3))
    } else {
      println(s"partial function not defined for 3")
    }
    println(s"but now I don't care if the partial function does not define 3: ${myPartialFunctionLifted(3)}")
    println(s"or 1: ${myPartialFunctionLifted(1)}")

    println(s"partial function chain 5 : ${myPartialFunctionChained(5)}")
    println(s"partial function chain 45: ${myPartialFunctionChained(45)}")


    val advancedPartialFunctions = new AdvancedPartialFunctions()
    for (i <- 1 to 5) {
      if (advancedPartialFunctions.myInstancePartialFunction.isDefinedAt(i)) println(s"myInstancePartialFunction($i): ${advancedPartialFunctions.myInstancePartialFunction(i)}")
      else println(s"myInstancePartialFunction($i): not defined")
    }

    println(s"this chatbot accpect messages: 'Hi', 'How old are you?'")
    val res01 = advancedPartialFunctions.myDumbChatbot("Hi")
    println(res01)
    val res02 = advancedPartialFunctions.myDumbChatbot("How old are you?")
    println(res02)

    scala.io.Source.stdin.getLines().foreach({
      line =>
        println(s"you asked: $line")
        if (advancedPartialFunctions.myDumbChatbot.isDefinedAt(line)) {
          println(s"answer: ${advancedPartialFunctions.myDumbChatbot(line)}")
        } else {
          println(s"answer not defined for question: $line")
        }
    })
  }


  class AdvancedPartialFunctions {
    val myInstancePartialFunction: PartialFunction[Int, Int] = new PartialFunction[Int, Int] {
      override def isDefinedAt(x: Int): Boolean = (x == 1 || x == 2 || x == 5)

      override def apply(v1: Int): Int = v1 match {
        case 1 => 11
        case 2 => 22
        case 5 => 55
      }
    }

    val msgHiReply = "Hi, how are you?"
    val msgHowOldAreYouReply = "I am a bot created just now. It depends on your machine timestamp."

    val myDumbChatbot: PartialFunction[String, String] = new PartialFunction[String, String] {
      override def isDefinedAt(x: String): Boolean = (x.equals("Hi") || x.equals("How old are you?"))

      override def apply(v1: String): String = v1 match {
        case "Hi" => {
          msgHiReply
        }
        case "How old are you?" => {
          msgHowOldAreYouReply
        }
      }
    }
  }

}
