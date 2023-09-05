package com.example.neurosmg.patientTestList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.State
import com.example.neurosmg.common.toFragment
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.patientTestList.addPatient.AddPatient
import com.example.neurosmg.patientTestList.patientProfile.PatientProfile

class PatientTestList : Fragment() {

    lateinit var binding: FragmentPatientTestListBinding

    private lateinit var fragment: Fragment
    private lateinit var bundle: Bundle
    private val adapter = PatientAdapter()

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[PatientsViewModel::class.java]
    }

    private val patientStateViewModel by lazy {
        ViewModelProvider(requireActivity())[StatePatientViewModel::class.java]
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPatientTestListBinding.inflate(inflater)
        viewModel.fetchUserPatients()
        return binding.root
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.userPatients.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Error -> {
                    progressBar.isVisible = false
                }

                State.Loading -> {
                    progressBar.isVisible = true
                }

                is State.Success -> {
                    progressBar.isVisible = false
                    adapter.addPatient(state.data)
                    rcView.adapter = adapter
                }

                State.Empty -> {}
            }
        }

        flButton.setOnClickListener {
            fragment = AddPatient.newInstance()
            replaceFragment(fragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (patientStateViewModel.getStatePatientList().navigateTo) {
            ScreenNavigationMenu.TO_ARCHIVE -> {
                binding.tvArchive.isVisible = true
                binding.flButton.isVisible = false
                mainActivityListener?.updateToolbarState(ToolbarState.Archive)
            }

            ScreenNavigationMenu.TO_PATIENT_LIST -> {
                mainActivityListener?.updateToolbarState(ToolbarState.PatientList)
                binding.flButton.isVisible = true
            }

            else -> {
                mainActivityListener?.updateToolbarState(ToolbarState.PatientList)
            }
        }

        init()

        adapter.onPatientItemClick = object : PatientAdapter.OnPatientClickListener {
            override fun onPatientIdClick(patient: Int) {
                fragment = when (patientStateViewModel.getStatePatientList().navigateTo) {
                    ScreenNavigationMenu.TO_ARCHIVE -> {
                        PatientProfile.newInstance()
                    }

                    ScreenNavigationMenu.TO_TESTS -> {
                        patientStateViewModel.getStatePatientList().navigateToTest?.toFragment()!!
                    }

                    ScreenNavigationMenu.TO_PATIENT_LIST -> {
                        val patientProfileFragment = PatientProfile.newInstance()
                        val bundle = Bundle()
                        bundle.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patient)
                        patientProfileFragment.arguments = bundle
                        patientProfileFragment
                    }
                }

                replaceFragment(fragment)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(tag)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        arguments?.clear()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PatientTestList()
    }
}