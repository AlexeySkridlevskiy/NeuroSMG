package com.example.neurosmg.tests.tmt

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentTMTTestBinding
import com.example.neurosmg.testsPage.TestsPage
import com.example.neurosmg.utils.exitFullScreenMode

class TMTTest : Fragment(), LabyrinthView.LabyrinthCompletionListener {

    private lateinit var binding: FragmentTMTTestBinding
    private var mainActivityListener: MainActivityListener? = null
    private lateinit var labyrinthView: LabyrinthView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
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
        binding = FragmentTMTTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.TMTTest)
        educationAnimation()
    }

    override fun onLabyrinthCompleted(steps: Int) {
        if(steps==21){
            infoDialogEndTest()
        }else{
            binding.tvLabSteps.text = steps.toString()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = TMTTest()
    }

    private fun educationAnimation() {
//        mainActivityListener?.updateToolbarState(ToolbarState.HideToolbar)
        binding.apply {
//            activity?.enterFullScreenMode()
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.tmt)
                okBtn.setOnClickListener {
                    infoDialog()
                    root.isVisible = false
                    activity?.exitFullScreenMode()
//                    mainActivityListener?.updateToolbarState(ToolbarState.SCTTest)
                    constraintLayout5.isVisible = true
                    labyrinthView.isVisible = true
                }
            }
            constraintLayout5.isVisible = false
            labyrinthView.isVisible = false
        }
    }

    private fun infoDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Правила тестирования") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Сделайте выбор между напитками и алкоголем. Отнесите товар к определенной категории.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            labyrinthView = binding.labyrinthView

            labyrinthView.setLabyrinthCompletionListener(this)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogEndTest() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Тестирование пройдено") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папке.") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TestsPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }
}
