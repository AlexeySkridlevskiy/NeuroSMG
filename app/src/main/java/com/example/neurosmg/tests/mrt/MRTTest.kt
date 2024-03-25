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
import android.widget.ImageView
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
    private var chosenCollection = 7
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

        binding.leftArea.setOnClickListener {
            saveData(LEFT_SIDE)
            startTest()
        }

        binding.rightArea.setOnClickListener {
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
        when (steps) {
            50 -> finishTest()
            else -> {
                startTime = System.currentTimeMillis()
                steps++
                binding.tvSteps.text = steps.toString()

                chosenCollection = chooseNumberOfCollection(steps)
                val imageOfFirstView = binding.imgView1.setImageResourceFromUniqueResource(
                    chosenCollection = chosenCollection
                )

                val indexOfImage2 = binding.imgView2.setImageResourceFromUniqueResource(
                    chosenCollection = chosenCollection,
                    imageOfFirstView = imageOfFirstView
                )

                //flag = imageOfFirstView % 2 == indexOfImage2 //тут понятнее нужно сделать
            }
        }
    }

    private fun ImageView.setImageResourceFromUniqueResource(
        chosenCollection: Int,
        imageOfFirstView: Image? = null
    ): Image {
        val randomImage = getImageFromCollection(chosenCollection, imageOfFirstView)
        setImageResource(randomImage.resource)

        return randomImage
    }

    private fun getImageFromCollection(
        chosenCollection: Int,
        imageOfFirstView: Image? = null,
    ): Image {
        val collection = getCollection(chosenCollection)
        val filteredCollection = collection.filterNot { it == imageOfFirstView }

        return if (filteredCollection.isEmpty()) {
            collection.random()
        } else {
            filteredCollection.filter { it.group != imageOfFirstView?.group }.randomOrNull()
        } ?: collection.random()
    }

    private fun getCollection(resource: Int): List<Image> {
        return when (resource) {
            1 -> image1Resources
            2 -> image2Resources
            3 -> image3Resources
            4 -> image4Resources
            5 -> image5Resources
            6 -> image6Resources
            7 -> image7Resources
            9 -> image9Resources
            10 -> image10Resources
            11 -> image11Resources
            else -> image7Resources
        }
    }

    private fun chooseNumberOfCollection(step: Int): Int {
        return when (step) {
            in 11..20 -> {
                typeFigure = "B"
                listOf(1, 2).random()
            }

            in 21..30 -> {
                typeFigure = "C"
                listOf(3, 9).random()
            }

            in 31..40 -> {
                typeFigure = "D"
                listOf(5, 6).random()
            }

            in 41..50 -> {
                typeFigure = "E"
                listOf(10, 11).random()
            }

            else -> {
                7
            }
        }
    }

    private fun saveData(s: String) {
        endTime = System.currentTimeMillis()

        val durationTime = endTime - startTime

        val result = when (s == LEFT_SIDE && flag) {
            true -> TRUE_ANSWER
            false -> FALSE_ANSWER
        }

        val dynamicRow = listOf(
            steps.toString(),
            typeFigure,
            durationTime.toString(),
            flag.toString(),
            result
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
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
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
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
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
        private const val TRUE_ANSWER = "true answer"
        private const val FALSE_ANSWER = "false answer"
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