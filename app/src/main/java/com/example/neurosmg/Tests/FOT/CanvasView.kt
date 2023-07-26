package com.example.neurosmg.Tests.FOT

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext

interface CanvasViewCallback {
    fun onCanvasViewTouch()
    fun onCanvasFirstTouch()
    fun onCanvasClickNoTest()
}

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val points = mutableListOf<Pair<Float, Float>>()
    private var touchCount: Int = 0
    private var canvasViewCallback: CanvasViewCallback? = null
    private var startIndex: Int = 2

    init {
        paint.color = Color.BLUE
        paint.style = Paint.Style.FILL
    }

    fun setCanvasViewCallback(callback: CanvasViewCallback) {
        canvasViewCallback = callback
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (point in points) {
            canvas.drawCircle(point.first, point.second, 32f, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (startIndex == 0){
            canvasViewCallback?.onCanvasFirstTouch()
            startIndex++
        }else{
            canvasViewCallback?.onCanvasClickNoTest()
        }
        if (TestActive.KEY_ACTIVE_FOT_TEST){
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    points.add(Pair(x, y))
                    invalidate() // Обновление отображения
                    canvasViewCallback?.onCanvasViewTouch()
                }
            }
        }
        return true
    }

    fun clearPoints() {
        touchCount = 0
        startIndex = 0
        points.clear()
        invalidate()
    }
}