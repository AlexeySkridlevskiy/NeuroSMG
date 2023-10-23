package com.example.neurosmg.questionnaires

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.neurosmg.common.toFragment
import com.example.neurosmg.databinding.FragmentQuestionnaireListBinding
import com.example.neurosmg.testsPage.TestPageViewModel
import com.example.neurosmg.testsPage.TestsPageFragment

class QuestionnaireList : Fragment(), ItemOnClickListener {

    private lateinit var binding: FragmentQuestionnaireListBinding

    private var mainActivityListener: MainActivityListener? = null
    private val adapter = QuestionnaireAdapter(this)
    private var patientId: Int = -1

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[QuestionnaireListViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onResume() {
        super.onResume()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionnaireListBinding.inflate(inflater)
        init()
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        arguments?.clear()
        mainActivityListener = null
    }

    private fun init() = with(binding) {

        rcView.layoutManager = LinearLayoutManager(requireContext())
        adapter.addTest(questionnaireItem = viewModel.getQuestionnaires())
        rcView.adapter = adapter

    }

    companion object {
        @JvmStatic
        fun newInstance(
            patientId: Int = -1
        ): QuestionnaireList {
            val fragment = QuestionnaireList()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onItemClick(item: QuestionnaireItem) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, item.title.toFragment(patientId))
            .addToBackStack(Screen.QUESTIONNAIRE)
            .commit()
    }
}