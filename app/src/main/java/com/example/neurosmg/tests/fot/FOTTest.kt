package com.example.neurosmg.tests.fot

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentFOTTestBinding
import com.example.neurosmg.tests.cbt.CBTTest
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode

class FOTTest : Fragment(), CanvasViewCallback {

    lateinit var binding: FragmentFOTTestBinding

    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var canvasView: CanvasView
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var touchCount: Int = 0
    private var viewDialog: Int = 0
    private var testRound: Int = 0
    private var secPerStart: Int = 0
    private var isStartTimer: Boolean = false
    private var soundPlayer: SoundPlayer? = null
    private var handIndex = "right"
    private var EventX: Float = 0F
    private var EventY: Float = 0F

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
        binding = FragmentFOTTestBinding.inflate(inflater)
        mainActivityListener?.updateToolbarState(ToolbarState.FOTTest)
        canvasView = binding.canvasView

        canvasView.setCanvasViewCallback(this)
        binding.startBtn.setOnClickListener {
            if(testRound>0){
                binding.canvasView.clearPoints()
                binding.startBtn.visibility = View.INVISIBLE
            }else{
                if (!TestActive.KEY_ACTIVE_FOT_TEST) {
                    onPause()
                    infoDialog()
                }
            }
        }

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

        educationAnimation()
        return binding.root
    }

    private fun startTest() {
        binding.startBtn.visibility = View.INVISIBLE
        TestActive.KEY_ACTIVE_FOT_TEST = true
        touchCount = 0
        isStartTimer = true
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                secPerStart = (30-millisUntilFinished/1000).toInt()
                binding.tvTime.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                canvasView.touchEnabled = false
                endTest()
            }
        }.start()
    }

    private fun endTest() {
        if (testRound == 0) {
            TestActive.KEY_ACTIVE_FOT_TEST = false
            endTime = System.currentTimeMillis()
            canvasView.touchEnabled = false
            val touchesPerSecond = touchCount.toDouble() / 30 //todo: тут частота нажатий
            binding.tvClicks.text = touchCount.toString()
            handIndex = "left"
            binding.lblHandTv.text = "Левая рука"
            testRound++
            onPause()
            infoDialogToLeft()
        } else if (testRound != 0) {
            TestActive.KEY_ACTIVE_FOT_TEST = false
            finishTest()
        }

    }

    override fun onPause() {
        super.onPause()
        if(testRound>0){
            binding.canvasView.clearPointsLeft()
        }else{
            binding.canvasView.clearPoints()
        }
    }

    override fun onDetach() {
        super.onDetach()
        TestActive.KEY_ACTIVE_FOT_TEST = false
        if (isStartTimer) {
            countDownTimer.cancel()
        }
        mainActivityListener = null
    }

    override fun onCanvasViewTouch() {
        if (TestActive.KEY_ACTIVE_FOT_TEST) {
            touchCount++
            updateTouchCountTextView()
        }
    }

    override fun onCanvasFirstTouch() {
        if(testRound>0){
            startTest()
        }else{
            startTest()
        }
    }

    override fun onCanvasClickNoTest() {

        if(testRound==1 && !TestActive.KEY_ACTIVE_FOT_TEST){
            if (viewDialog == 0) {
                viewDialog++
                infoDialogStartTest()
            }
        }
        else if (!TestActive.KEY_ACTIVE_FOT_TEST) {
            if (viewDialog == 0) {
                viewDialog++
                infoDialogStartTest()
            }
        }
    }

    override fun onCanvasData(x: Float, y: Float) {
        EventX = x
        EventY = y
    }

    override fun onCanvasDataMotion(timeInSeconds: Long, durationMillis: Long) {
        val dynamicRow = mutableListOf(
            durationMillis.toString(), secPerStart.toString(), secPerStart.toString(), timeInSeconds.toString(),
            EventX.toString(), EventY.toString(), handIndex
        )
        data.add(dynamicRow)
//        Log.d("MyLog", "$durationMillis, $secPerStart, $timeInSeconds, $EventX , $EventY, $handIndex")
    }

    private fun updateTouchCountTextView() {
        binding.tvClicks.text = touchCount.toString()
    }

    private fun infoDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        binding.startBtn.visibility = View.INVISIBLE
        alertDialogBuilder.setTitle("Начало") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Для начала тестирования коснитесь экрана") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
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
            viewDialog = 0
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogToLeft() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.fot_left)
        binding.startBtn.visibility = View.VISIBLE
        alertDialogBuilder.setTitle("Время вышло") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Продолжите исследование для левой руки") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            canvasView.touchEnabled = true
            infoDialogStartTest()
            Log.d("MyLog", "${TestActive.KEY_ACTIVE_FOT_TEST}")
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndAllTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName)
            soundPlayer?.stopSound()
            dialog.dismiss()
//            parentFragmentManager
//                .beginTransaction()
//                .replace(R.id.container, TestsPageFragment.newInstance())
//                .addToBackStack(Screen.MAIN_PAGE)
//                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun educationAnimation() {
//        mainActivityListener?.updateToolbarState(ToolbarState.HideToolbar)
        binding.apply {
            soundPlayer?.playSound(R.raw.fot_anim)
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.fot)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.fot_start_btn)
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.FOTTest)
                    startBtn.isVisible = true
                    canvasView.isVisible = true
                    lblHandTv.isVisible = true
                    constraintLayout.isVisible = true
                }
            }
            startBtn.isVisible = false
            canvasView.isVisible = false
            lblHandTv.isVisible = false
            constraintLayout.isVisible = false
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
                    infoDialogEndAllTest(fileName)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {
        private const val TEST_NAME = "FOT"
        private const val TEST_FILE_EXTENSION = ".csv"
        const val TAG_FRAGMENT = Screen.FOT_TEST
        @JvmStatic
        fun newInstance(patientId: Int = -1): FOTTest {
            val fragment = FOTTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}