package org.github.felipegutierrez.explore.akka.recap

object AdvancedVariance extends App {

  run()

  /** Invariance example */
  class IParking[T](things: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???
    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  /** Covariance example: widening the type with >: */
  class CParking[+T](things: IList[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  /** Contravariance example */
  class XParking[-T](things: IList[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
    def flatMap[R <: T, S](f: Function1[R, XParking[S]] => XParking[S]): XParking[S] = ???
  }

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  def run() = {

  }
}
