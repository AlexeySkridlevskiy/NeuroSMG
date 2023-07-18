package com.example.neurosmg.patientTestList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.ListItemBinding
import com.example.neurosmg.testsPage.TestItem

class PatientAdapter : RecyclerView.Adapter<PatientAdapter.ViewHolder>() {

    private val patientList = ArrayList<Patient>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
        fun bind(patient: Patient) = with(binding){
            tvTitle.text = patient.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PatientAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientAdapter.ViewHolder, position: Int) {
        holder.bind(patientList[position])
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addPatient(patient: Patient){
        patientList.add(patient)
        notifyDataSetChanged()
    }
}