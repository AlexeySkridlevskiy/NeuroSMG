package com.example.neurosmg.patientTestList.patientProfile

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.common.State
import com.example.neurosmg.common.showToast
import com.example.neurosmg.databinding.FragmentPatientProfileBinding
import com.example.neurosmg.doctorProfile.DoctorProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientProfile : Fragment() {
    lateinit var binding: FragmentPatientProfileBinding
    private var mainActivityListener: MainActivityListener? = null
    private var patientResponce: PatientResponse? = null
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[PatientProfileViewModel::class.java]
    }
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
        binding = FragmentPatientProfileBinding.inflate(inflater)
        binding.btnDeleteProf.setOnClickListener {
            binding.etBirthday.setText("")
            binding.rbProfMan.isChecked = false
            binding.rbProfWomen.isChecked = false
            binding.rbProfRight.isChecked = false
            binding.rbProfLeft.isChecked = false
            binding.etProfComment.setText("")
        }
        val id = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT)
        binding.tvIdPatient.text = id.toString()
        if (id != null) {
           patientResponce = viewModel.getPatientById(id)
        }

        var gender = ""
        gender = if(binding.rbProfMan.isChecked){
            "Male"
        }else{
            "Female"
        }
        binding.rbProfMan.setOnClickListener {
            gender = "Male"
            binding.rbProfWomen.isChecked = false
        }
        binding.rbProfWomen.setOnClickListener {
            gender = "Female"
            binding.rbProfMan.isChecked = false
        }

        var hand=""
        hand = if(binding.rbProfRight.isChecked){
            "Right"
        }else{
            "Left"
        }

        binding.rbProfLeft.setOnClickListener {
            hand = "Left"
            binding.rbProfRight.isChecked = false
        }

        binding.rbProfRight.setOnClickListener {
            hand = "Right"
            binding.rbProfLeft.isChecked = false
        }
        binding.btnSaveProf.setOnClickListener {
            val birthday = binding.etBirthday.text.toString()
            val comment = binding.etProfComment.text.toString()
            gender = if(binding.rbProfMan.isChecked){
                "Male"
            }else{
                "Female"
            }
            hand = if(binding.rbProfRight.isChecked){
                "Right"
            }else{
                "Left"
            }
            if (id != null) {
                val updatedData = UpdatePatientRequest(
                    data = UpdatePatientData(
                        birthday = birthday,
                        gender = gender,
                        leadHand = hand,
                        comment = comment
                    )
                )
                viewModel.updatePatientData(updatedData, id)
            }
        }

        binding.etBirthday.setOnClickListener {
            showDatePickerDialog()
        }

        viewModel.patientData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Error -> {
                    binding.progressBar.isVisible = false
                    if (state.data.errorMessage != null) {
//                        showToast(state.data.errorMessage)
                        showToast("Введите данные пациента!")
                    }
                }

                State.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is State.Success -> {
                    binding.progressBar.isVisible = false
                    if (state.data.birthday != null) {
                        binding.etBirthday.setText(state.data.birthday)
                    }
                    if (state.data.gender != null) {
                        if(state.data.gender == "Male"){
                            binding.rbProfMan.isChecked = true
                            binding.rbProfWomen.isChecked = false
                        }else{
                            binding.rbProfWomen.isChecked = true
                            binding.rbProfMan.isChecked = false
                        }
                    }
                    if (state.data.leadHand != null) {
                        if(state.data.leadHand == "Right"){
                            binding.rbProfRight.isChecked = true
                            binding.rbProfLeft.isChecked = false
                        }else{
                            binding.rbProfLeft.isChecked = true
                            binding.rbProfRight.isChecked = false
                        }
                    }
                    if (state.data.comment != null) {
                        binding.etProfComment.setText(state.data.comment)
                    }
                }

                State.Empty -> {}
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
        fun newInstance() = PatientProfile()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = formatDate(year, monthOfYear + 1, dayOfMonth)
                binding.etBirthday.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day) // Месяцы начинаются с 0, поэтому вычитаем 1
        return dateFormat.format(calendar.time)
    }
}