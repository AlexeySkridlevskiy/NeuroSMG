package com.example.neurosmg.testsPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.FragmentTestsPageBinding

class TestsPage : Fragment() {
    lateinit var binding: FragmentTestsPageBinding
    private val adapter = TestAdapter()
    private val testTitleList = listOf(
        "Test 1",
        "Test 2",
        "Test 3",
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
}