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
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentMRTTestBinding
import com.example.neurosmg.testsPage.TestsPageFragment
import com.example.neurosmg.utils.exitFullScreenMode
import kotlin.random.Random
import kotlin.random.nextInt

class MRTTest : Fragment() {
    lateinit var binding: FragmentMRTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    private var steps = 0
    private var flag = false
    private var soundPlayer: SoundPlayer? = null
    private val image1Resources = arrayOf(
        R.drawable.figure_1_1,
        R.drawable.figure_1_2,
        R.drawable.figure_1_3,
        R.drawable.figure_1_4,
    )

    private val image2Resources = arrayOf(
        R.drawable.figure_2_1,
        R.drawable.figure_2_2,
        R.drawable.figure_2_3,
        R.drawable.figure_2_4,
        R.drawable.figure_2_5,
        R.drawable.figure_2_6,
    )

    private val image3Resources = arrayOf(
        R.drawable.figure_3_1,
        R.drawable.figure_3_2,
        R.drawable.figure_3_3,
        R.drawable.figure_3_4,
        R.drawable.figure_3_5,
        R.drawable.figure_3_6,
    )

    private val image4Resources = arrayOf(
        R.drawable.figure_4_1,
        R.drawable.figure_4_2,
        R.drawable.figure_4_3,
        R.drawable.figure_4_4,
        R.drawable.figure_4_5,
        R.drawable.figure_4_6,
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
//            if(flag){
//                binding.textView12.text = "true"
//            }else{
//                binding.textView12.text = "false"
//            }
            startTest()
        }
        binding.buttonRight.setOnClickListener {
//            if(flag){
//                binding.textView12.text = "false"
//            }else{
//                binding.textView12.text = "true"
//            }
            startTest()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.MRTTest)
        educationAnimation()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MRTTest()
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
        if(steps==50){
            endTest()
        }else {
            steps++
            binding.tvSteps.text = steps.toString()

            var randomResource = randomImageResource()
            var randomIndex = randomImageFromResource(randomResource)
            if (randomResource == 1) {
                binding.imgView1.setImageResource(image1Resources[randomIndex])
            } else if (randomResource == 2) {
                binding.imgView1.setImageResource(image2Resources[randomIndex])
            } else if (randomResource == 3) {
                binding.imgView1.setImageResource(image3Resources[randomIndex])
            } else if (randomResource == 4) {
                binding.imgView1.setImageResource(image4Resources[randomIndex])
            }

            val randomResource1 = randomResource

            randomResource = randomImageResource()
            randomIndex = randomImageFromResource(randomResource)
            if (randomResource == 1) {
                binding.imgView2.setImageResource(image1Resources[randomIndex])
            } else if (randomResource == 2) {
                binding.imgView2.setImageResource(image2Resources[randomIndex])
            } else if (randomResource == 3) {
                binding.imgView2.setImageResource(image3Resources[randomIndex])
            } else if (randomResource == 4) {
                binding.imgView2.setImageResource(image4Resources[randomIndex])
            }

            val randomResource2 = randomResource
            flag = randomResource1==randomResource2
        }
    }

    private fun randomImageResource(): Int {
        return Random.nextInt(1..4)
    }

    private fun randomImageFromResource(randomResource: Int): Int {
        var randomIndex = 0
        if(randomResource==1){
            randomIndex = Random.nextInt(image1Resources.size)
        }else if(randomResource==2){
            randomIndex = Random.nextInt(image2Resources.size)
        }else if(randomResource==3){
            randomIndex = Random.nextInt(image3Resources.size)
        }else if(randomResource==4){
            randomIndex = Random.nextInt(image4Resources.size)
        }

        return randomIndex
    }

    private fun endTest(){
        infoDialogEndTest()
    }

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование окончено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные сохранены в папке!") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
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
}