package com.example.neurosmg.tests.mrt

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentMRTTestBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.utils.exitFullScreenMode
import com.example.neurosmg.utils.generateName
import kotlin.random.Random

class MRTTest : Fragment() {
    lateinit var binding: FragmentMRTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }

    private val data = mutableListOf<List<String>>()
    private var patientId: Int = -1

    private var steps = 0
    private var flag = false
    private var typeFigure = "A"
    private var soundPlayer: SoundPlayer? = null
    private var resource = 7
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        soundPlayer = SoundPlayer(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reenterTransition = true
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMRTTestBinding.inflate(inflater)
        binding.buttonLeft.isVisible = false
        binding.buttonRight.isVisible = false
        binding.buttonLeft.setOnClickListener {
            saveData(LEFT_SIDE)
            startTest()
        }
        binding.buttonRight.setOnClickListener {
            saveData(RIGHT_SIDE)
            startTest()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.MRTTest)
        educationAnimation()

        viewModelUploaderFile.uploadFileLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UploadState.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
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

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    private fun educationAnimation() {
        binding.apply {
            soundPlayer?.playSound(R.raw.mrt_anim)
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.mrt)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    constraintLayout4.isVisible = true
                    linearLayout.isVisible = true
                    infoDialogInstructionTest()
                }
            }
            constraintLayout4.isVisible = false
            linearLayout.isVisible = false
        }
    }

    private fun startTest() {
        if (steps == 50) {
            finishTest()
        } else {
            startTime = System.currentTimeMillis()
            steps++
            binding.tvSteps.text = steps.toString()

            when (steps) {
                11 -> {
                    resource = listOf(1, 2).random()
                    typeFigure = "B"
                }

                21 -> {
                    resource = listOf(3, 9).random()
                    typeFigure = "C"
                }

                31 -> {
                    resource = listOf(5, 6).random()
                    typeFigure = "D"
                }

                41 -> {
                    resource = listOf(10, 11).random()
                    typeFigure = "E"
                }
            }

            var randomIndex = randomImageFromResource(resource)
            when (resource) {
                1 -> binding.imgView1.setImageResource(image1Resources[randomIndex])
                2 -> binding.imgView1.setImageResource(image2Resources[randomIndex])
                3 -> binding.imgView1.setImageResource(image3Resources[randomIndex])
                4 -> binding.imgView1.setImageResource(image4Resources[randomIndex])
                5 -> binding.imgView1.setImageResource(image5Resources[randomIndex])
                6 -> binding.imgView1.setImageResource(image6Resources[randomIndex])
                7 -> binding.imgView1.setImageResource(image7Resources[randomIndex])
                9 -> binding.imgView1.setImageResource(image9Resources[randomIndex])
                10 -> binding.imgView1.setImageResource(image10Resources[randomIndex])
                11 -> binding.imgView1.setImageResource(image11Resources[randomIndex])
            }

            val randomIndexOne = randomIndex % 2
            
            randomIndex = randomImageFromResource(resource)
            when(resource){
                1 -> binding.imgView2.setImageResource(image1Resources[randomIndex])
                2 -> binding.imgView2.setImageResource(image2Resources[randomIndex])
                3 -> binding.imgView2.setImageResource(image3Resources[randomIndex])
                4 -> binding.imgView2.setImageResource(image4Resources[randomIndex])
                5 -> binding.imgView2.setImageResource(image5Resources[randomIndex])
                6 -> binding.imgView2.setImageResource(image6Resources[randomIndex])
                7 -> binding.imgView2.setImageResource(image7Resources[randomIndex])
                9 -> binding.imgView2.setImageResource(image9Resources[randomIndex])
                10 -> binding.imgView2.setImageResource(image10Resources[randomIndex])
                11 -> binding.imgView2.setImageResource(image11Resources[randomIndex])
            }
            flag = randomIndex % 2 == randomIndexOne
        }
    }

    private fun randomImageFromResource(randomResource: Int): Int {
        var randomIndex = 0

        when(randomResource){
            1 -> randomIndex = Random.nextInt(image1Resources.size)
            2 -> randomIndex = Random.nextInt(image2Resources.size)
            3 -> randomIndex = Random.nextInt(image3Resources.size)
            4 -> randomIndex = Random.nextInt(image4Resources.size)
            5 -> randomIndex = Random.nextInt(image5Resources.size)
            6 -> randomIndex = Random.nextInt(image6Resources.size)
            7 -> randomIndex = Random.nextInt(image7Resources.size)
            9 -> randomIndex = Random.nextInt(image9Resources.size)
            10 -> randomIndex = Random.nextInt(image10Resources.size)
            11 -> randomIndex = Random.nextInt(image11Resources.size)
        }

        return randomIndex
    }

    private fun saveData(s: String) {
        endTime = System.currentTimeMillis()
        val durationTime = endTime - startTime
        var result = ""
        result = if (s == LEFT_SIDE && flag) {
            "true answer"
        } else {
            "false answer"
        }
        val dynamicRow = mutableListOf(
            steps.toString(), typeFigure, durationTime.toString(), flag.toString(), result
        )
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

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование окончено!")
        alertDialogBuilder.setMessage("Данные сохранены в папке!")
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ ->
            viewModelUploaderFile.sendFile(
                idPatient = patientId,
                fileName = fileName,
                data = data
            )
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogInstructionTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Правило тестирования")
        alertDialogBuilder.setMessage("Определите, являются ли фигуры одинаковыми. Нажмите слева, если фигуры сопадают. Нажмите справа, если фигуры не сопадают.")
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ ->
            dialog.dismiss()
            binding.buttonLeft.isVisible = true
            binding.buttonRight.isVisible = true
            startTest()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {
        private const val TEST_NAME = "MRT"
        private const val TEST_FILE_EXTENSION = ".csv"
        private const val LEFT_SIDE = "left"
        private const val RIGHT_SIDE = "right"
        @JvmStatic
        fun newInstance(patientId: Int = -1): MRTTest{
            val fragment = MRTTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}