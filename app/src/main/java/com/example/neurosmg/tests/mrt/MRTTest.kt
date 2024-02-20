package com.example.neurosmg.tests.mrt

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
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
    private var resource = 1
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val image1Resources = arrayOf(
        R.drawable.figure_1_1,
        R.drawable.figure_1_2,
        R.drawable.figure_1_3,
        R.drawable.figure_1_4,
        R.drawable.figure_1_5,
        R.drawable.figure_1_6,
    )

    private val image2Resources = arrayOf(
        R.drawable.figure_2_1,
        R.drawable.figure_2_2,
        R.drawable.figure_2_3,
        R.drawable.figure_2_4,
        R.drawable.figure_2_5,
        R.drawable.figure_2_6,
        R.drawable.figure_2_7,
        R.drawable.figure_2_8,
    )

    private val image3Resources = arrayOf(
        R.drawable.figure_3_1,
        R.drawable.figure_3_2,
        R.drawable.figure_3_3,
        R.drawable.figure_3_4,
        R.drawable.figure_3_5,
        R.drawable.figure_3_6,
        R.drawable.figure_3_7,
        R.drawable.figure_3_8,
    )

    private val image4Resources = arrayOf(
        R.drawable.figure_4_1,
        R.drawable.figure_4_2,
        R.drawable.figure_4_3,
        R.drawable.figure_4_4,
        R.drawable.figure_4_5,
        R.drawable.figure_4_6,
        R.drawable.figure_4_7,
        R.drawable.figure_4_8,
    )

    private val image5Resources = arrayOf(
        R.drawable.figure_5_1,
        R.drawable.figure_5_2,
        R.drawable.figure_5_3,
        R.drawable.figure_5_4,
        R.drawable.figure_5_5,
        R.drawable.figure_5_6,
        R.drawable.figure_5_7,
        R.drawable.figure_5_8,
    )

    private val image6Resources = arrayOf(
        R.drawable.figure_6_1,
        R.drawable.figure_6_2,
        R.drawable.figure_6_3,
        R.drawable.figure_6_4,
        R.drawable.figure_6_5,
        R.drawable.figure_6_6,
        R.drawable.figure_6_7,
        R.drawable.figure_6_8,
    )

    private val image7Resources = arrayOf(
        R.drawable.figure_7_1,
        R.drawable.figure_7_2,
        R.drawable.figure_7_3,
        R.drawable.figure_7_4,
        R.drawable.figure_7_5,
        R.drawable.figure_7_6,
    )

    private val image9Resources = arrayOf(
        R.drawable.figure_9_1,
        R.drawable.figure_9_2,
        R.drawable.figure_9_3,
        R.drawable.figure_9_4,
        R.drawable.figure_9_5,
        R.drawable.figure_9_6,
    )

    private val image10Resources = arrayOf(
        R.drawable.figure_10_1,
        R.drawable.figure_10_2,
        R.drawable.figure_10_3,
        R.drawable.figure_10_4,
        R.drawable.figure_10_5,
        R.drawable.figure_10_6,
    )

    private val image11Resources = arrayOf(
        R.drawable.figure_11_1,
        R.drawable.figure_11_2,
        R.drawable.figure_11_3,
        R.drawable.figure_11_4,
        R.drawable.figure_11_5,
        R.drawable.figure_11_6,
    )

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
            saveData("left")
            startTest()
        }
        binding.buttonRight.setOnClickListener {
            saveData("right")
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
                    resource = 2
                    typeFigure = "B"
                }

                21 -> {
                    resource = 3
                    typeFigure = "C"
                }

                31 -> {
                    resource = 4
                    typeFigure = "D"
                }

                41 -> {
                    resource = 5
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
                8 -> binding.imgView1.setImageResource(image9Resources[randomIndex])
                9 -> binding.imgView1.setImageResource(image10Resources[randomIndex])
                10 -> binding.imgView1.setImageResource(image11Resources[randomIndex])
            }

            val randomIndexOne = randomIndex % 2
            
            randomIndex = randomImageFromResource(resource)
            when(resource){
                1 -> binding.imgView2.setImageResource(image1Resources[randomIndex])
                2 -> binding.imgView2.setImageResource(image2Resources[randomIndex])
                3 -> binding.imgView2.setImageResource(image3Resources[randomIndex])
                4 -> binding.imgView2.setImageResource(image4Resources[randomIndex])
                5 -> binding.imgView2.setImageResource(image5Resources[randomIndex])
            }
            flag = randomIndex%2 == randomIndexOne
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
        }

        return randomIndex
    }

    private fun saveData(s: String) {
        endTime = System.currentTimeMillis()
        val durationTime = endTime - startTime
        var result = ""
        if(s == "left" && flag){
            result = "true answer"
        }else{
            result = "false answer"
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
        alertDialogBuilder.setTitle("Тестирование окончено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные сохранены в папке!") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
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
        alertDialogBuilder.setTitle("Правило тестирования") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Определите, являются ли фигуры одинаковыми. Нажмите слева, если фигуры сопадают. Нажмите справа, если фигуры не сопадают.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
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