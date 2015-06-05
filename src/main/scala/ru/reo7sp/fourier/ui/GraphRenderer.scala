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

import java.awt.event._
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Color, Graphics, Graphics2D}
import javax.swing.{JPanel, Timer}

import ru.reo7sp.fourier.graphs.{DynamicGraphCaching, Graph}
import ru.reo7sp.fourier.{GraphsManager, ParallelTaskExecutor, StateManager}

class GraphRenderer extends JPanel {
  private val kMaxFps = 30
  private val kColors = Array(Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.GRAY)
  private val kBannerBackground = new Color(0.5f, 0.5f, 0.5f, 0.125f)
  private val kInfoBaseX = 16
  private val kInfoBaseY = 24
  private val kInfoGapY = 20
  private val kMoveDistance = 10
  private val kScaleSpeed = 0.005f

  val graphsManager = new GraphsManager
  var xShift = 0
  var yShift = 0
  var scaleX = 0.01f
  var scaleY = 0.01f

  private var _lastScaleEditTime = System.currentTimeMillis // in ms
  private var _lastFpsUpdate = System.currentTimeMillis // in ms
  private var _fps = 0
  private var _fpsTemp = 0
  private var _renderCacheCustomXShift = 0
  private var _renderCacheCustomYShift = 0

  addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {
      GraphRenderer.this.requestFocus()
    }
  })
  addMouseMotionListener(new MouseAdapter() {
    var lastDragTime = 0L
    var lastMouseX = 0
    var lastMouseY = 0

    override def mouseDragged(e: MouseEvent) {
      if (System.currentTimeMillis - lastDragTime < 250) {
        val dx = e.getX - lastMouseX
        val dy = e.getY - lastMouseY
        xShift += dx
        yShift += dy
        _renderCacheCustomXShift += dx
        _renderCacheCustomYShift += dy
      }
      lastMouseX = e.getX
      lastMouseY = e.getY
      lastDragTime = System.currentTimeMillis
    }
  })
  addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent) {
      e.getKeyChar match {
        case '-' => scaleX += kScaleSpeed
        case '=' => scaleX -= kScaleSpeed
        case '_' => scaleY += kScaleSpeed
        case '+' => scaleY -= kScaleSpeed
        case _ =>
      }
      if (scaleX <= 0) scaleX = kScaleSpeed
      if (scaleY <= 0) scaleY = kScaleSpeed

      e.getKeyCode match {
        case KeyEvent.VK_RIGHT => xShift -= kMoveDistance
        case KeyEvent.VK_LEFT => xShift += kMoveDistance
        case KeyEvent.VK_UP => yShift += kMoveDistance
        case KeyEvent.VK_DOWN => yShift -= kMoveDistance
        case _ =>
      }

      _lastScaleEditTime = System.currentTimeMillis
    }
  })

  new Timer(1000 / kMaxFps, new ActionListener {
    override def actionPerformed(actionEvent: ActionEvent): Unit = GraphRenderer.this.repaint()
  }).start()

  setFocusable(true)

  def this(graphs: Graph*) {
    this()
    graphsManager.graphs ++= graphs
  }

  override def paintComponent(rawG: Graphics) = {
    val g = rawG.asInstanceOf[Graphics2D]
    val now = System.currentTimeMillis
    val baseX = getWidth / 2 + xShift
    val baseY = getHeight / 2 + yShift

    def scaledX(x: Float) = x * scaleX
    def scaledY(y: Float) = y * scaleY

    def updateFps(): Unit = {
      if (now - _lastFpsUpdate > 1000) {
        _fps = _fpsTemp
        _fpsTemp = 0
        _lastFpsUpdate = now
      }
      _fpsTemp += 1
    }

    def clear(g: Graphics2D): Unit = {
      //g.clearRect(0, 0, getWidth, getHeight)
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR))
      g.fillRect(0, 0, getWidth, getHeight)
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER))
    }

    def drawCoordinatePlane(g: Graphics2D): Unit = {
      g.setColor(Color.LIGHT_GRAY)
      g.drawLine(0, baseY, getWidth, baseY)
      g.drawLine(baseX, 0, baseX, getHeight)
      g.setColor(Color.GRAY)
      for (x <- 0 to getWidth; if (x - baseX) % 100 == 0) {
        g.drawString(scaledX(x - baseX).toInt.toString, x, baseY)
        g.drawString(scaledX(x - baseX).toInt.toString, x, baseY)
      }
      for (y <- 0 to getHeight; if (y - baseY) % 100 == 0) {
        g.drawString(scaledY(-y + baseY).toInt.toString, baseX, y)
        g.drawString(scaledY(-y + baseY).toInt.toString, baseX, y)
      }
    }

    def drawGraph(g: Graphics2D, color: Color, graphData: graphsManager.GraphData): Unit = {
      val graph = graphData.graph

      g.setColor(color)

      var isGraphFullyCalculated = true
      var isReadyToRender = false
      var lastX = 0
      var lastY = 0
      for (x <- (-getWidth / 2) - xShift until (getWidth / 2) - xShift) {
        val currentX = scaledX(x)
        if (graph.check(currentX)) {
          val currentY = (graph.y(scaledX(x)) / scaleY).toInt
          if (isReadyToRender) {
            g.drawLine(baseX + lastX, baseY - lastY, baseX + x, baseY - currentY)
          }
          lastX = x
          lastY = currentY
          isReadyToRender = true
        } else {
          isGraphFullyCalculated = false
        }
      }

      if (isGraphFullyCalculated && now - graphData.renderCacheUpdateTime > 100 && graph.isInstanceOf[DynamicGraphCaching]) {
        graphData.renderCacheUpdateTime = now
        if (graphData.renderCache == null || graphData.renderCache.getWidth != getWidth || graphData.renderCache.getHeight != getHeight) {
          graphData.renderCache = new BufferedImage(getWidth, getHeight, BufferedImage.TYPE_4BYTE_ABGR)
        }
        clear(graphData.renderCache.getGraphics.asInstanceOf[Graphics2D])
        drawGraph(graphData.renderCache.getGraphics.asInstanceOf[Graphics2D], Color.LIGHT_GRAY, graphData)
        _renderCacheCustomXShift = 0
        _renderCacheCustomYShift = 0
      }
    }

    def drawBanner(g: Graphics2D): Unit = if (!isFocusOwner) {
      g.setColor(kBannerBackground)
      g.fillRect(0, 0, getWidth, getHeight)
    }

    def drawInfo(g: Graphics2D): Unit = {
      g.setColor(Color.BLACK)
      g.drawString("FPS: " + _fps, kInfoBaseX, kInfoBaseY + kInfoGapY * 0)
      g.drawString("Memory: " + ((Runtime.getRuntime.totalMemory - Runtime.getRuntime.freeMemory) / 1024 / 1024) + "M/" + (Runtime.getRuntime.totalMemory / 1024 / 1024) + "M", kInfoBaseX, kInfoBaseY + kInfoGapY * 1)
      g.drawString("Tasks: " + ParallelTaskExecutor.taskCount, kInfoBaseX, kInfoBaseY + kInfoGapY * 2)
      g.drawString("TaskExecutor threads: " + ParallelTaskExecutor.runningThreadsCount + "/" + ParallelTaskExecutor.threadsCount, kInfoBaseX, kInfoBaseY + kInfoGapY * 3)
      g.drawString("Time: " + StateManager.time, kInfoBaseX, kInfoBaseY + kInfoGapY * 4)
      g.drawString("ScaleX: " + scaleX, kInfoBaseX, kInfoBaseY + kInfoGapY * 5)
      g.drawString("ScaleY: " + scaleY, kInfoBaseX, kInfoBaseY + kInfoGapY * 6)
    }

    // draw component
    updateFps()
    clear(g)
    g.setColor(getBackground)
    g.fillRect(0, 0, getWidth, getHeight)
    drawCoordinatePlane(g)
    var i = -1
    for (graphData <- graphsManager.graphsData) {
      i += 1
      if (graphData.renderCache != null) {
        g.drawImage(graphData.renderCache, _renderCacheCustomXShift, _renderCacheCustomYShift, null)
      }
      drawGraph(g, kColors(i % kColors.length), graphData)
    }
    drawBanner(g)
    drawInfo(g)
  }
}
