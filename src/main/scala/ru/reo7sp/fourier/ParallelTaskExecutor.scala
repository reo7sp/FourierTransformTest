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

import scala.collection.mutable

object ParallelTaskExecutor {
  private val _tasks = new mutable.HashMap[String, Task]
  private val _threads = new mutable.HashSet[Thread]
  private var _runningThreadsCount = 0

  for (i <- 1 to Runtime.getRuntime.availableProcessors) {
    val thread = new TaskExecutor
    _threads += thread
    thread.start()
  }

  def stopAll(): Unit = {
    _threads.foreach(_.interrupt())
    _threads.clear()
  }

  def checkTask(id: String) = _tasks.synchronized {
    if (_tasks.contains(id)) {
      _tasks(id).lastCheck = System.currentTimeMillis
      true
    } else {
      false
    }
  }

  def addTask(id: String, func: () => Unit): Unit = _tasks.synchronized(_tasks(id) = new Task(func))

  def taskCount = _tasks.size

  def threadsCount = _threads.size

  def runningThreadsCount = _runningThreadsCount

  private class TaskExecutor extends Thread {
    setDaemon(true)

    override def run(): Unit = while (!isInterrupted) {
      try {
        var task: Task = null
        _tasks.synchronized {
          if (_tasks.nonEmpty) {
            val tasksQueueVal = _tasks.head
            task = tasksQueueVal._2
            _tasks -= tasksQueueVal._1
          }
        }
        if (task == null) {
          Thread.sleep(500)
        } else if (System.currentTimeMillis() - task.lastCheck < 1000) {
          _threads.synchronized(_runningThreadsCount += 1)
          task.func()
          _threads.synchronized(_runningThreadsCount -= 1)
        }
      } catch {
        case e: Throwable => println(e)
      }
    }
  }

  private case class Task(func: () => Unit) {
    var lastCheck = System.currentTimeMillis
  }
}
