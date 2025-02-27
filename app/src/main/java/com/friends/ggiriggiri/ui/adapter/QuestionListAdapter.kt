package com.friends.ggiriggiri.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.friends.ggiriggiri.data.model.QuestionListItemModel
import com.friends.ggiriggiri.databinding.RowQuestionListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionListAdapter(
    private var questionList : List<QuestionListItemModel>,
    private val parentFragment: Fragment,
    private val onItemClick: (QuestionListItemModel) -> Unit
) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>(){

    inner class QuestionViewHolder(private val binding: RowQuestionListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionListItemModel) {
            binding.tvRowQuestionNumber.text = "#${item.number}"
            binding.tvRowQuestionTitle.text = item.content
            binding.tvRowQuestionDate.text = formatDate(item.questionCreateTime)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding =
            RowQuestionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionList[position])
    }

    fun updateList(newList: List<QuestionListItemModel>) {
        questionList = newList
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "N/A" // ✅ 0L일 경우 "N/A" 표시
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}