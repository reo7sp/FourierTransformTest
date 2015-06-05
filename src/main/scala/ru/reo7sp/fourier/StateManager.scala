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

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.Timer

object StateManager {
  var time = 0.0
  var timeSpeed = 0.0

  private val _updatesPerSecond = 10
  private var _lastUpdateTime = System.currentTimeMillis / 1000.0 // in sec

  private def update(delta: Double = 1) { // in sec
    time += timeSpeed * delta
  }

  private val updateTimer = new Timer(1000 / _updatesPerSecond, new ActionListener {
    override def actionPerformed(actionEvent: ActionEvent): Unit = {
      val now = System.currentTimeMillis / 1000.0 // in sec
      update(now - _lastUpdateTime)
      _lastUpdateTime = now
    }
  })

  def startManager(): Unit = updateTimer.start()
}