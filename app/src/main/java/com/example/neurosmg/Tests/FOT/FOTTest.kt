package com.example.neurosmg.Tests.FOT

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentFOTTestBinding
import com.example.neurosmg.testsPage.TestsPage
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
    private var viewDialog: Int = 0
    private var testRound: Int = 0
    private var isStartTimer: Boolean = false

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
            if (!TestActive.KEY_ACTIVE_FOT_TEST){
                onPause()
                infoDialog()
            }
        }
        return binding.root
    }

    private fun startTest() {
        binding.button.visibility = View.INVISIBLE
        TestActive.KEY_ACTIVE_FOT_TEST = true
        touchCount = 0
        canvasView.clearPoints()
        isStartTimer = true
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
        if(testRound == 0){
            TestActive.KEY_ACTIVE_FOT_TEST = false
            endTime = System.currentTimeMillis()

            val touchesPerSecond = touchCount.toDouble() / 30 //todo: тут частота нажатий
            binding.tvClicks.text = touchCount.toString()

            instructionsTextView.text = "Левая рука"
            onPause()
            infoDialogToLeft()
            testRound++
        }else if (testRound!=0){
            TestActive.KEY_ACTIVE_FOT_TEST = false
            binding.button.visibility = View.VISIBLE
            infoDialogEndAllTest()
        }

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
        TestActive.KEY_ACTIVE_FOT_TEST = false
        if(isStartTimer){
            countDownTimer.cancel()
        }
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

    override fun onCanvasFirstTouch() {
        startTest()
    }

    override fun onCanvasClickNoTest() {
        if (!TestActive.KEY_ACTIVE_FOT_TEST){
            if (viewDialog==0){
                viewDialog++
                infoDialogStartTest()
            }
        }
    }

    private fun updateTouchCountTextView() {
        binding.tvClicks.text = touchCount.toString()
    }

    private fun infoDialog() {
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

    private fun infoDialogStartTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Предупреждение") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Перед началом теста нажмите кнопку начать") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            viewDialog=0
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogToLeft() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Время вышло") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Продолжите исследование для левой руки") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndAllTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, TestsPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }
}