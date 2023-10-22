package com.example.neurosmg.patientTestList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.archive.FileTest
import com.example.neurosmg.databinding.ListItemBinding

class RecyclerAdapter<T> : RecyclerView.Adapter<RecyclerAdapter<T>.ViewHolder>() {

    private val itemList = mutableListOf<T>()
    var onItemClick: OnItemClickListener<T>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemBinding.bind(itemView)

        fun bind(item: T) {
            with(binding) {
                when (item) {
                    is FileTest -> tvTitle.text = item.name
                    else -> tvTitle.text = item.toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])

        val item = itemList[position]
        holder.itemView.setOnClickListener {
            onItemClick?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(items: List<T>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    interface OnItemClickListener<T> {
        fun onItemClick(item: T)
    }
}