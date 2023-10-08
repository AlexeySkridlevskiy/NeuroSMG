package com.example.neurosmg.archive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neurosmg.R
import com.example.neurosmg.patientTestList.RecyclerAdapter

private const val PATIENT_ID = "patient_id"

class ArchivePatientFragment : Fragment() {

    private var patientId: Int? = null
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patientId = it.getInt(PATIENT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_archive_patient, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        @JvmStatic
        fun newInstance(patientId: Int) =
            ArchivePatientFragment().apply {
                arguments = Bundle().apply {
                    putInt(PATIENT_ID, patientId)
                }
            }
    }
}