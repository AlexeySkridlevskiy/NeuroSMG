package com.example.neurosmg.doctorProfile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentDoctorProfileBinding
import androidx.lifecycle.ViewModelProvider

class DoctorProfile : Fragment() {
    lateinit var binding: FragmentDoctorProfileBinding
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[DoctorProfileViewModel::class.java]
    }
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
        mainActivityListener?.updateToolbarState(ToolbarState.DoctorProfile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorProfileBinding.inflate(inflater)

        binding.tvLoginDoctor.text = viewModel.getUsername()
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = DoctorProfile()
    }
}