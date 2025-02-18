package com.friends.ggiriggiri.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.friends.ggiriggiri.R

class ProfileAdapter(private val profileList: List<ProfileItem>) :
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
        holder.imageView.setImageResource(profileItem.imageRes)
        holder.textView.text = profileItem.name
    }

    override fun getItemCount(): Int = profileList.size
}

data class ProfileItem(
    val imageRes: Int, // 이미지 리소스 ID
    val name: String   // 프로필 이름
)