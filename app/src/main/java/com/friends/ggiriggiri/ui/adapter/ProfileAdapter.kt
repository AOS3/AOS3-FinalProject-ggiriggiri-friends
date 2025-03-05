package com.friends.ggiriggiri.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.R

class ProfileAdapter(private var profileList: List<ProfileItem>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivRowProfile)
        val textView: TextView = itemView.findViewById(R.id.tvRowName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_profile_bottom_sheet, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profileItem = profileList[position]

        if (profileItem.imageRes.isNullOrEmpty()) {
            holder.imageView.setImageResource(R.drawable.ic_default_profile)
        } else {
            Glide.with(holder.itemView.context)
                .load(profileItem.imageRes)
                .placeholder(R.drawable.ic_default_profile) // 이미지 로딩 중 기본 아이콘
                .error(R.drawable.ic_default_profile) // 오류 발생 시 기본 아이콘
                .into(holder.imageView)
        }

        holder.textView.text = profileItem.name
    }

    override fun getItemCount(): Int = profileList.size

    // 🔥 RecyclerView 업데이트 로그 추가
    fun updateData(newList: List<ProfileItem>) {
        profileList = newList
        notifyDataSetChanged() // 변경 사항 반영
    }
}

data class ProfileItem(
    val imageRes: String, // 이미지 URL
    val name: String   // 프로필 이름
)