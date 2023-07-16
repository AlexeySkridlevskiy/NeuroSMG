package com.example.neurosmg.testsPage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.ListItemBinding

class TestAdapter: RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    private val testsList = ArrayList<TestItem>()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
        fun bind(testItem: TestItem) = with(binding){
            tvTitle.text = testItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(testsList[position])
    }

    override fun getItemCount(): Int {
        return testsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTest(testItem: TestItem){
        testsList.add(testItem)
        notifyDataSetChanged()
    }
}
