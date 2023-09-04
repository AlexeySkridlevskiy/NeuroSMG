package com.example.neurosmg.patientTestList.addPatient

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.MainActivityListener
import com.example.neurosmg.R
import com.example.neurosmg.ToolbarState
import com.example.neurosmg.api.IdController
import com.example.neurosmg.common.State
import com.example.neurosmg.common.showToast
import com.example.neurosmg.databinding.FragmentAddPatientBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddPatient : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[AddPatientViewModel::class.java]
    }
    lateinit var binding: FragmentAddPatientBinding
    private var idNewPatient: Int = -1
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
        binding = FragmentAddPatientBinding.inflate(inflater)
        viewModel.patientAdded.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Error -> {
                    binding.progressBar.isVisible = state.data.isLoading
                    if (state.data.exceptionMessage.isNotEmpty()) {
                        showToast(state.data.exceptionMessage)
                    }
                    if (state.data.showErrorDialog) {
                        infoDialogNotAddPatient()
                    }
                }

                State.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is State.Success -> {
                    binding.progressBar.isVisible = state.data.isLoading
                    if (state.data.showSuccessDialog) {
                        idNewPatient = state.data.addedPatientId
                        infoDialogAddPatient()
                    }
                }

                State.Empty -> {}
            }
        }
        val idController = IdController(requireContext())

        var gender = "Other";
        binding.rbMan.setOnClickListener {
            gender = "Male"
            binding.rbWomen.isChecked = false
        }
        binding.rbWomen.setOnClickListener {
            gender = "Female"
            binding.rbMan.isChecked = false
        }

        var hand = "";
        binding.rbLeft.setOnClickListener {
            hand = "Left"
            binding.rbRight.isChecked = false
        }

        binding.rbRight.setOnClickListener {
            hand = "Right"
            binding.rbLeft.isChecked = false
        }

        binding.etBirthday.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSave.setOnClickListener {
            val birthday = binding.etBirthday.text.toString()
            val comment = binding.etComment.text.toString()
            val patientData = PatientData(
                Birthday = birthday,
                Gender = gender,
                LeadHand = hand,
                Comment = comment,
                user_id_patient = idController.getUserId()
            )

            viewModel.addPatient(patientData)
        }

        binding.btnDelete.setOnClickListener {
            binding.etBirthday.setText("")
            binding.rbMan.isChecked = false
            binding.rbWomen.isChecked = false
            binding.rbLeft.isChecked = false
            binding.rbRight.isChecked = false
            binding.etComment.setText("")
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

    private fun infoDialogAddPatient() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Пациент сохранен!\nID нового пациента $idNewPatient") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("")
        alertDialogBuilder.setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
            viewModel.setEmptyStatus()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun infoDialogNotAddPatient() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.dialog_fail_add_patient))
        alertDialogBuilder.setMessage("")
        alertDialogBuilder.setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
            viewModel.setEmptyStatus()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, monthOfYear)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDate = dateFormat.format(selectedCalendar.time)

                binding.etBirthday.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddPatient()
    }
}