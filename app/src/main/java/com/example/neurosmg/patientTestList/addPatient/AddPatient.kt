package com.example.neurosmg.patientTestList.addPatient

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.Screen
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.api.IdController
import com.example.neurosmg.common.State
import com.example.neurosmg.databinding.FragmentAddPatientBinding
import com.example.neurosmg.patientTestList.PatientTestList
import com.example.neurosmg.testsPage.TestsPage

class AddPatient : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[AddPatientViewModel::class.java]
    }
    lateinit var binding: FragmentAddPatientBinding

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
        binding = FragmentAddPatientBinding.inflate(inflater)
        viewModel.patientAdded.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.Error -> {
                    binding.progressBar.isVisible = false
                }

                State.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is State.Success -> {
                    binding.progressBar.isVisible = false
                }
            }
        }
        val idController = IdController(requireContext())

        var gender = "Other";
        binding.rbMan.setOnClickListener {
            gender = "Male"
        }
        binding.rbWomen.setOnClickListener {
            gender = "Female"
        }

        var hand = "";
        binding.rbLeft.setOnClickListener {
            hand = "Left"
        }

        binding.rbRight.setOnClickListener {
            hand = "Right"
        }

        binding.btnSave.setOnClickListener {
            val birthday = binding.etBirthday.text.toString() // Получаем текст из EditText
            val comment = binding.etComment.text.toString()
            val patientData = PatientData(
                Birthday = birthday,
                Gender = gender,
                LeadHand = hand,
                Comment = comment,
                user_id_patient = idController.getUserId()
            )
            Log.d("MyLog", "$patientData")
            if (viewModel.isPatientAdd(patientData)){
                infoDialogAddPatient()
            }else{
                infoDialogNotAddPatient()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityListener?.updateToolbarState(ToolbarState.PatientProfile)
    }
    override fun onDetach() {
        super.onDetach()
        mainActivityListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddPatient()
    }

    private fun infoDialogAddPatient() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Пациент сохранен!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, PatientTestList.newInstance())
                .addToBackStack(Screen.TESTS_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogNotAddPatient() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Пациент не создан! Ошибка!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            dialog.dismiss()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, PatientTestList.newInstance())
                .addToBackStack(Screen.TESTS_PAGE)
                .commit()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }
}