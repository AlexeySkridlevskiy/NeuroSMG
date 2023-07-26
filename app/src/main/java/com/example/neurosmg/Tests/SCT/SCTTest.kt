package com.example.neurosmg.Tests.SCT

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentSCTTestBinding
import kotlin.random.Random

class SCTTest : Fragment() {

    private lateinit var textView: TextView
    private lateinit var buttonRed: Button
    private lateinit var buttonBlue: Button
    private val handler = Handler()

    private val colors = listOf(Color.RED, Color.BLUE)
    private val words = listOf("Красный", "Синий")

    private var score = 0
    private var totalAttempts = 0

    lateinit var binding: FragmentSCTTestBinding
    private var mainActivityListener: MainActivityListener? = null

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
        binding = FragmentSCTTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.SCTTest)
        textView = binding.tvColorText
        buttonRed = binding.button5
        buttonBlue = binding.button6

        setupTest()
        setupButtonClickListeners()
    }

    private fun setupTest() {
        totalAttempts = 0
        score = 0
        showRandomWord()
    }

    private fun setupButtonClickListeners() {
        buttonRed.setOnClickListener {
            binding.tvColorText.visibility = View.INVISIBLE
            checkAnswer(Color.RED)
        }

        buttonBlue.setOnClickListener {
            binding.tvColorText.visibility = View.INVISIBLE
            checkAnswer(Color.BLUE)
        }
    }

    private fun showRandomWord() {
        val randomIndex = Random.nextInt(0, words.size)
        val randomWord = words[randomIndex]
        val randomColor = colors[Random.nextInt(0, colors.size)]

        handler.postDelayed({
            textView.visibility = View.VISIBLE
            textView.text = randomWord
            textView.setTextColor(randomColor)
        }, 100)
    }

    private fun checkAnswer(selectedColor: Int) {
        totalAttempts++

        val displayedColor = textView.currentTextColor
        if (displayedColor == selectedColor) {
            score++
        }

        if (totalAttempts < 10) {
            showRandomWord()
        } else {
            showTestResult()
        }
    }

    private fun showTestResult() {
        textView.visibility = View.VISIBLE
        val accuracy = score * 100 / totalAttempts
        val resultText = "Тест завершен!\n\nТочность: $accuracy%"
        textView.text = resultText
        buttonRed.isEnabled = false
        buttonBlue.isEnabled = false
    }
    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }
    companion object {
        @JvmStatic
        fun newInstance() = SCTTest()
    }
}