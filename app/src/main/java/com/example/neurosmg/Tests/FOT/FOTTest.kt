package com.example.neurosmg.Tests.FOT

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentFOTTestBinding
import java.util.Random

class FOTTest : Fragment() {

    lateinit var binding: FragmentFOTTestBinding

    private var mainActivityListener: MainActivityListener? = null

    private lateinit var instructionsTextView: TextView
    private lateinit var startButton: Button
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val random = Random()
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
        startButton.setOnClickListener {
            startTest()
        }
        return binding.root
    }

    private fun startTest() {
        instructionsTextView.text = "Нажимайте как можно быстрее!"
        Log.d("MyLog", "Start test")
        // Запускаем таймер
        startTime = System.currentTimeMillis()

        // Отложенное завершение теста через 30 секунд
        Handler(Looper.getMainLooper()).postDelayed({
            endTest()
        }, 3000) // 30 секунд в миллисекундах
    }

    private fun endTest() {
        endTime = System.currentTimeMillis()
        Log.d("MyLog", "End test")
        val timeTaken = endTime - startTime

        instructionsTextView.text = "Время выполнения: $timeTaken мс"
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
}