package com.example.neurosmg.patientTestList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.testsPage.TestItem

class PatientTestList : Fragment() {

    lateinit var binding: FragmentPatientTestListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPatientTestListBinding.inflate(inflater)
        val test = requireActivity().intent.getParcelableExtra<TestItem>("test")
        if(test != null){
            binding.tvTest.text = test.title
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(intent: Intent) = PatientTestList()
    }
}