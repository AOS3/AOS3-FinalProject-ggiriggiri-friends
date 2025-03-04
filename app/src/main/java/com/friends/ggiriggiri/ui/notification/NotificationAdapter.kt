package com.friends.ggiriggiri.ui.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter : ListAdapter<NotificationEntity, NotificationAdapter.NotificationViewHolder>(DIFF_CALLBACK) {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationEntity) {
            binding.tvTitle.text = notification.title
            binding.tvMessage.text = notification.message
            binding.tvTimestamp.text = formatTimestamp(notification.timestamp)

            // 오늘 날짜인지 확인하여 배경색 변경
            if (isToday(notification.timestamp)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.mainColor))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificationEntity>() {
            override fun areItemsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    // 타임스탬프를 "yyyy-MM-dd HH:mm" 형식으로 변환 (초 제외)
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return format.format(date)
    }

    // 주어진 timestamp가 오늘 날짜인지 확인하는 함수
    private fun isToday(timestamp: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 현재 날짜
        val today = dateFormat.format(Date(System.currentTimeMillis()))
        // 주어진 timestamp 날짜
        val targetDate = dateFormat.format(Date(timestamp))

        return today == targetDate
    }
}
