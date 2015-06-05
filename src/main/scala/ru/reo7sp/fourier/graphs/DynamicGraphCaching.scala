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

import ru.reo7sp.fourier.StateManager

trait DynamicGraphCaching extends GraphCaching {
  var maxCacheLifetime = 100 // in ms
  
  private var _lastCheckTime = System.currentTimeMillis
  private var _time = 0.0

  override def check(x: Double): Boolean = {
    val result = super.check(x)
    
    val now = System.currentTimeMillis
    if (!result && canBeCalculated(x)) {
      _lastCheckTime = now
    }
    if (now - _lastCheckTime > maxCacheLifetime && StateManager.timeSpeed != 0) {
      _time = StateManager.time
      resetInternalData()
    }
    
    result
  }

  protected override def time = _time
}
