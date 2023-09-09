package com.example.neurosmg.tests.rat

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentRATTestBinding
import com.example.neurosmg.testsPage.TestsPage
import com.example.neurosmg.utils.exitFullScreenMode
import kotlin.random.Random

class RATTest : Fragment() {
    lateinit var binding: FragmentRATTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val ballColors = listOf(Color.RED, Color.YELLOW, Color.GREEN)
    private val ballProbabilities = listOf(8, 32, 128)
    private var ballProbabilitiesCurrent :Int = 0
    private var indexTouch :Int = 0
    private var bankIndexCount :Double = 0.0
    private var currentIndexCircle :Int = 1
    private var attemptIndex :Int = 1
    private var soundPlayer: SoundPlayer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        soundPlayer = SoundPlayer(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRATTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.RATTest)
        educationAnimation()
        binding.btnStart.setOnClickListener {
            infoDialogAttempt1()
            binding.btnStart.visibility = View.INVISIBLE
            binding.btnStop.visibility = View.VISIBLE
        }

        binding.btnStop.setOnClickListener {
            val ballCount = binding.ballsContainer.childCount
            val lastBall = binding.ballsContainer.getChildAt(ballCount - 1)
            stopBall(lastBall)
        }
        val ballsContainer: ConstraintLayout = binding.ballsContainer

        ballsContainer.setOnClickListener {
            indexTouch++
            if (indexTouch>0){
                binding.btnStop.isEnabled = true
            }
            val ballCount = ballsContainer.childCount
            if (ballCount > 0) {
                val lastBall = ballsContainer.getChildAt(ballCount - 1)
                val isPopped = popBall(lastBall)
                if (!isPopped) {
                    growBall(lastBall)
                }
            } else {
                addRandomBall(ballsContainer)
            }
        }
    }

    private fun popBall(ballView: View): Boolean {

        val colorIndex = ballView.tag as Int
        val indexPop = Random.nextInt(ballProbabilities[colorIndex]-indexTouch)
        val shouldPop = indexPop == 0
        if (shouldPop) with(binding) {

            ballsContainer.removeView(ballView)
            bankCurrentCount.text = "0"
            newAttempt()
            currentIndexCircle++
            currentCircle.text = currentIndexCircle.toString()
            indexTouch = 0
            addRandomBall(binding.ballsContainer)
            soundPlayer?.playSound(R.raw.rat_pop)
            Toast.makeText(requireContext(), "Шарик лопнул...", Toast.LENGTH_SHORT).show()
        }
        return shouldPop
    }
    private fun stopBall(ballView: View): Boolean {
            binding.ballsContainer.removeView(ballView)
            bankIndexCount += indexTouch*0.05
            binding.bankCount.text = String.format("%.2f", bankIndexCount)
            binding.bankCurrentCount.text = String.format("%.2f", indexTouch*0.05)
        newAttempt()
        currentIndexCircle++
            binding.currentCircle.text = currentIndexCircle.toString()
            indexTouch = 0
            addRandomBall(binding.ballsContainer)
        return true
    }
    private fun growBall(ballView: View) {
        val colorIndex = ballView.tag as Int
        val dpToPx = resources.displayMetrics.density
        val ballSizeDp = 2 // Измените эту переменную на нужное вам значение

        val centerX = ballView.x + ballView.width / 2
        val centerY = ballView.y + ballView.height / 2

        val ballSize = ballView.layoutParams.width + (ballSizeDp * dpToPx).toInt()

        val layoutParams = ballView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = ballSize
        layoutParams.height = ballSize

        val newLeftMargin = centerX - ballSize / 2
        val newTopMargin = centerY - ballSize / 2

        layoutParams.leftMargin = newLeftMargin.toInt()
        layoutParams.topMargin = newTopMargin.toInt()

        // Устанавливаем пивот анимации для центрирования анимации масштабирования
        ballView.pivotX = ballView.width / 2f
        ballView.pivotY = ballView.height / 2f

        ballView.requestLayout()

        // После увеличения шарика, проверяем лопнул ли он
        if (Random.nextInt(ballProbabilities[colorIndex]) == 0) {
            popBall(ballView)
        }
    }
    private fun addRandomBall(container: ConstraintLayout) {
        if(indexTouch==0){
            binding.btnStop.isEnabled = false
        }
        val ballSize = resources.getDimensionPixelSize(R.dimen.ball_size)
        val layoutParams = ConstraintLayout.LayoutParams(ballSize, ballSize)
        val ball = View(requireContext())
        val colorIndex = Random.nextInt(ballColors.size)
        ball.background = createRandomBallDrawable(colorIndex)
        ball.id = View.generateViewId()

        ball.tag = colorIndex // Используем tag для хранения индекса цвета
        ballProbabilitiesCurrent = ballProbabilities[colorIndex]

        container.addView(ball, layoutParams)

        // Центрируем шарик в контейнере
        layoutParams.leftToLeft = container.id
        layoutParams.topToTop = container.id

        // Рассчитываем позицию шарика так, чтобы он находился по центру контейнера
        val leftMargin = (container.width - ballSize) / 2
        val topMargin = (container.height - ballSize) / 2
        layoutParams.leftMargin = leftMargin
        layoutParams.topMargin = topMargin
        ball.layoutParams = layoutParams

        // Обработчик клика на шарик
        ball.setOnClickListener {
            if (Random.nextInt(ballProbabilities[colorIndex]) == 0) {
                popBall(ball)
            } else {
                growBall(ball)
            }
        }
    }
    private fun createRandomBallDrawable(colorIndex: Int): ShapeDrawable {
        val color = ballColors[colorIndex]

        val shape = OvalShape()
        val shapeDrawable = ShapeDrawable(shape)
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RATTest()
    }

    private fun newAttempt(){
        if (attemptIndex <= 3){
            if (currentIndexCircle==30){
                indexTouch=0
                currentIndexCircle=0
                bankIndexCount=0.0
                binding.bankCount.text = bankIndexCount.toString()
                binding.currentCircle.text = currentIndexCircle.toString()
                binding.bankCurrentCount.text = "0"
                if (attemptIndex==3){
                    infoDialogEndTest()
                }else if(attemptIndex==2){
                    attemptIndex++
                    infoDialogAttempt3()
                }else{
                    attemptIndex++
                    infoDialogAttempt2()
                }

            }
        }
    }

    private fun infoDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Инструкция") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Перед вами на экране будет представлен шар определенного цвета, который при касании экрана увеличивается в размере. По прошествии определенного количества касаний шар лопается. Каждое касание приносит определенную сумму выигрыша, которая может быть потеряна, если шар лопнет и может быть сохранена при нажатии кнопки «Стоп».\n" +
                "Всего существует три цветовые категории шаров, каждый из которых имеет различный шанс лопнуть (1/8, 1/32, 1/128).\n" +
                "Задача состоит в наборе наибольшего выигрыша.\n" +
                "Всего 3 попытки.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogAttempt1() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Попытка №1") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            binding.ballsContainer.visibility = View.VISIBLE
            addRandomBall(binding.ballsContainer)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogAttempt2() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.rat_new_game)
        alertDialogBuilder.setTitle("Попытка №2") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            addRandomBall(binding.ballsContainer)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogAttempt3() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.rat_new_game)
        alertDialogBuilder.setTitle("Попытка №3") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            addRandomBall(binding.ballsContainer)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке.") // TODO: в ресурсы выноси
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
            soundPlayer?.playSound(R.raw.rat_anim)
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.rat)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    infoDialog()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.SCTTest)
                    constraintLayout2.isVisible = true
                    btnStart.isVisible = true
                }
            }
            constraintLayout2.isVisible = false
            btnStart.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }
}