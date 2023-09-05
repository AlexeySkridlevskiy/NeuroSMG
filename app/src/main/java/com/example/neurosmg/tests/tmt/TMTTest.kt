package com.example.neurosmg.tests.tmt

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentTMTTestBinding

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

        labyrinthView = binding.labyrinthView

        labyrinthView.setLabyrinthCompletionListener(this)
    }

    override fun onLabyrinthCompleted(steps: Int) {
        binding.tvLabSteps.text = steps.toString()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = TMTTest()
    }
}
