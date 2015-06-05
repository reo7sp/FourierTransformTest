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

import ru.reo7sp.fourier.math.MathUtils

import scala.math._

class InvertedFourierTransformOfWaveGraph(var graph: Graph, var kExponent: Double) extends Graph {
  protected def omega(k: Double) = pow(k, kExponent)

  protected val _a = 1 / sqrt(2 * Pi)

  override def check(x: Double): Boolean = super.check(x) && graph.check(x)

  //override protected def calcY(x: Double) = _a * MathUtils.calculateIntegral(k => graph.y(k) * sin(k * x - _omega * time), -15, 15)
  override protected def calcY(x: Double) = {
//    val b = MathUtils.calculateIntegral(k => graph.y(k) * sin(k * x - omega(k) * time), -12, -8)
    val c = MathUtils.calculateIntegral(k => graph.y(k) * sin(k * x - omega(k) * time), 8, 12)
    _a * (/*b + */c)
  }
 }
