package com.example.neurosmg.patientTestList

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
import com.example.neurosmg.Tests.CBT.CBTTest
import com.example.neurosmg.Tests.FOT.FOTTest
import com.example.neurosmg.Tests.GNG.GNGTest
import com.example.neurosmg.Tests.IAT.IATTest
import com.example.neurosmg.Tests.MRT.MRTTest
import com.example.neurosmg.Tests.RAT.RATTest
import com.example.neurosmg.Tests.SCT.SCTTest
import com.example.neurosmg.Tests.TMT.TMTTest
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.databinding.FragmentPatientTestListBinding
import com.example.neurosmg.patientTestList.addPatient.AddPatient
import com.example.neurosmg.patientTestList.patientProfile.PatientProfile

class PatientTestList : Fragment(), PatientOnClickListener {

    lateinit var binding: FragmentPatientTestListBinding
    private val bundle = Bundle()
    private lateinit var fragment: Fragment
    private val adapter = PatientAdapter(this)
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[PatientsViewModel::class.java]
    }

    private var mainActivityListener: MainActivityListener? = null

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
        adapter.addPatient(patient = viewModel.getListOfPatient())
        rcView.adapter = adapter
        if(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_PATIENT) == true){
            flButton.visibility = View.VISIBLE
        }
        flButton.setOnClickListener {
            fragment = AddPatient.newInstance()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.loginFragment, fragment)
                .addToBackStack(Screen.MAIN_PAGE)
                .commit()
        }
        if(arguments?.getBoolean(KeyOfArgument.KEY_OF_MAIN_TO_ARCHIVE) == true){
            tvArchive.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    }
                    "RAT" -> {
                        fragment = RATTest.newInstance()
                    }
                    "IAT" -> {
                        fragment = IATTest.newInstance()
                    }
                    "GNG" -> {
                        fragment = GNGTest.newInstance()
                    }
                    "SCT" -> {
                        fragment = SCTTest.newInstance()
                    }
                    "TMT" -> {
                        fragment = TMTTest.newInstance()
                    }
                    "CBT" -> {
                        fragment = CBTTest.newInstance()
                    }
                    "MRT" -> {
                        fragment = MRTTest.newInstance()
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

        bundle.putString(KeyOfArgument.KEY_OF_ID_PATIENT, patient.id)
        bundle.putString(KeyOfArgument.KEY_OF_TEST_NAME, arguments?.getString(KeyOfArgument.KEY_OF_TEST_NAME))
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.loginFragment, fragment)
            .addToBackStack(Screen.MAIN_PAGE)
            .commit()
    }

    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PatientTestList()
    }
}