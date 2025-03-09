package com.friends.ggiriggiri.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.databinding.RowRequestListBinding
import com.friends.ggiriggiri.ui.memories.requestdetail.RequestDetailFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestListAdapter(
    private var requestList: List<RequestModel>,
    private val parentFragment : Fragment
    ) :
    RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>() {

        inner class RequestViewHolder(private val rowRequestListBinding: RowRequestListBinding) : RecyclerView.ViewHolder(rowRequestListBinding.root) {
            fun bind(item: RequestModel) {
                rowRequestListBinding.tvRequestListName.text = item.requestUserDocumentID
                rowRequestListBinding.tvRequestListSummary.text = item.requestMessage
                rowRequestListBinding.tvRequestListTime.text = formatDate(item.requestTime)

                rowRequestListBinding.root.setOnClickListener{
                    Log.d("RequestListAdapter", "🔥 RequestId 전달: ${item.requestId}")

                    val socialActivity = parentFragment.activity as SocialActivity
                    socialActivity.replaceFragment(RequestDetailFragment.newInstance(item.requestId))
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = RowRequestListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun updateList(newList: List<RequestModel>) {
        requestList = newList
        notifyDataSetChanged()
    }
}