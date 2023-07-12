package com.quiz.app.ui.viewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R
import io.agora.chat.ChatMessage
import io.agora.chat.TextMessageBody


class ChatMessageAdapter() : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    private val chatMessages: MutableList<ChatMessage> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.message_list_item, parent, false)
        return ChatMessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount() = chatMessages.size

    fun submitList(list: List<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(list)
        notifyDataSetChanged()
    }

    inner class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.sender_text_view)
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_view)

        fun bind(chatMessage: ChatMessage) {
            senderTextView.text = "${chatMessage.from}: "
            if (chatMessage.type == ChatMessage.Type.TXT) {
                messageTextView.text = (chatMessage.body as TextMessageBody).message
            }
        }
    }
}
