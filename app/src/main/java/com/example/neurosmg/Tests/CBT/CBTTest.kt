package com.example.neurosmg.Tests.CBT

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentCBTTestBinding

class CBTTest : Fragment() {
    lateinit var binding: FragmentCBTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private lateinit var allSquares: List<TextView>
    private lateinit var randomSquares: List<TextView>
    private lateinit var sequenceSquares: List<TextView>
    private lateinit var visibleSquares: List<TextView>
    private lateinit var timer: CountDownTimer

    private var currentSequenceLength = 3
    private val maxSequenceLength = 9
    private var sequence: List<Int> = emptyList()
    private var isShowingSequence = false

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
        binding = FragmentCBTTestBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.CBTTest)
        allSquares = listOf(
            binding.square1, binding.square2, binding.square3, binding.square4,
            binding.square5, binding.square6, binding.square7, binding.square8,
            binding.square9, binding.square10, binding.square11, binding.square12,
            binding.square13, binding.square14, binding.square15, binding.square16,
            binding.square17, binding.square18, binding.square19, binding.square20,
            binding.square21, binding.square22, binding.square23, binding.square24
            // Добавьте square5 - square24
        )

        allSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }

        binding.btnStart.setOnClickListener {
            startTest()
        }
    }

    private fun startTest() {
        // Show 9 random squares
        visibleSquares = allSquares.shuffled().take(9)
        visibleSquares.forEach { square ->
            square.setBackgroundResource(R.color.black) // Set your square color here
            square.visibility = View.VISIBLE
        }

        // Generate the sequence for the visible squares
        sequence = (0 until currentSequenceLength).map { visibleSquares.indices.random() }
        sequenceSquares = sequence.map { visibleSquares[it] }

        // Show the sequence
        showSequence()
    }

    private fun showSequence() {
        isShowingSequence = true
        sequence.forEachIndexed { index, squareIndex ->
            val square = visibleSquares[squareIndex]
            Log.d("MyLog", "$squareIndex")
            val delay = (index + 1) * 1000L
            square.postDelayed({
                square.setBackgroundResource(R.drawable.green_ball) // Set your highlighted square color here
            }, delay)
        }

        // Allow user to click squares after showing the sequence
        binding.root.postDelayed({
            visibleSquares.forEach { square ->
                square.setBackgroundResource(R.color.black) // Set your square color here
                square.setOnClickListener {
                    if (isShowingSequence) return@setOnClickListener // Disable clicks during sequence display
                    onSquareClick(square)
                }
            }
            isShowingSequence = false
        }, (currentSequenceLength + 1) * 1000L)
    }

    private fun showNextRandomSquares() {
        visibleSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }
        startTest()
    }

    private fun onSquareClick(square: TextView) {
        if (!isShowingSequence) {
            if (square in sequenceSquares) {
                sequenceSquares = sequenceSquares.drop(1)
                square.setBackgroundResource(R.drawable.green_ball) // Set your highlighted square color here
                if (sequenceSquares.isEmpty()) {
                    currentSequenceLength++
                    if (currentSequenceLength > maxSequenceLength) {
                        finishTest()
                    } else {
                        showNextRandomSquares()
                    }
                }
            } else {
                // User made a mistake, restart the test
                showNextRandomSquares()
            }
        }
    }

    private fun finishTest() {
        // Test finished, handle the result
        // TODO: Implement your finish test logic here
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CBTTest()
    }
}