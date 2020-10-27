package org.github.felipegutierrez.explore.akka.recap

import scala.annotation.tailrec

object AdvancedFunctionalCollections extends App {

  run()

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
    def ++(anotherSet: MySet[A]): MySet[A]
    def map[B](f: A => B): MySet[B]
    def flatMap[B](f: A => B): MySet[B]
    def filter(predicate: A => Boolean): MySet[A]
    def foreach(f: A => Unit): Unit
  }

  class EmptySet[A] extends MySet[A] {
    override def contains(elem: A): Boolean = false
    def size(): Int = 0
    override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
    override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
    override def map[B](f: A => B): MySet[B] = new EmptySet[B]
    override def flatMap[B](f: A => B): MySet[B] = new EmptySet[B]
    override def filter(predicate: A => Boolean): MySet[A] = this
    override def foreach(f: A => Unit): Unit = ()
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
    override def flatMap[B](f: A => B): MySet[B] = (tail.flatMap(f)) + f(head)
    override def filter(predicate: A => Boolean): MySet[A] = {
      val filteredTail = tail.filter(predicate)
      if (predicate(head)) filteredTail + head
      else filteredTail
    }
    override def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }
  }
}

