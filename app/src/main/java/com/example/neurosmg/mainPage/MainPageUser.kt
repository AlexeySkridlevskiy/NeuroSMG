package com.example.neurosmg.mainPage

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.aboutProgramPage.AboutProgramPage
import com.example.neurosmg.databinding.FragmentMainPageUserBinding
import com.example.neurosmg.patientTestList.PatientTestList
import com.example.neurosmg.testsPage.TestsPage

class MainPageUser : Fragment() {
    lateinit var binding: FragmentMainPageUserBinding

    private var mainActivityListener: MainActivityListener? = null
    private val bundle = Bundle()
    private val fragment = PatientTestList.newInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        } else {
            throw RuntimeException("$context must implement MainActivityListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageUserBinding.inflate(inflater)

        binding.btnGoTesting.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, TestsPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        binding.btnArchive.setOnClickListener {
            bundle.putBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE, true)
            fragment.arguments = bundle
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, fragment)
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        binding.btnPatients.setOnClickListener {
            bundle.putBoolean(KeyOfArgument.KEY_OF_MAIN_TO_PATIENT, true)
            fragment.arguments = bundle
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, fragment)
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }

        binding.btnAboutProgram.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, AboutProgramPage.newInstance())
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.MainPage)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainPageUser()
    }
}