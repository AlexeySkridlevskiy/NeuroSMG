package com.example.neurosmg.tests.cbt

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentCBTTestBinding
import com.example.neurosmg.testsPage.TestsPage
import com.example.neurosmg.utils.exitFullScreenMode

class CBTTest : Fragment() {
    lateinit var binding: FragmentCBTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private lateinit var allSquares: List<TextView>
    private lateinit var sequenceSquares: List<TextView>
    private lateinit var visibleSquares: List<TextView>
    private lateinit var timer: CountDownTimer

    private var currentSequenceLength = 3
    private val maxSequenceLength = 5
    private var sequence: List<Int> = emptyList()
    private var isShowingSequence = false
    private var expectedIndex = 0
    private var stepsIndex = 1
    private val maxStepsIndex = 20

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
        infoDialogStartTest()
        mainActivityListener?.updateToolbarState(ToolbarState.CBTTest)
        allSquares = listOf(
            binding.square1, binding.square2, binding.square3, binding.square4,
            binding.square5, binding.square6, binding.square7, binding.square8,
            binding.square9, binding.square10, binding.square11, binding.square12,
            binding.square13, binding.square14, binding.square15, binding.square16,
            binding.square17, binding.square18, binding.square19, binding.square20,
            binding.square21, binding.square22, binding.square23, binding.square24
        )

        allSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }

        binding.btnStart.setOnClickListener {
            infoDialogInstruction()
        }
    }

    private fun startTest() {
        if(stepsIndex==maxStepsIndex){
            finishTest()
        }
        binding.tvSteps.text = stepsIndex.toString()
        stepsIndex++
        expectedIndex = 0
        visibleSquares = allSquares.shuffled().take(9)
        visibleSquares.forEach { square ->
            square.setBackgroundResource(R.color.cbt_color_square)
            square.visibility = View.VISIBLE
        }

        sequence = visibleSquares.shuffled().take(currentSequenceLength).map { visibleSquares.indexOf(it) }
        sequenceSquares = sequence.map { visibleSquares[it] }

        // Add a delay before showing the sequence
        binding.root.postDelayed({
            showSequence()
        }, 1000L) // Adjust the delay time as needed
    }

    private fun showSequence() {
        isShowingSequence = true
        showNextSquare(0)
    }

    private fun showNextSquare(index: Int) {
        if (index >= sequence.size) {
            resetSquares()
            isShowingSequence = false
            return
        }

        val squareIndex = sequence[index]
        val square = visibleSquares[squareIndex]
        square.setBackgroundResource(R.color.cbt_color_square_user)

        binding.root.postDelayed({
            square.setBackgroundResource(R.color.cbt_color_square)
            showNextSquare(index + 1)
        }, 1000L)
    }

    private fun resetSquares() {
        visibleSquares.forEach { square ->
            square.setBackgroundResource(R.color.cbt_color_square)
            square.setOnClickListener {
                if (isShowingSequence) return@setOnClickListener
                onSquareClick(square)
            }
        }
    }

    private fun showNextRandomSquares() {
        visibleSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }

        startTest()
    }

    private fun onSquareClick(square: TextView) {
        if (!isShowingSequence) {
            if (square == sequenceSquares[expectedIndex]) {
                square.setBackgroundResource(R.color.cbt_color_square_user)
                expectedIndex++
                if (expectedIndex == sequenceSquares.size) {
                    sequenceSquares = emptyList()
                    expectedIndex = 0

                    currentSequenceLength++
                    if (currentSequenceLength > maxSequenceLength) {
                        finishTest()
                    } else {
                        showNextRandomSquares()
                    }
                }
            } else {
                if (currentSequenceLength > 5) {
                    currentSequenceLength = 5
                    sequenceSquares = emptyList()
                    expectedIndex = 0
                }
                showNextRandomSquares()
            }
        }
    }

    private fun finishTest() {
        binding.gridLayout.visibility = View.INVISIBLE
        binding.linearLayout.visibility = View.INVISIBLE
        infoDialogFinishTest()
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

    private fun infoDialogStartTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Начало") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Перед началом тестирования нажмите на кнопку старт.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogInstruction() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Инструкция") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Длина последовательности - 3") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            startTest()
            binding.btnStart.visibility = View.INVISIBLE
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogFinishTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun educationAnimation() {
//        mainActivityListener?.updateToolbarState(ToolbarState.HideToolbar)
        binding.apply {
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.fot)
                okBtn.setOnClickListener {
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.FOTTest)
                    linearLayout.isVisible = true
                    gridLayout.isVisible = true
                    btnStart.isVisible = true
                }
            }
            linearLayout.isVisible = false
            gridLayout.isVisible = false
            btnStart.isVisible = false
        }
    }
}