package com.example.neurosmg.doctorProfile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentDoctorProfileBinding
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.common.State
import com.example.neurosmg.common.showToast

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()

        viewModel.profileLD.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Error -> {
                    binding.progressBar.isVisible = false
                    if (state.data.errorMessage != null) {
                        showToast(state.data.errorMessage)
                    }
                }

                State.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is State.Success -> {
                    binding.progressBar.isVisible = false
                    if (state.data.username != null) {
                        binding.tvLoginDoctor.text = state.data.username
                    }
                }
            }
        }
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