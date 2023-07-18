package com.example.neurosmg.patientTestList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.testsPage.TestAdapter
import com.example.neurosmg.testsPage.TestItem

class PatientTestList : Fragment() {

    lateinit var binding: FragmentPatientTestListBinding
    private val adapter = PatientAdapter()
    private val patientList = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPatientTestListBinding.inflate(inflater)
        val value = arguments?.getString("test_title")
        binding.tvTest.text = value
        init()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PatientTestList()
    }

    private fun init() = with(binding){

        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
        patientList.forEach{el->
            val patient = Patient(el)
            adapter.addPatient(patient)
        }
    }
}