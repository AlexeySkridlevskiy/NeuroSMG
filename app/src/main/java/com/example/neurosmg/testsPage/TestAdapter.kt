package com.example.neurosmg.testsPage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.ListItemBinding

class TestAdapter(
    private val itemOnClickListener: ItemOnClickListener
) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    private val testsList = mutableListOf<TestItem>()
    var onItemClick: ((TestItem) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
        fun bind(testItem: TestItem) = with(binding) {
            tvTitle.text = testItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(testsList[position])

        val tests = testsList[position]
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tests)
            itemOnClickListener.onItemClick(testsList[position])
        }
    }

    override fun getItemCount(): Int {
        return testsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTest(testItem: List<TestItem>) {
        testsList.clear()
        testsList.addAll(testItem)
        notifyDataSetChanged()
    }
}
