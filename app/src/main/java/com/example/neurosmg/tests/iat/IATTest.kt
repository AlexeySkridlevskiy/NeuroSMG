package com.example.neurosmg.tests.iat

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
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentIATTestBinding
import com.example.neurosmg.tests.rat.RATTest
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode

class IATTest : Fragment() {

    lateinit var binding: FragmentIATTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private val totalRounds = 20
    private var currentRound = 1
    private var currentStep = 1

    private val drinksWords = arrayOf("Чай", "Лимонад", "Вода", "Сок", "Морс")
    private val alcoholWords = arrayOf("Водка", "Виски", "Вино", "Пиво", "Коньяк")
    private val goodWords = arrayOf("Вкусно", "Полезно", "Приятно", "Положительно", "Мило")
    private val badWords = arrayOf("Мерзко", "Отрицательно", "Гадко", "Отвратительно", "Ужасно")

    private lateinit var currentWordList: Array<String>
    private var soundPlayer: SoundPlayer? = null

    private var touchStartTimeUnixTimestamp: Long = 0
    private var touchStartTimeMillis: Long = 0
    private var touchEndTimeMillis: Long = 0
    private var touchDurationSeconds: Long = 0
    private var touchCategory: String = ""
    private var touchLeftCategory: String = "soft"
    private var touchRightCategory: String = "alco"
    private var correctAnswer: String = ""
    private var presentWord: String = ""

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
    }

    private fun updateWordList() {
        currentWordList = if(currentStep==1){
            if((0..1).random()==0) drinksWords else alcoholWords
        }else if(currentStep==2){
            if((0..1).random()==0) goodWords else badWords
        }else if(currentStep==3||currentStep==4){
            if((0..1).random()==0){
                if((0..1).random()==0) drinksWords else alcoholWords
            }else{
                if((0..1).random()==0) goodWords else badWords
            }
        }else if(currentStep==5){
            if((0..1).random()==0) alcoholWords else drinksWords
        }else if(currentStep==6||currentStep==7){
            if((0..1).random()==0){
                if((0..1).random()==0) alcoholWords else drinksWords
            }else{
                if((0..1).random()==0) badWords else goodWords
            }
        }else{
            if((0..1).random()==0) drinksWords else alcoholWords
        }

        updateWord()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateWord() {
        if (currentWordList.contentEquals(drinksWords)){
            binding.tvText.text = drinksWords.random()
        }else if(currentWordList.contentEquals(alcoholWords)){
            binding.tvText.text = alcoholWords.random()
        }else if(currentWordList.contentEquals(goodWords)){
            binding.tvText.text = goodWords.random()
        }else if(currentWordList.contentEquals(badWords)){
            binding.tvText.text = badWords.random()
        }
        presentWord = binding.tvText.text as String

            if (currentRound <= totalRounds) {
                binding.tvQuestion.text = currentRound.toString()
                currentRound++
            } else {
                if (currentStep < 7){
                    currentRound = 1
                    currentStep++
                    if (currentStep==2){
                        infoDialogStep2()
                        touchLeftCategory = "good"
                        touchRightCategory = "bad"
                        binding.btnLeft.text = "Хорошо"
                        binding.btnRight.text = "Плохо"
                    }else if(currentStep==3){
                        infoDialogStep3()
                        touchLeftCategory = "goodsoft"
                        touchRightCategory = "badalco"
                        binding.btnLeft.text = "Хорошо или напиток"
                        binding.btnRight.text = "Плохо или алкоголь"
                    }else if(currentStep==4){
                        infoDialogStep4()
                    }else if(currentStep==5){
                        infoDialogStep5()
                        touchLeftCategory = "alco"
                        touchRightCategory = "soft"
                        binding.btnLeft.text = "Алкоголь"
                        binding.btnRight.text = "Напиток"
                    }else if(currentStep==6){
                        infoDialogStep6()
                        touchLeftCategory = "goodalco"
                        touchRightCategory = "badsoft"
                        binding.btnLeft.text = "Алкоголь или хорошо"
                        binding.btnRight.text = "Напитки или плохо"
                    }else if(currentStep==7){
                        infoDialogStep7()
                    }
                    binding.tvStep.text = currentStep.toString()
                }else{
                    infoDialogEndTest()
                    binding.btnLeft.isEnabled = false
                    binding.btnRight.isEnabled = false
                }
            }
    }

    private fun checkAnswer(selectedCategory: Int) {
        var correctCategory = -1
        correctCategory = if(currentStep==5){
            if (currentWordList.contentEquals(alcoholWords)) 0 else 1
        }else if(currentStep>=6){
            if (currentWordList.contentEquals(alcoholWords)||currentWordList.contentEquals(goodWords)) 0 else 1
        }else{
            if (currentWordList.contentEquals(drinksWords)||currentWordList.contentEquals(goodWords)) 0 else 1
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
        Log.d("MyLog", "$touchStartTimeUnixTimestamp, $currentStep-${currentRound-1}, $touchDurationSeconds, $touchCategory, $touchNameCategory, $correctAnswer, $presentWord")
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
        touchDurationSeconds = touchDurationMillis // Преобразовать в секунды с точностью до тысячных миллисекунд
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

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папку") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            soundPlayer?.stopSound()
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPageFragment.newInstance())
                .addToBackStack(Screen.TESTS_PAGE)
                .commit()
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
        private const val TEST_NAME = "RAT"
        private const val TEST_FILE_EXTENSION = ".csv"
        @JvmStatic
        fun newInstance() = IATTest()
    }
}
