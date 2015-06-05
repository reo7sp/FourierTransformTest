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

import java.awt.image.BufferedImage

import ru.reo7sp.fourier.graphs.Graph

import scala.collection.mutable

class GraphsManager {
  val graphsData = mutable.ArrayBuffer[GraphData]()
  val graphs = new mutable.Buffer[Graph] {
    override def apply(n: Int): Graph = graphsData.apply(n).graph

    override def update(n: Int, newelem: Graph): Unit = graphsData.update(n, new GraphData(newelem))

    override def clear(): Unit = graphsData.clear()

    override def length: Int = graphsData.length

    override def remove(n: Int): Graph = graphsData.remove(n).graph

    override def +=:(elem: Graph): this.type = {
      graphsData.+=:(new GraphData(elem))
      this
    }

    override def +=(elem: Graph): this.type = {
      graphsData.+=(new GraphData(elem))
      this
    }

    override def insertAll(n: Int, elems: Traversable[Graph]): Unit = graphsData.insertAll(n, elems.map(new GraphData(_)))

    override def iterator: Iterator[Graph] = new Iterator[Graph] {
      private val graphsDataIter = graphsData.iterator

      override def hasNext: Boolean = graphsDataIter.hasNext
      override def next(): Graph = graphsDataIter.next().graph
    }
  }

  case class GraphData private[GraphsManager](graph: Graph) {
    var renderCache: BufferedImage = null
    var renderCacheUpdateTime = 0L
  }
}
