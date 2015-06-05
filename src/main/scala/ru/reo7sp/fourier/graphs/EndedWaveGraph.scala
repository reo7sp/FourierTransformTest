/*
 * Copyright (c) 2015 Reo_SP
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.reo7sp.fourier.graphs

import scala.math._

class EndedWaveGraph(k: Double, kExponent: Double, initialPhase: Double, var humpsCount: Double) extends WaveGraph(k, kExponent, initialPhase) {
  require(humpsCount >= 0)

  override protected def calcY(x: Double): Double = {
    val period = 2 * Pi
    val a = k * x - _omega * time + initialPhase
    val b = a / period
    val c = humpsCount / 2
    if (-c < b && b < c) {
      super.calcY(x)
    } else {
      0
    }
  }
}
