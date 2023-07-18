package com.example.neurosmg.testsPage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.databinding.FragmentTestsPageBinding
import com.example.neurosmg.login.LoginViewModel
import com.example.neurosmg.patientTestList.PatientTestList

class TestsPage : Fragment(), ItemOnClickListener {

    private lateinit var binding: FragmentTestsPageBinding
    private val fragment = PatientTestList.newInstance()

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[TestPageViewModel::class.java]
    }

    private val bundle = Bundle()
    private val adapter = TestAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestsPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {

        rcView.layoutManager = LinearLayoutManager(requireContext())
        adapter.addTest(testItem = viewModel.getTests())
        rcView.adapter = adapter
    }

    override fun onItemClick(item: TestItem) {
        bundle.putString(KeyOfArgument.KEY_OF_TEST_NAME, item.title)
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, fragment)
            .addToBackStack(Screen.MAIN_PAGE)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TestsPage()
    }
}