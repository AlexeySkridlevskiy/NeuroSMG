package com.example.neurosmg.tests.rat

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentRATTestBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode
import kotlin.random.Random

class RATTest : Fragment() {
    lateinit var binding: FragmentRATTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private val ballColors = listOf(Color.RED, Color.YELLOW, Color.GREEN)
    private val ballProbabilities = listOf(8, 32, 128)
    private var ballProbabilitiesCurrent :Int = 0
    private var indexTouch :Int = 0
    private var bankIndexCount :Double = 0.0
    private var currentIndexCircle :Int = 1
    private var attemptIndex :Int = 1
    private var indexTouchAttempt :Int = 0
    private var soundPlayer: SoundPlayer? = null
    private var touchStartTimeUnixTimestamp: Long = 0
    private var indexPop: Int = 0
    private var colorIndex: Int = 0
    private var sizeBall: Int = 10
    private var touchDuration: Long = 0
    private var stateBall: String = ""

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
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRATTestBinding.inflate(inflater)
        viewModelUploaderFile.uploadFileLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UploadState.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                UploadState.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is UploadState.Success.SuccessGetIdFile -> {
                    binding.progressBar.isVisible = true
                    Toast.makeText(requireContext(), "$state", Toast.LENGTH_SHORT).show()
                }

                UploadState.Success.SuccessSendFile -> {
                    binding.progressBar.isVisible = false
                    parentFragmentManager.popBackStack()
                }

                UploadState.Initial -> {}
            }
        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.RATTest)
        educationAnimation()
        binding.btnStart.setOnClickListener {
            infoDialogAttempt1()
            binding.btnStart.visibility = View.INVISIBLE
            binding.btnStop.visibility = View.VISIBLE
            colorIndex = Random.nextInt(ballColors.size)
            indexPop = Random.nextInt(ballProbabilities[colorIndex])
        }

        binding.btnStop.setOnClickListener {
            val ballCount = binding.ballsContainer.childCount
            val lastBall = binding.ballsContainer.getChildAt(ballCount - 1)
            stopBall(lastBall)
        }
        val ballsContainer: ConstraintLayout = binding.ballsContainer

        ballsContainer.isClickable = true
        ballsContainer.isFocusable = true
        ballsContainer.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    touchDuration = System.currentTimeMillis() - touchStartTimeUnixTimestamp

                    indexTouch++
                    if (indexTouch > 0) {
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
            true
        }

    }

    private fun popBall(ballView: View): Boolean {
        indexPop++
        indexTouchAttempt++
        sizeBall+=2
        val shouldPop = indexPop == ballProbabilities[colorIndex]
        Log.d("MyLog", "$indexPop")
        touchStartTimeUnixTimestamp = System.currentTimeMillis()

        if (shouldPop) with(binding) {
            sizeBall=10
            stateBall = "exploded"
            colorIndex = Random.nextInt(ballColors.size)
            indexPop = Random.nextInt(ballProbabilities[colorIndex])
            ballsContainer.removeView(ballView)
            bankCurrentCount.text = "0"
            newAttempt()
            currentIndexCircle++
            currentCircle.text = currentIndexCircle.toString()
            indexTouch = 0
            addRandomBall(binding.ballsContainer)
            soundPlayer?.playSound(R.raw.rat_pop)
            saveData()
            Toast.makeText(requireContext(), "Шарик лопнул...", Toast.LENGTH_SHORT).show()
            return true
        }
        stateBall="pump"
        saveData() //todo: фиксация данных
        return false
    }
    @SuppressLint("SuspiciousIndentation")
    private fun stopBall(ballView: View): Boolean {
        sizeBall=10
        stateBall = "save"
        indexTouchAttempt++
        touchDuration = 0
        colorIndex = Random.nextInt(ballColors.size)
        indexPop = Random.nextInt(ballProbabilities[colorIndex])
            binding.ballsContainer.removeView(ballView)
            bankIndexCount += indexTouch*0.05
            binding.bankCount.text = String.format("%.2f", bankIndexCount)
            binding.bankCurrentCount.text = String.format("%.2f", indexTouch*0.05)
        newAttempt()
        currentIndexCircle++
            binding.currentCircle.text = currentIndexCircle.toString()
            indexTouch = 0
            addRandomBall(binding.ballsContainer)
        saveData()
        return true
    }
    private fun growBall(ballView: View) {
        val dpToPx = resources.displayMetrics.density
        val ballSizeDp = 2

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

        ballView.pivotX = ballView.width / 2f
        ballView.pivotY = ballView.height / 2f

        ballView.requestLayout()

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun addRandomBall(container: ConstraintLayout) {
        if(indexTouch==0){
            binding.btnStop.isEnabled = false
        }
        val ballSize = resources.getDimensionPixelSize(R.dimen.ball_size)
        val layoutParams = ConstraintLayout.LayoutParams(ballSize, ballSize)
        val ball = View(requireContext())
        ball.background = createRandomBallDrawable(colorIndex)
        ball.id = View.generateViewId()

        ball.tag = colorIndex
        ballProbabilitiesCurrent = ballProbabilities[colorIndex]

        container.addView(ball, layoutParams)

        layoutParams.leftToLeft = container.id
        layoutParams.topToTop = container.id

        val leftMargin = (container.width - ballSize) / 2
        val topMargin = (container.height - ballSize) / 2
        layoutParams.leftMargin = leftMargin
        layoutParams.topMargin = topMargin
        ball.layoutParams = layoutParams

        val ballsContainer: ConstraintLayout = binding.ballsContainer

        ball.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    touchDuration = System.currentTimeMillis() - touchStartTimeUnixTimestamp

                    indexTouch++
                    if (indexTouch > 0) {
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
            true
        }
    }
    private fun createRandomBallDrawable(colorIndex: Int): ShapeDrawable {
        val color = ballColors[colorIndex]

        val shape = OvalShape()
        val shapeDrawable = ShapeDrawable(shape)
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    private fun saveData(){
        val bankCount = String.format("%.2f", bankIndexCount)
        val dynamicRow = mutableListOf(
            touchStartTimeUnixTimestamp.toString(), attemptIndex.toString(), ballProbabilities[colorIndex].toString(),
            indexPop.toString(), sizeBall.toString(), currentIndexCircle.toString(), indexTouchAttempt.toString(),
            indexTouch.toString(), touchDuration.toString(), stateBall, bankCount
        )
        data.add(dynamicRow)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    private fun newAttempt(){
        if (attemptIndex <= 3){
            if (currentIndexCircle==30){
                indexTouch=0
                indexTouchAttempt=0
                currentIndexCircle=0
                bankIndexCount=0.0
                binding.bankCount.text = bankIndexCount.toString()
                binding.currentCircle.text = currentIndexCircle.toString()
                binding.bankCurrentCount.text = "0"
                if (attemptIndex==3){
                    finishTest()
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

    private fun finishTest() {
        binding.constraintMain.visibility = View.INVISIBLE

        saveDataToFileCSV()
    }

    private fun saveDataToFileCSV() {
        val csvWriter = CSVWriter(context = requireContext())
        val unixTime = System.currentTimeMillis()
        val fileName = "${TEST_NAME}.${unixTime}${TEST_FILE_EXTENSION}" //поменять файл на нужный
        csvWriter.writeDataToCsv(data, fileName = fileName) {
            when (it) {
                DataUploadCallback.OnFailure -> {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.not_success_save_file),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                DataUploadCallback.OnSuccess -> {
                    infoDialogEndTest(fileName)
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

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName)
            dialog.dismiss()
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
//                    mainActivityListener?.updateToolbarState(ToolbarState.RATTest)
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

    companion object {
        private const val TEST_NAME = "RAT"
        private const val TEST_FILE_EXTENSION = ".csv"
        @JvmStatic
        fun newInstance(patientId: Int = -1): RATTest{
            val fragment = RATTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}