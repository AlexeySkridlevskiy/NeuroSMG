package com.example.neurosmg.tests.cbt

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.neurosmg.databinding.FragmentCBTTestBinding
import com.example.neurosmg.utils.exitFullScreenMode

class CBTTest : Fragment() {
    lateinit var binding: FragmentCBTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private lateinit var allSquares: List<TextView>
    private lateinit var sequenceSquares: List<TextView>
    private lateinit var visibleSquares: List<TextView>
    private lateinit var timer: CountDownTimer

    private var currentSequenceLength = 3
    private val maxSequenceLength = 9
    private var sequence: List<Int> = emptyList()
    private var isShowingSequence = false
    private var expectedIndex = 0
    private var stepsIndex = 1
    private val maxStepsIndex = 20
    private var soundPlayer: SoundPlayer? = null
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1
    private var flagIndex: Boolean = true
    private var squareIndex: Int = -1

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }

    private val fixedSequence = (0 until 9).toList()

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
        Log.d("MyLog", "$patientId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCBTTestBinding.inflate(inflater)
        educationAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    private fun startTest() {
        binding.tvSteps.text = stepsIndex.toString()
        stepsIndex++
        expectedIndex = 0
        visibleSquares = allSquares.shuffled().take(9)
        visibleSquares.forEach { square ->
            square.setBackgroundResource(R.color.cbt_color_square)
            square.visibility = View.VISIBLE
        }

        sequence = fixedSequence.take(currentSequenceLength)
        sequenceSquares = sequence.map { visibleSquares[it] }

        binding.root.postDelayed({
            showSequence()
        }, 1000L)
    }

    private fun showSequence() {
        isShowingSequence = true
        showNextSquare(0)
    }

    private fun showNextSquare(index: Int) {
        if (index >= sequence.size) {
            resetSquares()
            isShowingSequence = false
            if (stepsIndex == 2) {
                soundPlayer?.playSound(R.raw.cbt_first_seq)
            }
            touchStartTimeMillis = System.currentTimeMillis()
//                binding.root.isClickable = true

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
        flagIndex = true
        visibleSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }

        binding.root.postDelayed({
            startTest()
        }, 1000L)
    }

    private fun onSquareClick(square: TextView) {
        soundPlayer?.stopSound()
        if (!isShowingSequence) {
            if (square !== sequenceSquares[expectedIndex]) {
                flagIndex = false
            }
            square.setBackgroundResource(R.color.cbt_color_square_user)
            expectedIndex++
            touchEndTimeMillis = System.currentTimeMillis()
            touchDurationSeconds = touchEndTimeMillis - touchStartTimeMillis

            squareIndex = visibleSquares.indexOf(square)

            saveData(touchDurationSeconds)

            touchStartTimeMillis = System.currentTimeMillis()

            if (expectedIndex == sequenceSquares.size) {
                if (stepsIndex > maxStepsIndex) {
                    finishTest()
                    return
                }
                sequenceSquares = emptyList()
                expectedIndex = 0
                if (flagIndex) {
                    currentSequenceLength++
                    when (currentSequenceLength) {
                        4 -> soundPlayer?.playSound(R.raw.cbt_seq_4)
                        5 -> soundPlayer?.playSound(R.raw.cbt_seq_5)
                        6 -> soundPlayer?.playSound(R.raw.cbt_seq_6)
                        7 -> soundPlayer?.playSound(R.raw.cbt_seq_7)
                        8 -> soundPlayer?.playSound(R.raw.cbt_seq_8)
                        9 -> soundPlayer?.playSound(R.raw.cbt_seq_9)
                    }
                    if (currentSequenceLength > maxSequenceLength) {
                        finishTest()
                    } else {
                        binding.root.isClickable = false
                        showNextRandomSquares()
                    }
                } else {
                    if (currentSequenceLength > 5) {
                        currentSequenceLength = 5
                        sequenceSquares = emptyList()
                        expectedIndex = 0
                    }
                    when (currentSequenceLength) {
                        4 -> soundPlayer?.playSound(R.raw.cbt_seq_4)
                        5 -> soundPlayer?.playSound(R.raw.cbt_seq_5)
                    }
                    showNextRandomSquares()
                }
            }
        }
    }

    private fun finishTest() {
        binding.gridLayout.visibility = View.INVISIBLE
        binding.linearLayout.visibility = View.INVISIBLE

        saveDataToFileCSV()
    }

    private fun saveData(touchDurationSeconds: Long) {
        val dynamicRow = mutableListOf(
            (stepsIndex - 1).toString(), expectedIndex.toString(), touchDurationSeconds.toString()
        )
        data.add(dynamicRow)

        Log.d("MyLog", "${stepsIndex - 1}, ${squareIndex + 1}, $touchDurationSeconds")
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    private fun infoDialogStartTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(requireContext().getString(R.string.cbt_dialog_title))
        alertDialogBuilder.setMessage(requireContext().getString(R.string.cbt_dialog_message))
        alertDialogBuilder.setPositiveButton(requireContext().getString(R.string.dialog_ok)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogInstruction() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(requireContext().getString(R.string.cbt_dialog_inst_title))
        alertDialogBuilder.setMessage(requireContext().getString(R.string.cbt_dialog_inst_sumtitle))
        alertDialogBuilder.setPositiveButton(requireContext().getString(R.string.dialog_ok)) { dialog, _ ->
            dialog.dismiss()
            startTest()
            binding.btnStart.visibility = View.INVISIBLE
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogFinishTest(fileName: String) {

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.stopSound()
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
            soundPlayer?.playSound(R.raw.cbt_anim)
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.cbt)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.cbt_start_btn)
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    linearLayout.isVisible = true
                    gridLayout.isVisible = true
                    btnStart.isVisible = true
                    infoDialogStartTest()
                }
            }
            linearLayout.isVisible = false
            gridLayout.isVisible = false
            btnStart.isVisible = false
        }
    }

    private fun saveDataToFileCSV() {
        val csvWriter = CSVWriter(context = requireContext())
        val unixTime = System.currentTimeMillis()
        val fileName = "$TEST_NAME.${unixTime}$TEST_FILE_EXTENSION" //поменять файл на нужный
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
                    infoDialogFinishTest(fileName)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {
        private const val TEST_NAME = "CBT"
        private const val TEST_FILE_EXTENSION = ".csv"

        @JvmStatic
        fun newInstance(
            patientId: Int = -1
        ): CBTTest {
            val fragment = CBTTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}