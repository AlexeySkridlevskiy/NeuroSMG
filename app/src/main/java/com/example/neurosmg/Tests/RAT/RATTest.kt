package com.example.neurosmg.Tests.RAT

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentRATTestBinding
import kotlin.random.Random

class RATTest : Fragment() {
    lateinit var binding: FragmentRATTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val ballColors = listOf(Color.RED, Color.YELLOW, Color.GREEN)
    private val ballProbabilities = listOf(32, 16, 8)

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

        val ballsContainer: ConstraintLayout = binding.ballsContainer

        ballsContainer.setOnClickListener {
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
        val shouldPop = Random.nextInt(ballProbabilities[colorIndex]) == 0
        if (shouldPop) {
            binding.ballsContainer.removeView(ballView)
        }
        return shouldPop
    }

    private fun growBall(ballView: View) {
        val ballSize = ballView.layoutParams.width * 1.1 // Увеличиваем размер на 10%
        ballView.layoutParams.width = ballSize.toInt()
        ballView.layoutParams.height = ballSize.toInt()
        ballView.requestLayout()
    }

    private fun addRandomBall(container: ConstraintLayout) {
        val ballSize = resources.getDimensionPixelSize(R.dimen.ball_size)
        val layoutParams = ConstraintLayout.LayoutParams(ballSize, ballSize)
        val ball = View(requireContext())
        ball.background = createRandomBallDrawable()
        ball.id = View.generateViewId()

        val colorIndex = Random.nextInt(ballColors.size)
        ball.tag = colorIndex // Используем tag для хранения индекса цвета

        container.addView(ball, layoutParams)

        // Центрируем шарик в контейнере
        layoutParams.leftToLeft = container.id
        layoutParams.topToTop = container.id
        layoutParams.leftMargin = (container.width - ballSize) / 2
        layoutParams.topMargin = (container.height - ballSize) / 2
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

    private fun createRandomBallDrawable(): ShapeDrawable {
        val colorIndex = Random.nextInt(ballColors.size)
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
}
