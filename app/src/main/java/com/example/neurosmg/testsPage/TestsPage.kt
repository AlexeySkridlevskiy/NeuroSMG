package com.example.neurosmg.testsPage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.databinding.FragmentTestsPageBinding
import com.example.neurosmg.patientTestList.PatientTestList

class TestsPage : Fragment(), ItemOnClickListener {
    lateinit var binding: FragmentTestsPageBinding
    private val adapter = TestAdapter(this)
    private val testTitleList = listOf(
        "FOT",
        "RAT",
        "IAT",
        "GNG",
        "SCT",
        "TMT",
        "CBT",
        "MRT",
    )
    private var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestsPageBinding.inflate(inflater)
        init()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = TestsPage()
    }

    private fun init() = with(binding){
        
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
        testTitleList.forEach{el->
            val test = TestItem(el)
            adapter.addTest(test)
        }
    }

    override fun onItemClick(item: TestItem) {
        Log.d("MyLog", "Click item ${item.title}")
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, PatientTestList.newInstance())
            .addToBackStack(Screen.MAIN_PAGE)
            .commit()
    }
}