package com.example.neurosmg.Tests.FOT

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentFOTTestBinding
import java.util.Random

class FOTTest : Fragment(), CanvasViewCallback {

    lateinit var binding: FragmentFOTTestBinding

    private var mainActivityListener: MainActivityListener? = null

    private lateinit var instructionsTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startButton: Button
    private lateinit var canvasView: CanvasView
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var touchCount: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFOTTestBinding.inflate(inflater)
        instructionsTextView = binding.textView8
        startButton = binding.button
        canvasView = binding.canvasView
        canvasView.setCanvasViewCallback(this)
        startButton.setOnClickListener {
            startTest()
        }
        return binding.root
    }

    private fun startTest() {
        TestActive.KEY_ACTIVE_FOT_TEST = true
        touchCount = 0
        canvasView.clearPoints()

        // Отложенное завершение теста через 30 секунд
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvTime.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                endTest()
            }
        }.start()
    }

    private fun endTest() {
        TestActive.KEY_ACTIVE_FOT_TEST = false
        endTime = System.currentTimeMillis()

        val touchesPerSecond = touchCount.toDouble() / 30
        binding.tvClicks.text = touchCount.toString()

        instructionsTextView.text = "Частота нажатий: $touchesPerSecond нажатий/сек"

    }

    override fun onPause() {
        super.onPause()
        canvasView.clearPoints()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.FOTTest)
    }
    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }
    companion object {
        @JvmStatic
        fun newInstance() = FOTTest()
    }

    override fun onCanvasViewTouch() {
        if(TestActive.KEY_ACTIVE_FOT_TEST){
            touchCount++
            updateTouchCountTextView()
        }
    }

    private fun updateTouchCountTextView() {
        binding.tvClicks.text = touchCount.toString()
    }
}