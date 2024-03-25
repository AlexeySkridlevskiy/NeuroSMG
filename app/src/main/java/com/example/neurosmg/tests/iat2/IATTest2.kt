package com.example.neurosmg.tests.iat2

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
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
import androidx.lifecycle.lifecycleScope
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentIATTest2Binding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.utils.contentEquals
import com.example.neurosmg.utils.exitFullScreenMode
import com.example.neurosmg.utils.generateName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IATTest2 : Fragment() {

    lateinit var binding: FragmentIATTest2Binding
    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<List<String>>()
    private var patientId: Int = -1

    private val mutableNumberOfQuestion = MutableStateFlow(1)
    private val numberOfQuestion: StateFlow<Int> = mutableNumberOfQuestion

    private var currentNumberOfBlock = 1

    private lateinit var currentPicList: List<Pair<Int, String>>
    private var soundPlayer: SoundPlayer? = null

    private var touchStartTimeUnixTimestamp: Long = 0
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private var touchCategory: String = ""
    private var touchLeftCategory: String = "alco"
    private var touchRightCategory: String = "fruit"
    private var correctAnswer: String = ""
    private var presentPicture: Pair<Int, String>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        soundPlayer = SoundPlayer(context)
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KeyOfArgument.KEY_OF_FRAGMENT, Screen.IAT_TEST)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIATTest2Binding.inflate(inflater)
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

        lifecycleScope.launch {
            numberOfQuestion.collect() {
                binding.tvQuestion.text = numberOfQuestion.value.toString()
            }
        }

        binding.tvBlock.text = currentNumberOfBlock.toString()
        mainActivityListener?.updateToolbarState(ToolbarState.IATTest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reenterTransition = true
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    private fun choosePicList(): List<Pair<Int, String>> {
        val randomIndex = (0..1).random()

        return when (currentNumberOfBlock) {
            1 -> if (randomIndex == 0) fruitsPic else alcoholPic
            2 -> if (randomIndex == 0) smilePic else grimacePic
            3, 4 -> chooseListForSteps(randomIndex, fruitsPic, alcoholPic, smilePic, grimacePic)
            5 -> if (randomIndex == 0) smilePic else grimacePic
            6, 7 -> chooseListForSteps(randomIndex, alcoholPic, fruitsPic, grimacePic, smilePic)
            else -> if (randomIndex == 0) fruitsPic else alcoholPic
        }
    }

    private fun updateWordList() {
        currentPicList = choosePicList()
        updateWord()
        updateValues()
    }

    private fun chooseListForSteps(
        randomIndex: Int,
        list1: List<Pair<Int, String>>,
        list2: List<Pair<Int, String>>,
        list3: List<Pair<Int, String>>,
        list4: List<Pair<Int, String>>
    ): List<Pair<Int, String>> {
        return if (randomIndex == 0) {
            if ((0..1).random() == 0) list1 else list2
        } else {
            if ((0..1).random() == 0) list3 else list4
        }
    }

    private fun getPicture(pictures: List<Pair<Int, String>>): Pair<Int, String> {
        val selectedPicture = selectRandomPicture(pictures, presentPicture)
        presentPicture = selectedPicture
        return selectedPicture
    }

    private fun selectRandomPicture(pictures: List<Pair<Int, String>>, previousPicture: Pair<Int, String>?): Pair<Int, String> {
        var selectedPic: Pair<Int, String>
        do {
            selectedPic = pictures.random()
        } while (selectedPic == previousPicture)
        return selectedPic
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateWord() {
        binding.imgView.setImageResource(
            when {
                currentPicList.contentEquals(fruitsPic) -> {
                    getPicture(fruitsPic).first
                }

                currentPicList.contentEquals(alcoholPic) -> {
                    getPicture(alcoholPic).first
                }

                currentPicList.contentEquals(smilePic) -> {
                    getPicture(smilePic).first
                }

                currentPicList.contentEquals(grimacePic) -> {
                    getPicture(grimacePic).first
                }

                else -> getPicture(grimacePic).first

            }
        )
    }

    private fun updateValues() {
        if (numberOfQuestion.value <= TOTAL_QUESTION_IN_ONE_BLOCK) {
            mutableNumberOfQuestion.value++
        } else {
            if (currentNumberOfBlock < 7) {
                currentNumberOfBlock++
                when (currentNumberOfBlock) {
                    2 -> {
                        infoDialogStep2()
                        touchLeftCategory = "smile"
                        touchRightCategory = "grimace"
                        binding.btnLeft.text = "Улыбки"
                        binding.btnRight.text = "Гримасы"
                    }
                    3 -> {
                        infoDialogStep3()
                        touchLeftCategory = "smilealco"
                        touchRightCategory = "grimacefruit"
                        binding.btnLeft.text = "Улыбки или алкоголь"
                        binding.btnRight.text = "Гримасы или фрукты"
                    }
                    4 -> {
                        infoDialogStep4()
                    }
                    5 -> {
                        infoDialogStep5()
                        touchLeftCategory = "grimace"
                        touchRightCategory = "smile"
                        binding.btnLeft.text = "Гримасы"
                        binding.btnRight.text = "Улыбки"
                    }
                    6 -> {
                        infoDialogStep6()
                        touchLeftCategory = "grimacealco"
                        touchRightCategory = "smilefruit"
                        binding.btnLeft.text = "Гримасы или алкоголь"
                        binding.btnRight.text = "Улыбки или фрукты"
                    }
                    7 -> {
                        infoDialogStep7()
                    }
                }
                binding.tvBlock.text = currentNumberOfBlock.toString()
            }else{
                finishTest()
                binding.btnLeft.isEnabled = false
                binding.btnRight.isEnabled = false
            }
        }
    }

    private fun checkAnswer(selectedCategory: Int) {
        var correctCategory = -1
        correctCategory = if (currentNumberOfBlock == 5) {
            if (currentPicList.contentEquals(smilePic)) 0 else 1
        } else if (currentNumberOfBlock >= 6) {
            if (currentPicList.contentEquals(fruitsPic) || currentPicList.contentEquals(smilePic)) 0 else 1
        } else {
            if (currentPicList.contentEquals(fruitsPic) || currentPicList.contentEquals(grimacePic)) 0 else 1
        }

        if (selectedCategory == correctCategory) {
            correctAnswer = ANSWER_TRUE
            saveData()
            updateWordList()
        } else {
            correctAnswer = ANSWER_FALSE
            saveData()
            Toast.makeText(
                requireContext(),
                "Неверно! Сделайте правильный выбор",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveData(){
        var touchNameCategory = ""
        touchNameCategory = if (touchCategory == LEFT) {
            touchLeftCategory
        }else{
            touchRightCategory
        }
        val dynamicRow = listOf(
            touchStartTimeUnixTimestamp.toString(),
            currentNumberOfBlock.toString(),
            numberOfQuestion.value.toString(),
            touchDurationSeconds.toString(),
            touchCategory,
            touchNameCategory,
            correctAnswer,
            presentPicture?.second.orEmpty()
        )
        Log.d("saveData", "saveData: $dynamicRow")
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

    private fun educationAnimation() {
        binding.apply {
            lottieLayout.run {
                root.isVisible = true
                soundPlayer?.playSound(R.raw.iat2_prestart)
                animationLottie.setAnimation(R.raw.iat2)
                okBtn.setOnClickListener {
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    constraintLayout3.isVisible = true
                    clBtn.isVisible = true
                    imgView.isVisible = true
                    infoDialogInstructionTest()
                }
            }

            constraintLayout3.isVisible = false
            clBtn.isVisible = false
            imgView.isVisible = false
        }
    }

    private fun calculateTouchDuration() {
        val touchDurationMillis = touchEndTimeMillis - touchStartTimeMillis
        touchDurationSeconds = touchDurationMillis // Преобразовать в секунды с точностью до тысячных миллисекунд
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun infoDialogInstructionTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(R.string.dialog_title_rules)
        alertDialogBuilder.setMessage(R.string.dialog_iat_subtitle)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()

            binding.btnLeft.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchCategory = LEFT
                        touchStartTimeUnixTimestamp = System.currentTimeMillis()
                        touchStartTimeMillis = System.currentTimeMillis()
                    }
                    MotionEvent.ACTION_UP -> {
                        touchEndTimeMillis = System.currentTimeMillis()
                        calculateTouchDuration()
                        checkAnswer(1)
                    }
                }
                false
            }

            binding.btnRight.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchCategory = RIGHT
                        touchStartTimeUnixTimestamp = System.currentTimeMillis()
                        touchStartTimeMillis = System.currentTimeMillis()
                    }
                    MotionEvent.ACTION_UP -> {
                        touchEndTimeMillis = System.currentTimeMillis()
                        calculateTouchDuration()
                        checkAnswer(0)
                    }
                }
                false
            }
            infoDialogStep1()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep1() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_1)
        alertDialogBuilder.setTitle(getStageString(1))
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Фрукты”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep2() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_2)
        alertDialogBuilder.setTitle(getStageString(2))
        alertDialogBuilder.setMessage("Сделайте выбор между “Улыбками” и “Гримасами”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep3() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_3)
        alertDialogBuilder.setTitle(getStageString(3))
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Улыбки” и “Фрукты + Гримасы”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep4() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_4)
        alertDialogBuilder.setTitle(getStageString(4))
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Улыбки” и “Фрукты + Гримасы”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep5() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_5)
        alertDialogBuilder.setTitle(getStageString(5))
        alertDialogBuilder.setMessage("Сделайте выбор между “Улыбками” и “Гримасами”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep6() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_6)
        alertDialogBuilder.setTitle(getStageString(6))
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Гримасы” и “Фрукты + Улыбки”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep7() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat2_step_7)
        alertDialogBuilder.setTitle(getStageString(7))
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Гримасы” и “Фрукты + Улыбки”.")
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            soundPlayer?.stopSound()
            dialog.dismiss()
            currentPicList = choosePicList()
            updateWord()
            mutableNumberOfQuestion.value = 1
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle(R.string.dialog_title_test_done_title)
        alertDialogBuilder.setMessage(R.string.dialog_title_test_done_subtitle)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName, data)
            soundPlayer?.stopSound()
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun getStageString(stage: Int): String {
       return getString(R.string.stage_by_step, stage.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.stopSound()
    }

    companion object {
        private const val TEST_NAME = "IAT2"
        private const val TOTAL_QUESTION_IN_ONE_BLOCK = 19
        private const val LEFT = "left"
        private const val RIGHT = "right"
        private const val ANSWER_TRUE = "true"
        private const val ANSWER_FALSE = "false"

        @JvmStatic
        fun newInstance(patientId: Int = -1): IATTest2 {
            val fragment = IATTest2()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}
