package com.example.neurosmg.patientTestList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.testsPage.TestAdapter
import com.example.neurosmg.testsPage.TestItem
import com.example.neurosmg.testsPage.TestPageViewModel

class PatientTestList : Fragment() {

    lateinit var binding: FragmentPatientTestListBinding
    private val adapter = PatientAdapter()

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[PatientsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPatientTestListBinding.inflate(inflater)
        val value = arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME)
        binding.tvTest.text = value
        init()
        return binding.root
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(requireContext())
        adapter.addPatient(patient = viewModel.getListOfPatient())
        rcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = PatientTestList()
    }
}