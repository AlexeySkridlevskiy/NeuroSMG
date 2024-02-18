package com.example.neurosmg.tests.iat2

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
import com.example.neurosmg.databinding.FragmentIATTest2Binding
import com.example.neurosmg.tests.cbt.CbtTestViewModel
import com.example.neurosmg.tests.iat.IATTest
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode
import com.example.neurosmg.utils.generateName

class IATTest2 : Fragment() {

    lateinit var binding: FragmentIATTest2Binding
    private var mainActivityListener: MainActivityListener? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    private val totalRounds = 20
    private var currentRound = 1
    private var currentStep = 1

    private val fruitsPic = arrayOf(R.drawable.fruit_1, R.drawable.fruit_2, R.drawable.fruit_3,
        R.drawable.fruit_4, R.drawable.fruit_5, R.drawable.fruit_6, R.drawable.fruit_7)
    private val alcoholPic = arrayOf(R.drawable.alco_1, R.drawable.alco_2, R.drawable.alco_3,
        R.drawable.alco_4, R.drawable.alco_5, R.drawable.alco_6, R.drawable.alco_7)
    private val smilePic = arrayOf(R.drawable.smile_1, R.drawable.smile_2, R.drawable.smile_3,
        R.drawable.smile_4, R.drawable.smile_5, R.drawable.smile_6, R.drawable.smile_7)
    private val grimacePic = arrayOf(R.drawable.grimace_1, R.drawable.grimace_2, R.drawable.grimace_3,
        R.drawable.grimace_4, R.drawable.grimace_5, R.drawable.grimace_6, R.drawable.grimace_7)

    private lateinit var currentWordList: Array<Int>
    private var soundPlayer: SoundPlayer? = null

    private var touchStartTimeUnixTimestamp: Long = 0
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private var touchCategory: String = ""
    private var touchLeftCategory: String = "alco"
    private var touchRightCategory: String = "fruit"
    private var correctAnswer: String = ""

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

    private fun updateWordList() {
        currentWordList = if(currentStep==1){
            if((0..1).random()==0) fruitsPic else alcoholPic
        }else if(currentStep==2){
            if((0..1).random()==0) smilePic else grimacePic
        }else if(currentStep==3||currentStep==4){
            if((0..1).random()==0){
                if((0..1).random()==0) fruitsPic else alcoholPic
            }else{
                if((0..1).random()==0) smilePic else grimacePic
            }
        }else if(currentStep==5){
            if((0..1).random()==0) smilePic else grimacePic
        }else if(currentStep==6||currentStep==7){
            if((0..1).random()==0){
                if((0..1).random()==0) alcoholPic else fruitsPic
            }else{
                if((0..1).random()==0) grimacePic else smilePic
            }
        }else{
            if((0..1).random()==0) fruitsPic else alcoholPic
        }

        updateWord()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateWord() {
        if (currentWordList.contentEquals(fruitsPic)){
            binding.imgView.setImageResource(fruitsPic.random())
        }else if(currentWordList.contentEquals(alcoholPic)){
            binding.imgView.setImageResource(alcoholPic.random())
        }else if(currentWordList.contentEquals(smilePic)){
            binding.imgView.setImageResource(smilePic.random())
        }else if(currentWordList.contentEquals(grimacePic)){
            binding.imgView.setImageResource(grimacePic.random())
        }
//        presentWord = binding.imgView.

        if (currentRound <= totalRounds) {
            binding.tvQuestion.text = currentRound.toString()
            currentRound++
        } else {
            if (currentStep < 7){
                currentRound = 1
                currentStep++
                if (currentStep==2){
                    infoDialogStep2()
                    touchLeftCategory = "smile"
                    touchRightCategory = "grimace"
                    binding.btnLeft.text = "Улыбки"
                    binding.btnRight.text = "Гримасы"
                }else if(currentStep==3){
                    infoDialogStep3()
                    touchLeftCategory = "smilealco"
                    touchRightCategory = "grimacefruit"
                    binding.btnLeft.text = "Улыбки или алкоголь"
                    binding.btnRight.text = "Гримасы или фрукты"
                }else if(currentStep==4){
                    infoDialogStep4()
                }else if(currentStep==5){
                    infoDialogStep5()
                    touchLeftCategory = "grimace"
                    touchRightCategory = "smile"
                    binding.btnLeft.text = "Гримасы"
                    binding.btnRight.text = "Улыбки"
                }else if(currentStep==6){
                    infoDialogStep6()
                    touchLeftCategory = "grimacealco"
                    touchRightCategory = "smilefruit"
                    binding.btnLeft.text = "Гримасы или алкоголь"
                    binding.btnRight.text = "Улыбки или фрукты"
                }else if(currentStep==7){
                    infoDialogStep7()
                }
                binding.tvStep.text = currentStep.toString()
            }else{
                finishTest()
                binding.btnLeft.isEnabled = false
                binding.btnRight.isEnabled = false
            }
        }
    }

    private fun checkAnswer(selectedCategory: Int) {
        var correctCategory = -1
        correctCategory = if(currentStep==5){
            if (currentWordList.contentEquals(smilePic)) 0 else 1
        }else if(currentStep>=6){
            if (currentWordList.contentEquals(fruitsPic)||currentWordList.contentEquals(smilePic)) 0 else 1
        }else{
            if (currentWordList.contentEquals(fruitsPic)||currentWordList.contentEquals(grimacePic)) 0 else 1
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
        touchNameCategory = if(touchCategory=="left"){
            touchLeftCategory
        }else{
            touchRightCategory
        }
//        Log.d("MyLog", "$touchStartTimeUnixTimestamp, $currentStep-${currentRound-1}, $touchDurationSeconds, $touchCategory, $touchNameCategory, $correctAnswer, $presentWord")
        val dynamicRow = mutableListOf(
            touchStartTimeUnixTimestamp.toString(), (currentStep-(currentRound-1)).toString(),
            touchDurationSeconds.toString(), touchCategory, touchNameCategory, correctAnswer
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
        alertDialogBuilder.setTitle("Правило тестирования") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между фруктами и алкоголем. Отнесите товар к определенной категории.") // TODO: в ресурсы выноси
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
                        checkAnswer(1)
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
        alertDialogBuilder.setTitle("Этап 1") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Фрукты”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_2)
        alertDialogBuilder.setTitle("Этап 2") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Улыбками” и “Гримасами”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_3)
        alertDialogBuilder.setTitle("Этап 3") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Улыбки” и “Фрукты + Гримасы”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_4)
        alertDialogBuilder.setTitle("Этап 4") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Улыбки” и “Фрукты + Гримасы”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_5)
        alertDialogBuilder.setTitle("Этап 5") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Улыбками” и “Гримасами”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_6)
        alertDialogBuilder.setTitle("Этап 6") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Гримасы” и “Фрукты + Улыбки”.") // TODO: в ресурсы выноси
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
        soundPlayer?.playSound(R.raw.iat2_step_7)
        alertDialogBuilder.setTitle("Этап 7") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Гримасы” и “Фрукты + Улыбки”.") // TODO: в ресурсы выноси
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
        private const val TEST_NAME = "IAT2"
        private const val TEST_FILE_EXTENSION = ".csv"
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
