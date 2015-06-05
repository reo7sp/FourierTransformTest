/*
 * Copyright (c) 2015 Reo_SP
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.reo7sp.fourier.math

import scala.math._

object FunctionGenerators {
  def trianglePeriodicFunc(epsilon: Double = Pi): Double => Double = {
    x => {
      val period = 2 * Pi
      var periodicX = (x + Pi) - (period * ((x + Pi) / period).toInt)
      if (periodicX < 0) periodicX += period

      (if (periodicX <= (period - epsilon)) {
        periodicX / (period - epsilon)
      } else {
        period / epsilon - periodicX / epsilon
      }) * 2 - 1
    }
  }

  def sineWaveWithEnds(humpsCount: Int): Double => Double = {
    require(humpsCount >= 0)
    x => {
      val period = 2 * Pi
      if (-humpsCount / 2 < x / period && x / period < humpsCount / 2) {
        sin(x)
      } else {
        0
      }
    }
  }

  def cosineWaveWithEnds(humpsCount: Int): Double => Double = {
    require(humpsCount >= 0)
    x => {
      val period = 2 * Pi
      if (-humpsCount / 2 < x / period && x / period < humpsCount / 2) {
        cos(x)
      } else {
        0
      }
    }
  }
}
