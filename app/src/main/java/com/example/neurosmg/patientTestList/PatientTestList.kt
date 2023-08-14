package com.example.neurosmg.patientTestList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.State
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.patientTestList.addPatient.AddPatient
import com.example.neurosmg.patientTestList.entity.Patient
import com.example.neurosmg.patientTestList.patientProfile.PatientProfile
import com.example.neurosmg.tests.cbt.CBTTest
import com.example.neurosmg.tests.fot.FOTTest
import com.example.neurosmg.tests.gng.GNGTest
import com.example.neurosmg.tests.iat.IATTest
import com.example.neurosmg.tests.mrt.MRTTest
import com.example.neurosmg.tests.rat.RATTest
import com.example.neurosmg.tests.sct.SCTTest
import com.example.neurosmg.tests.tmt.TMTTest

class PatientTestList : Fragment(), PatientOnClickListener {

    lateinit var binding: FragmentPatientTestListBinding
    private val bundle = Bundle()
    private lateinit var fragment: Fragment
    private val adapter = PatientAdapter(this)
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[PatientsViewModel::class.java]
    }

    private var mainActivityListener: MainActivityListener? = null
    private var fragmentTag: String = ""

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
        binding = FragmentPatientTestListBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.userPatients.observe(viewLifecycleOwner) { state ->
            when(state) {
                State.Error -> {
                    progressBar.isVisible = false
                }
                State.Loading -> {
                    progressBar.isVisible = true
                }
                is State.Success -> {
                    progressBar.isVisible = false
                    adapter.addPatient(state.data)
                    rcView.adapter = adapter
                }
            }
        }

        if(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_PATIENT) == true){
            flButton.visibility = View.VISIBLE
        }
        flButton.setOnClickListener {
            fragment = AddPatient.newInstance()
            fragmentTag = Screen.ADD_PATIENT
            replaceFragment(fragment, fragmentTag)
        }
        if(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE) == true){
            tvArchive.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE) == true){
            mainActivityListener?.updateToolbarState(ToolbarState.Archive)
        } else {
            mainActivityListener?.updateToolbarState(ToolbarState.PatientList)
        }
    }

    override fun onItemClick(patient: Patient) {
        when(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_PATIENT)){
            true -> {
                //todo: прописать логику если переход шел на страницу пациентов
                Log.d("MyLog", "from main page to patient page")
                fragment = PatientProfile.newInstance()
            }
            false -> {
                when (arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME)){
                    "FOT" ->  { //todo: лучше вынести это в companionObject тут в классе под ключами.
                        // А лучше одинаковые ключи вынести в отдельное место и использовать их там.
                        fragment = FOTTest.newInstance()
                        saveTag(Screen.FOT_TEST)
                    }
                    "RAT" -> {
                        fragment = RATTest.newInstance()
                        saveTag(Screen.RAT_TEST)
                    }
                    "IAT" -> {
                        fragment = IATTest.newInstance()
                        saveTag(Screen.IAT_TEST)
                    }
                    "GNG" -> {
                        fragment = GNGTest.newInstance()
                        saveTag(Screen.GNG_TEST)
                    }
                    "SCT" -> {
                        fragment = SCTTest.newInstance()
                        saveTag(Screen.SCT_TEST)
                    }
                    "TMT" -> {
                        fragment = TMTTest.newInstance()
                        saveTag(Screen.TMT_TEST)
                    }
                    "CBT" -> {
                        fragment = CBTTest.newInstance()
                        saveTag(Screen.CBT_TEST)
                    }
                    "MRT" -> {
                        fragment = MRTTest.newInstance()
                        saveTag(Screen.MRT_TEST)
                    }
                }

                when(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE)){
                    true -> {
                        fragment = FOTTest.newInstance()
                    }
                    false -> {
                    }
                    else->{

                    }
                }
            }
            else -> {
            }
        }

        bundle.putString(KeyOfArgument.KEY_OF_ID_PATIENT, patient.id.toString())
        bundle.putString(KeyOfArgument.KEY_OF_TEST_NAME, arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME))
        fragment.arguments = bundle
        replaceFragment(fragment, fragmentTag)
    }

    private fun saveTag(tag: String) {
        fragmentTag = tag
        bundle.putString(KeyOfArgument.KEY_OF_FRAGMENT, fragmentTag)
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(tag)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        arguments?.clear()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PatientTestList()
    }
}