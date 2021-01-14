package org.github.felipegutierrez.explore.recap

import scala.annotation.tailrec

object AdvancedFunctionalCollections {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val mySet = MySet(1, 2, 3)
    mySet.foreach(println)

    val anotherSet = mySet + 4
    anotherSet.foreach(println)

    val againAnotherSet = mySet ++ MySet(5, 6, 7)
    againAnotherSet.foreach(println)

    println(s"size ${againAnotherSet.size()}")

    mySet.map(x => x * 10).foreach(println)
  }

  object MySet {
    def apply[A](values: A*): MySet[A] = {
      @tailrec def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
        if (valSeq.isEmpty) acc
        else buildSet(valSeq.tail, acc + valSeq.head)
      buildSet(values.toSeq, new EmptySet[A])
    }
  }

  trait MySet[A] extends (A => Boolean) {
    def apply(elem: A): Boolean = contains(elem)
    def size(): Int
    def contains(elem: A): Boolean
    def +(elem: A): MySet[A]
    def ++(anotherSet: MySet[A]): MySet[A] // union
    def map[B](f: A => B): MySet[B]
    def flatMap[B](f: A => MySet[B]): MySet[B]
    def filter(predicate: A => Boolean): MySet[A]
    def foreach(f: A => Unit): Unit
    def -(elem: A): MySet[A]
    def --(anotherSet: MySet[A]): MySet[A] // difference
    def &(anotherSet: MySet[A]): MySet[A] // intersection
    def unary_! : MySet[A]
  }

  class EmptySet[A] extends MySet[A] {
    override def contains(elem: A): Boolean = false
    override def size(): Int = 0
    override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
    override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
    override def map[B](f: A => B): MySet[B] = new EmptySet[B]
    override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
    override def filter(predicate: A => Boolean): MySet[A] = this
    override def foreach(f: A => Unit): Unit = ()
    override def -(elem: A): MySet[A] = this
    override def --(anotherSet: MySet[A]): MySet[A] = this
    override def &(anotherSet: MySet[A]): MySet[A] = this
    override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
  }

  // all elements of type A which satisfy a property
  // { x in A | property(x) }
  class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
    override def size(): Int = Int.MaxValue
    def contains(elem: A): Boolean = property(elem)
    // { x in A | property(x) } + element = { x in A | property(x) || x == element }
    def +(elem: A): MySet[A] =
      new PropertyBasedSet[A](x => property(x) || x == elem)

    // { x in A | property(x) } ++ set => { x in A | property(x) || set contains x }
    def ++(anotherSet: MySet[A]): MySet[A] =
      new PropertyBasedSet[A](x => property(x) || anotherSet(x))

    // all integers => (_ % 3) => [0 1 2]
    def map[B](f: A => B): MySet[B] = politelyFail
    def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
    def foreach(f: A => Unit): Unit = politelyFail

    def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))
    def -(elem: A): MySet[A] = filter(x => x != elem)
    def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
    def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
    def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

    def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
  }

  class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
    override def contains(elem: A): Boolean = head == elem || tail.contains(elem)
    def size(): Int = {
      var i = 0
      this.foreach { _ => i+=1 }
      i
    }
    override def +(elem: A): MySet[A] =
      if (this.contains(elem)) this
      else new NonEmptySet[A](elem, this)
    override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head
    override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)
    override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)
    override def filter(predicate: A => Boolean): MySet[A] = {
      val filteredTail = tail.filter(predicate)
      if (predicate(head)) filteredTail + head
      else filteredTail
    }
    override def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }
    override def -(elem: A): MySet[A] = {
      if (head == elem) tail
      else tail.-(elem).+(head)
    }
    override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet.contains(x))
    override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection is equal to filtering
    override def unary_! : MySet[A] = this
  }
}

