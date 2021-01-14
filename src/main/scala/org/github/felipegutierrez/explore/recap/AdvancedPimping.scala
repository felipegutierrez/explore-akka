package org.github.felipegutierrez.explore.recap

object AdvancedPimping {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    implicit class RicherInt(richInt: RichInt) {
      def isOdd: Boolean = richInt.value % 2 != 0
    }

    println(s"new RichInt(42).sqrt: ${new RichInt(42).sqrt}")

    println(s"42.isEven: ${42.isEven}") // new RichInt(42).isEven
    // type enrichment = pimping

    1 to 10

    import scala.concurrent.duration._
    3.seconds

    println(s"12.asInt: ${"12".asInt}")
    println(s"felipe.encrypt: ${"felipe".encrypt(2)}")

    3.times(() => println("Scala Rocks!"))
    println(4 * List(1, 2))
  }

  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }

      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] =
        if (n <= 0) List()
        else concatenate(n - 1) ++ list

      concatenate(value)
    }
  }

  implicit class RichString(val value: String) extends AnyVal {
    def asInt: Int = Integer.valueOf(value) // java.lang.Integer -> Int
    def encrypt(cypherDistance: Int): String = value.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

}
