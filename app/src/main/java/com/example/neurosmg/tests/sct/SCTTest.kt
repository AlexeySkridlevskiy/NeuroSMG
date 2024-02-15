package com.example.neurosmg.tests.sct

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentSCTTestBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.utils.exitFullScreenMode
import kotlin.random.Random

class SCTTest : Fragment() {

    private lateinit var textView: TextView
    private lateinit var buttonRed: Button
    private lateinit var buttonBlue: Button
    private val handler = Handler()

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private val colors = listOf(Color.RED, Color.BLUE)
    private val words = listOf(
        R.string.color_red, R.string.color_blue
    )

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
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSCTTestBinding.inflate(inflater)
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
            touchBtnLR = getString(R.string.left_side)
            touchBtnLRColor = getString(R.string.color_red_en)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    buttonBlue.isEnabled = false
                    touchStartTimeMillis = System.currentTimeMillis()
                }

                MotionEvent.ACTION_UP -> {
                    touchEndTimeMillis = System.currentTimeMillis()
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    calculateTouchDuration()
                    binding.tvColorText.visibility = View.INVISIBLE
                    checkAnswer(Color.RED)
                    buttonBlue.isEnabled = true
                }
            }
            false
        }

        buttonBlue.setOnTouchListener { _, motionEvent ->
            touchBtnLR = getString(R.string.right_side)
            touchBtnLRColor = getString(R.string.color_blue_en)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    buttonRed.isEnabled = false
                    touchStartTimeMillis = System.currentTimeMillis()
                }

                MotionEvent.ACTION_UP -> {
                    touchEndTimeMillis = System.currentTimeMillis()
                    touchStartTimeUnixTimestamp = System.currentTimeMillis()
                    calculateTouchDuration()
                    binding.tvColorText.visibility = View.INVISIBLE
                    checkAnswer(Color.BLUE)
                    buttonRed.isEnabled = true
                }
            }
            false
        }
    }

    private fun calculateTouchDuration() {
        val touchDurationMillis = touchEndTimeMillis - touchStartTimeMillis
        touchDurationSeconds = touchDurationMillis
    }

    private fun showRandomWord() {
        val randomIndex = Random.nextInt(0, words.size)
        val randomWord = words[randomIndex]
        randomColor = colors[Random.nextInt(0, colors.size)]
        if(totalAttempts<10){
            randomColor = if (randomWord == R.string.color_red) {
                Color.RED
            } else {
                Color.BLUE
            }
        }

        handler.postDelayed({
            textView.visibility = View.VISIBLE
            textView.text = getString(randomWord)
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
        randomColorView = if (randomColor == Color.RED) {
            getString(R.string.color_red_en)
        } else {
            getString(R.string.color_blue_en)
        }

        val dynamicRow = mutableListOf(
            touchStartTimeUnixTimestamp.toString(), touchDurationSeconds.toString(), touchBtnLR,
            randomColorView, touchBtnLRColor, totalAttempts.toString()
        )
        data.add(dynamicRow)
    }

    private fun finishTest() {
        binding.layoutSCT.visibility = View.INVISIBLE

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

    private fun showTestResult() {
        finishTest()
        buttonRed.isEnabled = false
        buttonBlue.isEnabled = false
    }
    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    private fun infoDialogStartTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.sct_dialog_title)
        alertDialogBuilder.setMessage(R.string.sct_dialog_subtitle)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogReady() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.sct_dialog_title_start)
        alertDialogBuilder.setMessage(R.string.sct_dialog_subtitle_start)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            dialog.dismiss()
            setupTest()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle(R.string.dialog_test_success_title)
        alertDialogBuilder.setMessage(R.string.dialog_test_success_subtitle)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName)
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun educationAnimation() {
        binding.apply {
            soundPlayer?.playSound(R.raw.sct_anim)
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.sct)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.sct_start)
                    infoDialogStartTest()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    textView17.isVisible = true
                    btnRed.isVisible = true
                    btnBlue.isVisible = true
                }
            }
            textView17.isVisible = false
            btnRed.isVisible = false
            btnBlue.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {
        private const val TAG = "SCTTest"
        private const val TEST_NAME = "SCT"
        private const val TEST_FILE_EXTENSION = ".csv"

        @JvmStatic
        fun newInstance(patientId: Int = -1): SCTTest {
            val fragment = SCTTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}