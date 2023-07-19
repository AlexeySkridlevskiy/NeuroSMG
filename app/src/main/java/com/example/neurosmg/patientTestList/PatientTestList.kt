package com.example.neurosmg.patientTestList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.Tests.FOTTest
import com.example.neurosmg.Tests.IATTest
import com.example.neurosmg.Tests.RATTest
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.testsPage.TestAdapter

class PatientTestList : Fragment(), PatientOnClickListener {

    lateinit var binding: FragmentPatientTestListBinding
    private val bundle = Bundle()
    private lateinit var fragment: Fragment
    private val adapter = PatientAdapter(this)
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

    override fun onItemClick(patient: Patient) {
        when (arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME)){
            "FOT" ->  {
                fragment = FOTTest.newInstance()
            }
            "RAT" -> {
                fragment = RATTest.newInstance()
            }
            "IAT" -> {
                fragment = IATTest.newInstance()
            }
        }
        bundle.putString(KeyOfArgument.KEY_OF_ID_PATIENT, patient.id)
        bundle.putString(KeyOfArgument.KEY_OF_TEST_NAME, arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME))
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, fragment)
            .addToBackStack(Screen.MAIN_PAGE)
            .commit()
    }
}