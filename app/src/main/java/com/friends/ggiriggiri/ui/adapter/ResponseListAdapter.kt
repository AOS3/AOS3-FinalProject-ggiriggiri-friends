package com.friends.ggiriggiri.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.data.model.ResponseModel
import com.friends.ggiriggiri.databinding.RowRequestDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResponseListAdapter(var responseList: List<ResponseModel>, private var userProfileImageMap: Map<String, String>) :
    RecyclerView.Adapter<ResponseListAdapter.ResponseViewHolder>() {

    inner class ResponseViewHolder(private val rowRequestDetailBinding: RowRequestDetailBinding) :
        RecyclerView.ViewHolder(rowRequestDetailBinding.root) {
        fun bind(item: ResponseModel) {
            Log.d("ResponseListAdapter", "🔥 Binding item: ${item.responseUserDocumentID}, message: ${item.responseMessage}")
            rowRequestDetailBinding.tvResponseUserName.text = item.responseUserDocumentID
            rowRequestDetailBinding.tvResponseRequestTime.text = formatDate(item.responseTime)
            rowRequestDetailBinding.tvResponseMessage.text = item.responseMessage

            Glide.with(rowRequestDetailBinding.root)
                .load(userProfileImageMap[item.responseUserDocumentID] ?: item.responseUserProfileImage)
                .placeholder(R.drawable.ic_default_profile)
                .circleCrop()
                .into(rowRequestDetailBinding.ivResponseProfile)

            Glide.with(rowRequestDetailBinding.root)
                .load(item.responseImage.ifEmpty { null })
                .placeholder(R.drawable.background_response)
                .into(rowRequestDetailBinding.ivResponseMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val binding = RowRequestDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResponseViewHolder(binding)
    }

    override fun getItemCount(): Int = responseList.size

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        holder.bind(responseList[position])
    }

    fun updateList(newList: List<ResponseModel>, newProfileImageMap: Map<String, String>) {
        responseList = newList
        userProfileImageMap = newProfileImageMap
        Log.d("ResponseListAdapter", "🔥 updateList() called, new size: ${responseList.size}")
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun updateProfileImages(newProfileImageMap: Map<String, String>) {
        userProfileImageMap = newProfileImageMap
        notifyDataSetChanged()
    }
}