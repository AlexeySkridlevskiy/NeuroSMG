package com.example.neurosmg.tests.iat

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
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentIATTestBinding
import com.example.neurosmg.testsPage.TestsPage
import com.example.neurosmg.utils.exitFullScreenMode
import kotlin.random.Random

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

    companion object {
        @JvmStatic
        fun newInstance() = IATTest()
    }

    private fun educationAnimation() {
        binding.apply {
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.iat)
                okBtn.setOnClickListener {
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
            binding.btnLeft.setOnClickListener { checkAnswer(1) }
            binding.btnRight.setOnClickListener { checkAnswer(0) }
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

            if (currentRound <= totalRounds) {
                binding.tvQuestion.text = currentRound.toString()
                currentRound++
            } else {
                if (currentStep < 7){
                    currentRound = 1
                    currentStep++
                    if (currentStep==2){
                        infoDialogStep2()
                        binding.btnLeft.text = "Хорошо"
                        binding.btnRight.text = "Плохо"
                    }else if(currentStep==3){
                        infoDialogStep3()
                        binding.btnLeft.text = "Хорошо или напиток"
                        binding.btnRight.text = "Плохо или алкоголь"
                    }else if(currentStep==4){
                        infoDialogStep4()
                    }else if(currentStep==5){
                        infoDialogStep5()
                        binding.btnLeft.text = "Алкоголь"
                        binding.btnRight.text = "Напиток"
                    }else if(currentStep==6){
                        infoDialogStep6()
                        binding.btnLeft.text = "Плохо или алкоголь"
                        binding.btnRight.text = "Хорошо или напиток"
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
        val correctCategory = if (currentWordList.contentEquals(drinksWords)||currentWordList.contentEquals(goodWords)) 0 else 1

        if (selectedCategory == correctCategory) {
            updateWordList()
        } else {
            Toast.makeText(requireContext(), "Неверно! Сделайте правильный выбор", Toast.LENGTH_SHORT).show()
        }
    }

    private fun infoDialogInstructionTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Правило тестирования") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между напитками и алкоголем. Отнесите товар к определенной категории.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            binding.btnLeft.setOnClickListener { checkAnswer(0) }
            binding.btnRight.setOnClickListener { checkAnswer(1) }
            infoDialogStep1()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep1() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 1") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Напитки”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep2() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 2") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Хорошим” и “Плохим”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep3() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 3") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Напитки + Хорошо” и “Алкоголь + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep4() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 4") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Напитки + Хорошо” и “Алкоголь + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep5() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 5") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь” и “Напитки”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep6() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 6") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Хорошо” и “Напитки + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            updateWordList()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogStep7() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Этап 7") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между “Алкоголь + Хорошо” и “Напитки + Плохо”.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
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
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPage.newInstance())
                .addToBackStack(Screen.TESTS_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

}
