/*
 * Copyright (c) 2015 Reo_SP
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.reo7sp.fourier.ui

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JLabel, JPanel, JTextField}

import ru.reo7sp.fourier.Core

import scala.collection.mutable

class ConfigPanel extends JPanel with ActionListener {
  private val _configurables = new mutable.HashMap[String, JTextField]

  Core.appId match {
    case 1 =>
      addConfigurable("epsilon", "ε", 0)
      addConfigurable("fourierN", "n", 5)
    case 2 =>
      addConfigurable("timeSpeed", "timeSpeed", 0)
    case 3 =>
      addConfigurable("timeSpeed", "timeSpeed", 0)
    case 4 =>
      addConfigurable("timeSpeed", "timeSpeed", 0)
  }

  def getValue(name: String) = _configurables(name).getText.toDouble

  private def addConfigurable(name: String, title: String, initVal: Double) {
    add(new JLabel(title + "="))
    val textField = new JTextField(initVal.toString, 3)
    textField.addActionListener(this)
    add(textField)
    _configurables(name) = textField
  }

  override def actionPerformed(e: ActionEvent) {
    Core.updateFourierGraph()
  }
}
