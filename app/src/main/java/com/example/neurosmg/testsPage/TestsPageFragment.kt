package com.example.neurosmg.testsPage

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.setScreenOrientation
import com.example.neurosmg.common.toFragment
import com.example.neurosmg.databinding.FragmentTestsPageBinding

class TestsPageFragment : Fragment(), ItemOnClickListener {

    private lateinit var binding: FragmentTestsPageBinding

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[TestPageViewModel::class.java]
    }

    private val adapter = TestAdapter(this)

    private var mainActivityListener: MainActivityListener? = null

    private var patientId: Int = -1

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
//        Toast.makeText(requireContext(), "ID пациента ${arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT)}", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(item: TestItem) {

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, item.title.toFragment(patientId))
            .addToBackStack(Screen.TESTS_PAGE)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {

        @JvmStatic
        fun newInstance(
            patientId: Int = -1
        ): TestsPageFragment {
            val fragment = TestsPageFragment()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}