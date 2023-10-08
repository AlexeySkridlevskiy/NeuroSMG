package com.example.neurosmg.patientTestList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.ListItemBinding
import com.example.neurosmg.patientTestList.entity.Patient

class RecyclerAdapter() : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    private val patientList = mutableListOf<Patient>()

    var onPatientItemClick: OnPatientClickListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemBinding.bind(itemView)
        fun bind(patient: Patient) = with(binding) {
            tvTitle.text = patient.id.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(patientList[position])

        val patient = patientList[position]
        holder.itemView.setOnClickListener {
            onPatientItemClick?.onPatientIdClick(patient.id)
        }
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addPatient(patient: List<Patient>) {
        patientList.clear()
        patientList.addAll(patient)
        notifyDataSetChanged()
    }

    interface OnPatientClickListener {
        fun onPatientIdClick(patient: Int)
    }
}