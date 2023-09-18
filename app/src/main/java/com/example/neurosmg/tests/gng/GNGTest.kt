package com.example.neurosmg.tests.gng

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentGNGTestBinding
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode

class GNGTest : Fragment() {
    // Объявление переменных
    private lateinit var binding: FragmentGNGTestBinding
    private var mainActivityListener: MainActivityListener? = null
    private val cross = "cross"
    private val plus = "plus"
    private lateinit var timer: CountDownTimer
    val handler = Handler()
    private var answer = false
    private var soundPlayer: SoundPlayer? = null
    private var touchStartTimeUnixTimestamp: Long = 0
    private var indexStep: Int = 0
    private var indexGoNogo: String = ""
    private var flagClickBtn: Int = 0
    private var touchStartTime: Long = 0
    private var touchEndTime: Long = 0
    private var randomTimeView: Long = 0

    // Переопределение метода onAttach для связи с активностью
    override fun onAttach(context: Context) {
        super.onAttach(context)
        soundPlayer = SoundPlayer(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    // Переопределение метода onCreateView для создания интерфейса фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGNGTestBinding.inflate(inflater)
        return binding.root
    }

    // Переопределение метода onViewCreated для инициализации интерфейса и запуска генерации
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.GNGTest)
        educationAnimation()
        binding.square1.setImageResource(R.drawable.zoom)
        binding.square2.setImageResource(R.drawable.zoom)
        binding.square3.setImageResource(R.drawable.zoom)
        binding.square4.setImageResource(R.drawable.zoom)
        // Начать генерацию крестиков и плюсиков через определенные интервалы
        binding.btnStart.setOnClickListener {
            startGeneratingCrossesAndPluses()
        }

        binding.btnCross.setOnClickListener {
            if (answer == true) {
//                binding.tvAnswer.text = "TRUE"
//                binding.tvAnswer.setTextColor(Color.GREEN)
            } else {
//                binding.tvAnswer.text = "FALSE"
//                binding.tvAnswer.setTextColor(Color.RED)
            }
        }

        binding.btnCross.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.btnCross.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF7260A3"))
                    // Когда начинается нажатие
                    touchStartTime = System.currentTimeMillis()
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.btnCross.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF6750A4"))
                    // Когда нажатие заканчивается
                    touchEndTime = System.currentTimeMillis()
                    val pressDuration = touchEndTime - touchStartTime
                    // Здесь вы можете использовать pressDuration по вашему усмотрению
                    flagClickBtn = pressDuration.toInt()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun saveData(){
        Log.d("MyLog", "$touchStartTimeUnixTimestamp, $indexStep, $indexGoNogo, $flagClickBtn, $randomTimeView")
    }

    private fun timerForData(){
        handler.postDelayed({
            timer = object : CountDownTimer(125000, 2500) { // Здесь задается интервал 1.5 секунды
                override fun onTick(millisUntilFinished: Long) {
                    saveData()
                    flagClickBtn = 0
                }

                override fun onFinish() {
                }
            }.start()
        }, 2400)
    }

    private fun startGeneratingCrossesAndPluses() {
        binding.btnStart.visibility = View.INVISIBLE
        binding.btnCross.visibility = View.VISIBLE
        handler.postDelayed({
            timer = object : CountDownTimer(125000, 2500) { // Здесь задается интервал 1.5 секунды
                override fun onTick(millisUntilFinished: Long) {
                    if(indexStep==0){
                        timerForData()
                    }

                    indexStep++
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    val randomSquare = (1..4).random()
                    val randomCross = (0..1).random()
                    indexGoNogo = if(randomCross==0){
                        "go"
                    }else{
                        "nogo"
                    }
                    randomTimeView = (1000..2000).random().toLong()
                    when (randomSquare) {
                        1 -> {
                            binding.square1.setImageResource(
                                if (randomCross == 0) R.drawable.cross else R.drawable.plus
                            )
                            answer = randomCross == 0
                            handler.postDelayed({
                                binding.square1.setImageResource(R.drawable.zoom)
                            }, randomTimeView)
                        }

                        2 -> {
                            binding.square2.setImageResource(
                                if (randomCross == 0) R.drawable.cross else R.drawable.plus
                            )
                            answer = randomCross == 0
                            handler.postDelayed({
                                binding.square2.setImageResource(R.drawable.zoom)
                            }, randomTimeView)
                        }

                        3 -> {
                            binding.square3.setImageResource(
                                if (randomCross == 0) R.drawable.cross else R.drawable.plus
                            )
                            answer = randomCross == 0
                            handler.postDelayed({
                                binding.square3.setImageResource(R.drawable.zoom)
                            }, randomTimeView)
                        }

                        4 -> {
                            binding.square4.setImageResource(
                                if (randomCross == 0) R.drawable.cross else R.drawable.plus
                            )
                            answer = randomCross == 0
                            handler.postDelayed({
                                binding.square4.setImageResource(R.drawable.zoom)
                            }, randomTimeView)
                        }
                    }
                }

                override fun onFinish() {
                    infoDialogEndTest()
                }
            }.start()
        }, 2000)
    }

    // Переопределение метода onDetach для отключения связи с активностью
    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    // Переопределение метода onDestroy для отмены таймера при уничтожении фрагмента
    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    // Компаньон объект для создания экземпляра фрагмента
    companion object {
        @JvmStatic
        fun newInstance() = GNGTest()
    }

    private fun infoDialogStart() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Начало") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Для начала тестирования нажмите кнопку Начать") // TODO: в ресурсы выноси
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
        alertDialogBuilder.setMessage("Перед вами на экране будет представлен шар определенного цвета, который при касании экрана увеличивается в размере. По прошествии определенного количества касаний шар лопается. Каждое касание приносит") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            infoDialogStart()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папку") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPageFragment.newInstance())
                .addToBackStack(Screen.TESTS_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun educationAnimation() {
        binding.apply {
            soundPlayer?.playSound(R.raw.gng_anim)
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.gng)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.gng_start_btn)
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    imageView2.isVisible = true
                    square1.isVisible = true
                    square2.isVisible = true
                    square3.isVisible = true
                    square4.isVisible = true
                    btnStart.isVisible = true
                    infoDialogInstruction()
                }
            }
            imageView2.isVisible = false
            square1.isVisible = false
            square2.isVisible = false
            square3.isVisible = false
            square4.isVisible = false
            btnStart.isVisible = false
        }
    }
}
