package com.example.neurosmg.tests.tmt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class LabyrinthView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val userPath = mutableListOf<Point>()
    private val userPathPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val paint = Paint()
    private val cellSize = 81
    private val labyrinthData = mutableListOf(
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    )

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in labyrinthData.indices) {
            for (col in labyrinthData[row].indices) {
                val cellValue = labyrinthData[row][col]
                if (cellValue == 1) {
                    val left = col * cellSize.toFloat()
                    val top = row * cellSize.toFloat()
                    val right = (col + 1) * cellSize.toFloat()
                    val bottom = (row + 1) * cellSize.toFloat()
                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }

        if (userPath.isNotEmpty()) {
            val path = Path()
            val startPoint = userPath[0]
            path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
            for (i in 1 until userPath.size) {
                val point = userPath[i]
                path.lineTo(point.x.toFloat(), point.y.toFloat())
            }
            canvas.drawPath(path, userPathPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (isValidMove(x, y)) {
                    userPath.add(Point(x, y))
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

    private fun isValidMove(x: Int, y: Int): Boolean {
        val row = y / cellSize
        val col = x / cellSize

        if (row < 0 || row >= labyrinthData.size || col < 0 || col >= labyrinthData[0].size) {
            return false
        }

        if (labyrinthData[row][col] == 1) {
            Log.d("MyLog", "Столкновение")
            return false
        }

        return true
    }

}
