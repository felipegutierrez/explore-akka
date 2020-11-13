package org.github.felipegutierrez.explore.akka.recap

import scala.reflect.runtime.{universe => ru}
import ru._

object AdvancedReflection extends App {

  run()

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"hi, my name is $name")
  }

  def run() = {
    // 1- create a mirror
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    // 2- create a class object by its name
    val clazz = mirror.staticClass("org.github.felipegutierrez.explore.akka.recap.AdvancedReflection.Person")
    // 3- create a reflected mirror
    val reflectedMirror = mirror.reflectClass(clazz)
    // 4- get the constructor
    val constructor = clazz.primaryConstructor.asMethod
    // 5- reflect the constructor
    val constructorMirror = reflectedMirror.reflectConstructor(constructor)
    // 6- invoke the constructor
    val instance = constructorMirror.apply("John")

    println(instance)

    val p = Person("Felipe")
    // 1- find the method name
    val methodName = "sayMyName"
    // 2- reflect the instance using the mirror
    val reflected = mirror.reflect(p)
    // 3- method symbol
    val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
    // 4- reflect the method
    val method = reflected.reflectMethod(methodSymbol)
    // 5- invoke
    method.apply()

    /** Using Type Tags */
    // val typeTag = typeTag[Person]
    val typeTag = weakTypeTag[Person]
    println(typeTag.tpe)

    val myMap = new MyMap[Int, String]
    val typeArgs = getTypeArgs(myMap)
    println(typeArgs)
  }

  class MyMap[K, V]
  /** passing type tags as implicit parameters */
  def getTypeArgs[T](value: T)(implicit typeTag: TypeTag[T]) = weakTypeTag.tpe match {
    case TypeRef(_, _, typeArgs) => typeArgs
    case _ => List()
  }


}
