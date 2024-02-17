package com.example.neurosmg.tests.iat

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentIATTestBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.utils.contentEquals
import com.example.neurosmg.utils.exitFullScreenMode
import com.example.neurosmg.utils.generateName

class IATTest : Fragment() {

    lateinit var binding: FragmentIATTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private val totalRounds = 20
    private var currentRound = 1
    private var currentStep = 1

    private val drinksWords = listOf("Чай", "Лимонад", "Вода", "Сок", "Морс")
    private val alcoholWords = listOf("Водка", "Виски", "Вино", "Пиво", "Коньяк")
    private val goodWords = listOf("Вкусно", "Полезно", "Приятно", "Положительно", "Мило")
    private val badWords = listOf("Мерзко", "Отрицательно", "Гадко", "Отвратительно", "Ужасно")

    private lateinit var currentWordList: List<String>
    private var soundPlayer: SoundPlayer? = null

    private var touchStartTimeUnixTimestamp: Long = 0
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private var touchCategory: String = ""
    private var touchLeftCategory: String = "soft"
    private var touchRightCategory: String = "alco"
    private var correctAnswer: String = ""
    private var presentWord: String? = null

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
        binding = FragmentIATTestBinding.inflate(inflater)
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
        binding.tvQuestion.text = currentRound.toString()
        binding.tvStep.text = currentStep.toString()
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

    private fun chooseWordList(): List<String> {
        val randomIndex = (0..1).random()

        return when (currentStep) {
            1 -> if (randomIndex == 0) drinksWords else alcoholWords
            2 -> if (randomIndex == 0) goodWords else badWords
            3, 4 -> chooseListForSteps(randomIndex, drinksWords, alcoholWords, goodWords, badWords)
            5 -> if (randomIndex == 0) alcoholWords else drinksWords
            6, 7 -> chooseListForSteps(randomIndex, alcoholWords, drinksWords, badWords, goodWords)
            else -> if (randomIndex == 0) drinksWords else alcoholWords
        }
    }

    private fun chooseListForSteps(
        randomIndex: Int,
        list1: List<String>,
        list2: List<String>,
        list3: List<String>,
        list4: List<String>
    ): List<String> {
        return if (randomIndex == 0) {
            if ((0..1).random() == 0) list1 else list2
        } else {
            if ((0..1).random() == 0) list3 else list4
        }
    }

    private fun updateWordList() {
        currentWordList = chooseWordList()
        updateWord()
    }

    private fun selectRandomWord(words: List<String>, previousWord: String?): String {
        var selectedWord: String
        do {
            selectedWord = words.random()
        } while (selectedWord == previousWord)
        return selectedWord
    }

    private fun getWord(drinks: List<String>): String {
        val selectedWord = selectRandomWord(drinks, presentWord)
        presentWord = selectedWord
        return selectedWord
    }

    private fun updateWord() {
        binding.tvText.text = when {
            currentWordList.contentEquals(drinksWords) -> {
                getWord(drinksWords)
            }

            currentWordList.contentEquals(alcoholWords) -> {
                getWord(alcoholWords)
            }

            currentWordList.contentEquals(goodWords) -> {
                getWord(goodWords)
            }

            currentWordList.contentEquals(badWords) -> {
                getWord(badWords)
            }

            else -> {
                getWord(drinksWords)
            }
        }

        presentWord = binding.tvText.text as String

        if (currentRound <= totalRounds) {
            binding.tvQuestion.text = currentRound.toString()
            currentRound++
        } else {
            if (currentStep < 7) {
                currentRound = 1
                currentStep++
                when (currentStep) {
                    2 -> {
                        infoDialogStep2()
                        touchLeftCategory = "good"
                        touchRightCategory = "bad"
                        binding.btnLeft.text = "Хорошо"
                        binding.btnRight.text = "Плохо"
                    }
                    3 -> {
                        infoDialogStep3()
                        touchLeftCategory = "goodsoft"
                        touchRightCategory = "badalco"
                        binding.btnLeft.text = "Хорошо или напиток"
                        binding.btnRight.text = "Плохо или алкоголь"
                    }
                    4 -> {
                        infoDialogStep4()
                    }
                    5 -> {
                        infoDialogStep5()
                        touchLeftCategory = "alco"
                        touchRightCategory = "soft"
                        binding.btnLeft.text = "Алкоголь"
                        binding.btnRight.text = "Напиток"
                    }
                    6 -> {
                        infoDialogStep6()
                        touchLeftCategory = "goodalco"
                        touchRightCategory = "badsoft"
                        binding.btnLeft.text = "Алкоголь или хорошо"
                        binding.btnRight.text = "Напитки или плохо"
                    }
                    7 -> {
                        infoDialogStep7()
                    }
                }
                binding.tvStep.text = currentStep.toString()
            } else {
                finishTest()
                binding.btnLeft.isEnabled = false
                binding.btnRight.isEnabled = false
            }
        }
    }

    private fun checkAnswer(selectedCategory: Int) {
        var correctCategory = -1
        correctCategory = if (currentStep == 5) {
            if (currentWordList.contentEquals(alcoholWords)) 0 else 1
        } else if (currentStep >= 6) {
            if (currentWordList.contentEquals(alcoholWords) || currentWordList.contentEquals(
                    goodWords
                )
            ) 0 else 1
        } else {
            if (currentWordList.contentEquals(drinksWords) || currentWordList.contentEquals(
                    goodWords
                )
            ) 0 else 1
        }

        if (selectedCategory == correctCategory) {
            correctAnswer = "true"
            saveData()
            updateWordList()
        } else {
            correctAnswer = "false"
            saveData()
            Toast.makeText(requireContext(), "Неверно! Сделайте правильный выбор", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData(){
        var touchNameCategory = ""
        touchNameCategory = if (touchCategory=="left") {
            touchLeftCategory
        } else {
            touchRightCategory
        }

        val dynamicRow = mutableListOf(
            touchStartTimeUnixTimestamp.toString(),
            currentStep.toString(),
            touchDurationSeconds.toString(),
            touchCategory,
            touchNameCategory,
            correctAnswer,
            presentWord.orEmpty()
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

    private fun educationAnimation() {
        binding.apply {
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.iat)
                soundPlayer?.playSound(R.raw.iat_prestart)
                okBtn.setOnClickListener {
                    soundPlayer?.stopSound()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    constraintLayout3.isVisible = true
                    clBtn.isVisible = true
                    tvText.isVisible = true
                    infoDialogInstructionTest()
                }
            }

            constraintLayout3.isVisible = false
            clBtn.isVisible = false
            tvText.isVisible = false
        }
    }

    private fun calculateTouchDuration() {
        val touchDurationMillis = touchEndTimeMillis - touchStartTimeMillis
        touchDurationSeconds = touchDurationMillis
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun infoDialogInstructionTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_ready_btn_ok)
        alertDialogBuilder.setTitle("Правило тестирования") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между напитками и алкоголем. Отнесите товар к определенной категории.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()

            binding.btnLeft.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchCategory = "left"
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

            binding.btnRight.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchCategory = "right"
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
            infoDialogStep1()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep1() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_1)
        alertDialogBuilder.setTitle("Этап 1") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Напитки”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep2() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_2)
        alertDialogBuilder.setTitle("Этап 2") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Хорошим” и “Плохим”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep3() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_3)
        alertDialogBuilder.setTitle("Этап 3") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Напитки + Хорошо” и “Алкоголь + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep4() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_4)
        alertDialogBuilder.setTitle("Этап 4") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Напитки + Хорошо” и “Алкоголь + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep5() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_5)
        alertDialogBuilder.setTitle("Этап 5") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Напитки”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep6() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_6)
        alertDialogBuilder.setTitle("Этап 6") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Хорошо” и “Напитки + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep7() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.iat_step_7)
        alertDialogBuilder.setTitle("Этап 7") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Хорошо” и “Напитки + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папку") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            viewModelUploaderFile.sendFile(idPatient = patientId, fileName, data)
            soundPlayer?.stopSound()
            dialog.dismiss()
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
        private const val TEST_NAME = "IAT"
        private const val TEST_FILE_EXTENSION = ".csv"
        @JvmStatic
        fun newInstance(patientId: Int = -1): IATTest{
            val fragment = IATTest()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}
