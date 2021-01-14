package org.github.felipegutierrez.explore.recap

object AdvancedBoundedPolymorphism {

  trait Animal {
    def bread: List[Animal]
  }
  class Cat extends Animal {
    override def bread: List[Cat] = ???
  }
  class Dog extends Animal {
    /** we should not be able to write List[Cat] inside a Dog class!!! */
    override def bread: List[Cat] = ???
  }

  /** Solution 1 - using polymorphism. Make the Animal trait upper bounded to itself */
  trait AnimalPoly[A <: AnimalPoly[A]] {
    def bread: List[AnimalPoly[A]]
  }
  /** now extends the Animal trait using the type */
  class CatPoly extends AnimalPoly[CatPoly] {
    override def bread: List[CatPoly] = ???
  }
  class DogPoly extends AnimalPoly[DogPoly] {
    override def bread: List[DogPoly] = ???
  }
  /** this example of F-bounded Polymorphism is seen in Java Comparable objects */
  class Person extends Comparable[Person] {
    override def compareTo(t: Person): Int = ???
  }
  /** However developer still can write the code that is not semantically right
   * the Crocodile class extends a Dog and still uses the F-Bounded Polymorphism! */
  class Crocodile extends AnimalPoly[DogPoly] {
    override def bread: List[DogPoly] = ???
  }

  /** Solution 2 - using F-Bounded Polymorphis with self-type */
  trait AnimalSelfPoly[A <: AnimalSelfPoly[A]] { self: A =>
    def bread: List[AnimalSelfPoly[A]]
  }
  /** now extends the Animal trait using the type */
  class CatSelfPoly extends AnimalSelfPoly[CatSelfPoly] {
    override def bread: List[CatSelfPoly] = ???
  }
  class DogSelfPoly extends AnimalSelfPoly[DogSelfPoly] {
    override def bread: List[DogSelfPoly] = ???
  }
  /** now the compiler does not ally the Crocodile act as a Dog */
  //  class CrocodileSelf extends AnimalSelfPoly[DogSelfPoly] {
  //    override def bread: List[DogSelfPoly] = ???
  //  }
}
