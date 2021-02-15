package org.github.felipegutierrez.explore.recap.codility

object FrogJmpS {
  def solution(x: Int, y: Int, d: Int): Int = {
    Math.ceil((y - x).toDouble / d).toInt
  }

  def solutionWithoutMath(x: Int, y: Int, d: Int): Int = {
    ((y - x) + (d - 1)) / d
  }
}
