package com.example.neurosmg.tests.tmt

import android.annotation.SuppressLint
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
import android.widget.Toast

class LabyrinthView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    interface LabyrinthCompletionListener {
        fun onLabyrinthCompleted(steps: Int, data: MutableList<List<String>>)
    }

    fun setLabyrinthCompletionListener(listener: LabyrinthCompletionListener) {
        completionListener = listener
    }


    private var completionListener: LabyrinthCompletionListener? = null
    private val data = mutableListOf<List<String>>()

    private val userPath = mutableListOf<Point>()
    private var tvLabSteps = 1
    private val userPathPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 20f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val finishX = 16
    private var finishY = 3
    private var isFinishMessageShown = false
    private var isCollisionLogged = false

    private val paint = Paint()
    private var cellSize = 0
    private val labyrinth1 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth2 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth3 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth4 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth5 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth6 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )

    private val labyrinth7 = mutableListOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0)
    )
    private var labyrinthData = labyrinth1
    private val labyrinthList = listOf(labyrinth1, labyrinth2, labyrinth3, labyrinth4, labyrinth5, labyrinth6, labyrinth7)

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Измеряем ширину ConstraintLayout
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)

        // Вычисляем размер ячейки так, чтобы она соответствовала ширине ConstraintLayout
        cellSize = parentWidth / labyrinth1[0].size

        // Устанавливаем размеры для нашего View
        setMeasuredDimension(parentWidth, cellSize * labyrinth1.size)
    }

    private fun handleLabyrinthCompletion() {
        tvLabSteps++
        completionListener?.onLabyrinthCompleted(tvLabSteps, data)
        if (tvLabSteps > 20) {
            userPath.clear()
            isFinishMessageShown = false
            isCollisionLogged = false
        } else {
            val randomIndex = (labyrinthList.indices).random()

            labyrinthData = labyrinthList[randomIndex]
            finishY = if (randomIndex == 0 || randomIndex == 5) {
                3
            } else {
                1
            }

            userPath.clear()
            isFinishMessageShown = false
            isCollisionLogged = false
            invalidate()
        }
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (isValidMove(x, y)) {
                    userPath.add(Point(x, y))

                    postInvalidate()

                    if (!isFinishMessageShown && x / cellSize == finishX && y / cellSize == finishY) {
                        showToast("Вы прошли лабиринт!")
                        handleLabyrinthCompletion()
                        isFinishMessageShown = true
                    }

                    if (!isCollisionLogged && !isValidMove(x, y)) {
                        Log.d("MyLog", "collision")
                        isCollisionLogged = true
                    }
                }

                saveData("move", x, y)
            }
            MotionEvent.ACTION_UP -> {
                handler.postDelayed({
                    userPath.clear()
                    postInvalidate()
                }, 50)
                isFinishMessageShown = false
                isCollisionLogged = false
            }
        }
        return true
    }

    private fun saveData(s: String, x: Int, y: Int) {
        val unixTimestamp = System.currentTimeMillis()
        val dynamicRow = mutableListOf(
            unixTimestamp.toString(),
            tvLabSteps.toString(),
            x.toString(),
            y.toString(),
            s
        )
        data.add(dynamicRow)
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun isValidMove(x: Int, y: Int): Boolean {
        val row = y / cellSize
        val col = x / cellSize

        if (row < 0 || row >= labyrinthData.size || col < 0 || col >= labyrinthData[0].size) {
            return false
        }

        if (labyrinthData[row][col] == 1) {
//            Log.d("MyLog", "collision")
            saveData("collision", x, y)
            return false
        }

        return true
    }

}
