package com.example.neurosmg.testsPage

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.databinding.FragmentTestsPageBinding
import com.example.neurosmg.patientTestList.PatientTestList
import com.example.neurosmg.patientTestList.StatePatientViewModel

class TestsPage : Fragment(), ItemOnClickListener {

    private lateinit var binding: FragmentTestsPageBinding
    private val fragment = PatientTestList.newInstance()

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[TestPageViewModel::class.java]
    }

    private val patientStateViewModel by lazy {
        ViewModelProvider(requireActivity())[StatePatientViewModel::class.java]
    }

    private val adapter = TestAdapter(this)

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
        binding = FragmentTestsPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        mainActivityListener?.updateToolbarState(ToolbarState.TestPage)
        init()
    }

    private fun init() = with(binding) {

        rcView.layoutManager = LinearLayoutManager(requireContext())
        adapter.addTest(testItem = viewModel.getTests())
        rcView.adapter = adapter
    }

    override fun onItemClick(item: TestItem) {

        patientStateViewModel.navToTests(item.title)

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(Screen.TESTS_PAGE)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = TestsPage()
    }
}