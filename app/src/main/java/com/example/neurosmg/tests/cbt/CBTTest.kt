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
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.FileUploaderViewModel
import com.example.neurosmg.databinding.FragmentCBTTestBinding
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode
import kotlinx.coroutines.launch

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
    private val maxStepsIndex = 1
    private var soundPlayer: SoundPlayer? = null
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[FileUploaderViewModel::class.java]
    }

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
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
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
    }

    private fun startTest() {
        if (stepsIndex > maxStepsIndex) {
            finishTest()
        }
        binding.tvSteps.text = stepsIndex.toString()
        stepsIndex++
        expectedIndex = 0
        visibleSquares = allSquares.shuffled().take(9)
        visibleSquares.forEach { square ->
            square.setBackgroundResource(R.color.cbt_color_square)
            square.visibility = View.VISIBLE
        }

        sequence =
            visibleSquares.shuffled().take(currentSequenceLength).map { visibleSquares.indexOf(it) }
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
        visibleSquares.forEach { square ->
            square.visibility = View.INVISIBLE
        }

        startTest()
    }

    private fun onSquareClick(square: TextView) {
        if (!isShowingSequence) {
            if (square == sequenceSquares[expectedIndex]) {
                square.setBackgroundResource(R.color.cbt_color_square_user)
                expectedIndex++
                touchEndTimeMillis = System.currentTimeMillis()
                touchDurationSeconds = touchEndTimeMillis - touchStartTimeMillis
                saveData(touchDurationSeconds)
                touchStartTimeMillis = System.currentTimeMillis()
                if (expectedIndex == sequenceSquares.size) {
                    sequenceSquares = emptyList()
                    expectedIndex = 0
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
                        showNextRandomSquares()
                    }
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

        Log.d("MyLog", "${stepsIndex - 1}, $expectedIndex, $touchDurationSeconds")
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
        alertDialogBuilder.setTitle("Начало") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Перед началом тестирования нажмите на кнопку старт.") // TODO: в ресурсы выноси
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
        alertDialogBuilder.setMessage("Длина последовательности - 3") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            startTest()
            binding.btnStart.visibility = View.INVISIBLE
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogFinishTest() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси

            viewModelUploaderFile.uploadFile(patientId, successCallback = {
                Log.d("viewModelUploaderFile", "uploadFile: Success")
            },
                errorCallback = { errorMessage ->
                    Log.d("viewModelUploaderFile", "uploadFile: $errorMessage")
                }
            )

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
            soundPlayer?.playSound(R.raw.cbt_anim)
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.cbt)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    soundPlayer?.playSound(R.raw.cbt_start_btn)
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.FOTTest)
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
        csvWriter.writeDataToCsv(data) {
            when (it) {
                DataUploadCallback.OnFailure -> {

                    //todo: тут как-то обработай тип сори, не смогли отправить данные на сервер

                    Toast.makeText(
                        requireContext(),
                        "Неудачно сохранились файлы",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                DataUploadCallback.OnSuccess -> {
                    infoDialogFinishTest()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {

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