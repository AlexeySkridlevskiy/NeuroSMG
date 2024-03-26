package com.example.neurosmg.tests.gng

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import com.example.neurosmg.databinding.FragmentGNGTestBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.utils.NroThemeColor
import com.example.neurosmg.utils.exitFullScreenMode
import com.example.neurosmg.utils.generateName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GNGTest : Fragment() {

    private lateinit var binding: FragmentGNGTestBinding
    private var mainActivityListener: MainActivityListener? = null
    private lateinit var job: Job

    private var answer = false
    private var soundPlayer: SoundPlayer? = null
    private var timeStartGenerateNewStimulus: Long = 0
    private var timeTapOnStimulus: Long = 0
    private var indexStep: Int = 0
    private var flagClickBtn: Int = 0

    private var durationForShowingView: Long = DURATION_MIN
    private var currentStimulus: String = ""

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<List<String>>()
    private var patientId: Int = -1

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
        binding = FragmentGNGTestBinding.inflate(inflater)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.GNGTest)
        educationAnimation()
        binding.square1.setImageResource(R.drawable.zoom)
        binding.square2.setImageResource(R.drawable.zoom)
        binding.square3.setImageResource(R.drawable.zoom)
        binding.square4.setImageResource(R.drawable.zoom)

        binding.btnStart.setOnClickListener {
            startGeneratingCrossesAndPluses()
        }

        binding.btnCross.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.btnCross.setBackgroundColor(Color.GRAY)

                    timeTapOnStimulus = System.currentTimeMillis() - timeStartGenerateNewStimulus

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    binding.btnCross.setBackgroundColor(NroThemeColor.VIOLET.colorInt.toInt())
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun saveData(){
        val dynamicRow = mutableListOf(
            timeStartGenerateNewStimulus.toString(),
            indexStep.toString(),
            currentStimulus,
            timeTapOnStimulus.toString(),
            durationForShowingView.toString()
        )
        Log.d("TAG_MRT", "saveData: $dynamicRow")
        data.add(dynamicRow)
    }

    private fun finishTest() {
        binding.constraintMain.visibility = View.INVISIBLE
        saveDataToFileCSV()
    }

    private fun saveDataToFileCSV() {
        val csvWriter = CSVWriter(context = requireContext())
        val fileName = generateName(TEST_NAME)
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

    private fun startGeneratingCrossesAndPluses() {
        binding.btnStart.visibility = View.INVISIBLE
        binding.btnCross.visibility = View.VISIBLE

        job = CoroutineScope(Dispatchers.Main.immediate).launch {
            delay(2000)

            repeat(51) {
                generateStimulusAndDisplay()
                delay(durationForShowingView)
            }
            finishTest()
        }
    }

    private fun generateStimulusAndDisplay() {
        val randomSquare = (1..4).random()
        val shouldShowCross = (currentStimulus == PLUS && (1..5).random() == 1)
        currentStimulus = if (shouldShowCross) CROSS else PLUS

        timeStartGenerateNewStimulus = System.currentTimeMillis()

        if (indexStep != 0) {
            updateValues()
        }

        indexStep++

        displayStimulus(randomSquare)
        durationForShowingView = randomDurationForShowingView()
    }

    private fun updateValues() {
        saveData()
        flagClickBtn = 0
        timeTapOnStimulus = 0
    }

    private fun displayStimulus(square: Int) {
        val imageView = when (square) {
            1 -> binding.square1
            2 -> binding.square2
            3 -> binding.square3
            4 -> binding.square4
            else -> null
        }

        imageView?.let {
            it.setImageResource(if (currentStimulus == PLUS) R.drawable.plus else R.drawable.cross)
            answer = currentStimulus == PLUS

            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                it.setImageResource(R.drawable.zoom)
            }
        }
    }

    private fun randomDurationForShowingView(): Long {
        return (DURATION_MIN..DURATION_MAX).random()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
        if (::job.isInitialized) {
            job.cancel()
        }
    }

    private fun infoDialogStart() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.gng_dialog_title_start)
        alertDialogBuilder.setMessage(R.string.gng_dialog_subtitle_start)
        alertDialogBuilder.setPositiveButton(R.string.dialog_btn_ok) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogInstruction() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.gng_dialog_title)
        alertDialogBuilder.setMessage(R.string.gng_dialog_message)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            dialog.dismiss()
            infoDialogStart()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle(R.string.gng_dialog_title_success)
        alertDialogBuilder.setMessage(R.string.gng_dialog_save)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName, data)
            dialog.dismiss()
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

    companion object {
        private const val TEST_NAME = "GNG"

        private const val DURATION_MAX = 2000L
        private const val DURATION_MIN = 1000L
        private const val TIMER_DURATION = 100_000L
        private const val TAG_GO = "go"
        private const val TAG_NO_GO = "nogo"
        private const val PLUS = "plus"
        private const val CROSS = "cross"

        fun newInstance(patientId: Int = -1): GNGTest {
            val fragment = GNGTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}
