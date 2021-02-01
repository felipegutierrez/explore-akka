package org.github.felipegutierrez.explore.recap

object AdvancedMonads {

  //    def main(args: Array[String]): Unit = {
  //      run()
  //    }

  val attemptRuntimeException = AttemptMonad {
    throw new RuntimeException("My own monad, yes!")
  }

  def run() = {

    println(attemptRuntimeException)

    val lazyInstance = LazyMonad {
      println("Today I don't feel like doing anything")
      42
    }

    val flatMappedInstance = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    val flatMappedInstance2 = lazyInstance.flatMap(x => LazyMonad {
      10 * x
    })
    flatMappedInstance.use
    flatMappedInstance2.use
  }

  // our own Try monad
  trait AttemptMonad[+A] {
    def flatMap[B](f: A => AttemptMonad[B]): AttemptMonad[B]
  }

  case class SuccessMonad[+A](value: A) extends AttemptMonad[A] {
    def flatMap[B](f: A => AttemptMonad[B]): AttemptMonad[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => FailMonad(e)
      }
  }

  case class FailMonad(e: Throwable) extends AttemptMonad[Nothing] {
    def flatMap[B](f: Nothing => AttemptMonad[B]): AttemptMonad[B] = this
  }

  // 1 - Lazy monad
  class LazyMonad[+A](value: => A) {
    private lazy val internalValue = value

    def use: A = internalValue

    // flatMap is using call by need
    def flatMap[B](f: (=> A) => LazyMonad[B]): LazyMonad[B] = f(internalValue)
  }

  object AttemptMonad {
    def apply[A](a: => A): AttemptMonad[A] =
      try {
        SuccessMonad(a)
      } catch {
        case e: Throwable => FailMonad(e)
      }
  }

  object LazyMonad {
    def apply[A](value: => A): LazyMonad[A] = new LazyMonad(value) // unit
  }

  // 2: map and flatten in terms  of flatMap
  /*
    Monad[T] { // List
      def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)
      def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x))) // Monad[B]
      def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)
      List(1,2,3).map(_ * 2) = List(1,2,3).flatMap(x => List(x * 2))
      List(List(1, 2), List(3, 4)).flatten = List(List(1, 2), List(3, 4)).flatMap(x => x) = List(1,2,3,4)
    }
   */
}
