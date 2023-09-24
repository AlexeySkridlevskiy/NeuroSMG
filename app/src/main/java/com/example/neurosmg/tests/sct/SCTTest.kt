package com.example.neurosmg.tests.sct

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentSCTTestBinding
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode
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
    private var soundPlayer: SoundPlayer? = null

    private var randomColor: Int = 0

    private var touchStartTimeUnixTimestamp: Long = 0
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private var touchBtnLR: String = ""
    private var touchBtnLRColor: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        soundPlayer = SoundPlayer(context)
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
        educationAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.SCTTest)
        textView = binding.tvColorText
        buttonRed = binding.btnRed
        buttonBlue = binding.btnBlue

        binding.layoutSCT.setOnClickListener {
            infoDialogReady()
        }
        setupButtonClickListeners()
    }

    private fun setupTest() {
        totalAttempts = 0
        score = 0
        showRandomWord()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtonClickListeners() {
        buttonRed.setOnTouchListener { _, motionEvent ->
            touchBtnLR = "left"
            touchBtnLRColor = "red"
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    touchStartTimeMillis = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    touchEndTimeMillis = System.currentTimeMillis()
                    calculateTouchDuration()
                    binding.tvColorText.visibility = View.INVISIBLE
                    checkAnswer(Color.RED)
                }
            }
            false
        }

        buttonBlue.setOnTouchListener { _, motionEvent ->
            touchBtnLR = "right"
            touchBtnLRColor = "blue"
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    touchStartTimeMillis = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    touchEndTimeMillis = System.currentTimeMillis()
                    calculateTouchDuration()
                    binding.tvColorText.visibility = View.INVISIBLE
                    checkAnswer(Color.BLUE)
                }
            }
            false
        }
    }
    private fun calculateTouchDuration() {
        val touchDurationMillis = touchEndTimeMillis - touchStartTimeMillis
        touchDurationSeconds = touchDurationMillis // Преобразовать в секунды с точностью до тысячных миллисекунд
    }
    private fun showRandomWord() {
        val randomIndex = Random.nextInt(0, words.size)
        val randomWord = words[randomIndex]
        randomColor = colors[Random.nextInt(0, colors.size)]
        if(totalAttempts<10){
            randomColor = if (randomWord=="Красный"){
                Color.RED
            }else{
                Color.BLUE
            }
        }

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

        saveData()

        if (totalAttempts < 50) {
            showRandomWord()
        } else {
            showTestResult()
        }
    }

    private fun saveData() {
        var randomColorView = ""
        randomColorView = if(randomColor == Color.RED){
            "red"
        }else{
            "blue"
        }
        Log.d("MyLog", "$touchStartTimeUnixTimestamp, $touchDurationSeconds, $touchBtnLR, $randomColorView, $touchBtnLRColor, $totalAttempts")
    }

    private fun showTestResult() {
//        textView.visibility = View.VISIBLE
//        val accuracy = score * 100 / totalAttempts
//        val resultText = "Тест завершен!\n\nТочность: $accuracy%"
//        textView.text = resultText
        infoDialogEndTest()
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

    private fun infoDialogStartTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Начало") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Для начала тестирования коснитесь экрана") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogReady() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Исследование начато") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Приготовьтесь") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            setupTest()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено, спасибо!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPageFragment.newInstance())
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
            soundPlayer?.playSound(R.raw.sct_anim)
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.sct)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.sct_start)
                    infoDialogStartTest()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.SCTTest)
                    textView17.isVisible = true
                    btnRed.isVisible = true
                    btnBlue.isVisible = true
//                    line.isVisible = true
                }
            }
            textView17.isVisible = false
            btnRed.isVisible = false
            btnBlue.isVisible = false
//            constraintLayout.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }
}