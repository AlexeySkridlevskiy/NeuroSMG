package com.example.neurosmg.tests.tmt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LabyrinthView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val cellSize = 50  // Size of each cell in pixels
    private val labyrinthData = mutableListOf(
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(1, 0, 1, 1, 1, 1, 1, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 0, 1),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1)
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
    }
}
