package com.example.neurosmg.tests.mrt

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentMRTTestBinding
import com.example.neurosmg.utils.exitFullScreenMode

class MRTTest : Fragment() {
    lateinit var binding: FragmentMRTTestBinding
    private var mainActivityListener: MainActivityListener? = null

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
        binding = FragmentMRTTestBinding.inflate(inflater)
        educationAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.MRTTest)
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
            lottieLayout.run {
                root.isVisible = true
                animationLottie.setAnimation(R.raw.mrt)
                okBtn.setOnClickListener {
                    root.isVisible = false
                    activity?.exitFullScreenMode()
                    tvText.isVisible = true
//                    square1.isVisible = true
//                    square2.isVisible = true
//                    square3.isVisible = true
//                    square4.isVisible = true
//                    btnStart.isVisible = true
//                    infoDialogInstruction()
                }
            }
            tvText.isVisible = false
//            imageView2.isVisible = false
//            square1.isVisible = false
//            square2.isVisible = false
//            square3.isVisible = false
//            square4.isVisible = false
//            btnStart.isVisible = false
        }
    }
}