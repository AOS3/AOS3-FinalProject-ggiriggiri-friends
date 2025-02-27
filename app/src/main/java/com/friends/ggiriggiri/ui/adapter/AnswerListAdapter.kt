package com.friends.ggiriggiri.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.data.model.QuestionAnswerModel
import com.friends.ggiriggiri.databinding.RowQuestionAnswerBinding

class AnswerListAdapter(
    private var answerList : List<QuestionAnswerModel>
) : RecyclerView.Adapter<AnswerListAdapter.AnswerViewHolder>(){

    inner class AnswerViewHolder(private val binding: RowQuestionAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionAnswerModel) {
            binding.tvRowQuestionAnswerUserName.text = item.userName
            binding.tvRowQuestionAnswerComment.text = item.answerMessage

            Glide.with(binding.root)
                .load(item.userProfileImage)
                .placeholder(R.drawable.ic_default_profile)
                .circleCrop()
                .into(binding.ivRowQuestionAnswerProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding =
            RowQuestionAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(answerList[position])
    }

    fun updateList(newList: List<QuestionAnswerModel>) {
        answerList = newList
        notifyDataSetChanged()
    }
}