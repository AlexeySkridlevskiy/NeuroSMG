package com.example.neurosmg.tests.fot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

interface CanvasViewCallback {
    fun onCanvasViewTouch()
    fun onCanvasFirstTouch()
    fun onCanvasClickNoTest()
    fun onCanvasData(x: Float, y: Float)
    fun onCanvasDataMotion(timeInSeconds: Long, durationMillis: Long)
}

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val points = mutableListOf<Pair<Float, Float>>()
    private var touchCount: Int = 0
    private var canvasViewCallback: CanvasViewCallback? = null
    private var startIndex: Int = 2
    var touchEnabled = true

    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var touchStartTimeUnixTimestamp: Long = 0
    private val timerHandler = Handler()

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
            canvas.drawCircle(point.first, point.second, 16f, paint)
        }
    }

    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime
            // Здесь вы можете обновлять интерфейс с текущим временем
            // Например, отобразить время на вашем экране или выполнить другие действия с ним
            timerHandler.postDelayed(this, 10) // Обновление каждую секунду
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(!touchEnabled){
            return false
        }

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
                    startTime = SystemClock.uptimeMillis()
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    timerHandler.postDelayed(updateTimer, 0)
                    canvasViewCallback?.onCanvasData(x, y)
                    points.add(Pair(x, y))
                    invalidate() // Обновление отображения
                    canvasViewCallback?.onCanvasViewTouch()
                }
                MotionEvent.ACTION_UP -> {
                    timerHandler.removeCallbacks(updateTimer)
                    val timeInSeconds = elapsedTime

                    timerHandler.removeCallbacks(updateTimer)
                    canvasViewCallback?.onCanvasDataMotion(timeInSeconds, touchStartTimeUnixTimestamp)
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