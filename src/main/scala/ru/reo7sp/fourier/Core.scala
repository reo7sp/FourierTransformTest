/*
 * Copyright (c) 2015 Reo_SP
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.reo7sp.fourier

import ru.reo7sp.fourier.graphs._
import ru.reo7sp.fourier.math.FunctionGenerators
import ru.reo7sp.fourier.ui.MainWindow

import scala.math._

object Core {
  val appId = 4

  private val _window = new MainWindow

  def main(args: Array[String]) {
    System.setProperty("sun.java2d.opengl", "true")

    appId match {
      case 1 =>
        val func = FunctionGenerators.trianglePeriodicFunc(conf.getValue("epsilon"))
        graphs += new FunctionGraph(func)
        graphs += new FourierSeriesGraph(func, conf.getValue("fourierN").toInt) with GraphCaching
      case 2 =>
        val wave = new EndedWaveGraph(1, 0.5, 0, 50)
        val fourierTransformOfWave = new FourierTransformGraph(wave) with DynamicGraphCaching
        graphs += wave
        graphs += fourierTransformOfWave
      case 3 =>
        val wave = new MultipliedWaveGraph(10, 0.5, 0, x => pow(E, -0.01 * x * x))
        val fourierTransformOfWave = new FourierTransformGraph(wave) with DynamicGraphCaching
        graphs += wave
        graphs += fourierTransformOfWave
        graphs += new InvertedFourierTransformGraph(fourierTransformOfWave) with DynamicGraphCaching
      case 4 =>
        val wave = new FunctionGraph(x => sin(10 * x) * pow(E, -0.01 * x * x))
        val fourierTransformOfWave = new FourierTransformGraph(wave) with GraphCaching
        graphs += fourierTransformOfWave
        graphs += new InvertedFourierTransformOfWaveGraph(fourierTransformOfWave, 0.5) with DynamicGraphCaching
        //graphs += new WaveGraph(10, 0.5, 0)
    }
    _window.setVisible(true)
    StateManager.startManager()
  }

  def updateFourierGraph(): Unit = {
    appId match {
      case 1 =>
        val epsilon = conf.getValue("epsilon").toInt
        val n = conf.getValue("fourierN").toInt
        val func = FunctionGenerators.trianglePeriodicFunc(epsilon)

        val graph0 = graphs.head.asInstanceOf[FunctionGraph]
        graph0.func = func

        val graph1 = graphs(1).asInstanceOf[FourierSeriesGraph]
        graph1.func = func
        graph1.n = n
      case 2 =>
        StateManager.timeSpeed = conf.getValue("timeSpeed")
      case 3 =>
        StateManager.timeSpeed = conf.getValue("timeSpeed")
      case 4 =>
        StateManager.timeSpeed = conf.getValue("timeSpeed")
    }
    graphs.foreach(graph => graph.resetInternalData())
  }

  private def graphs = _window.graphRenderer.graphsManager.graphs

  private def conf = _window.configPanel
}
