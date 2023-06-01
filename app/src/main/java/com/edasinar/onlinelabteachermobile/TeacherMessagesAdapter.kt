package com.edasinar.onlinelabteachermobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edasinar.model.MessageInfo
import com.edasinar.model.TeacherMessageInfo
import com.edasinar.onlinelabteachermobile.databinding.CardLayoutBinding

class TeacherMessagesAdapter(private val messageList: ArrayList<MessageInfo>):
    RecyclerView.Adapter<TeacherMessagesAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val rowBinding: CardLayoutBinding =
            CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.binding.titleName.text = messageList[position].messageLabel

        val clickListener = View.OnClickListener {
            val intent = Intent(holder.itemView.context, TeacherGetMessageActivity::class.java)
            intent.putExtra("message",messageList[position])
            holder.itemView.context.startActivity(intent)
        }
        holder.binding.mainCardView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(binding: CardLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: CardLayoutBinding

        init {
            this.binding = binding
        }
    }
}