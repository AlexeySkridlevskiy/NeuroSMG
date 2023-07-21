package com.example.neurosmg.Tests.TMT

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentGNGTestBinding
import com.example.neurosmg.databinding.FragmentTMTTestBinding

class TMTTest : Fragment() {

    lateinit var binding: FragmentTMTTestBinding
    private var mainActivityListener: MainActivityListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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