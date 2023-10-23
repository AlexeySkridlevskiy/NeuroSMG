package com.example.neurosmg.questionnaires

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.neurosmg.R
import com.example.neurosmg.databinding.ListItemBinding

class QuestionnaireAdapter(
    private val itemOnClickListener: ItemOnClickListener
) : RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder>() {

    private val questionnaireList = mutableListOf<QuestionnaireItem>()
    var onItemClick: ((QuestionnaireItem) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
        fun bind(questionnaireItem: QuestionnaireItem) = with(binding) {
            tvTitle.text = questionnaireItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questionnaireList[position])

        val questionnaires = questionnaireList[position]
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(questionnaires)
            itemOnClickListener.onItemClick(questionnaireList[position])
        }
    }

    override fun getItemCount(): Int {
        return questionnaireList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTest(questionnaireItem: List<QuestionnaireItem>) {
        questionnaireList.clear()
        questionnaireList.addAll(questionnaireItem)
        notifyDataSetChanged()
    }
}
