package com.example.neurosmg.Tests.FOT

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

interface CanvasViewCallback {
    fun onCanvasViewTouch()
}

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val points = mutableListOf<Pair<Float, Float>>()
    private var touchCount: Int = 0
    private var canvasViewCallback: CanvasViewCallback? = null

    init {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
    }

    fun setCanvasViewCallback(callback: CanvasViewCallback) {
        canvasViewCallback = callback
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (point in points) {
            canvas.drawCircle(point.first, point.second, 10f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(TestActive.KEY_ACTIVE_FOT_TEST){
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    points.add(Pair(x, y))
                    invalidate()
                    canvasViewCallback?.onCanvasViewTouch()
                }
            }
            return true
        }else{
            return true
        }
    }

    fun clearPoints() {
        touchCount = 0
        points.clear()
        invalidate()
    }
}