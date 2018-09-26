package xyz.keyurved.processwatcher.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.keyurved.processwatcher.R
import xyz.keyurved.processwatcher.entities.Message
import java.time.format.DateTimeFormatter

class MessageViewAdapter(private val messageDataset: ArrayList<Message>):
        RecyclerView.Adapter<MessageViewAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.message_card_item_view, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun getItemCount() = messageDataset.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.setItem(messageDataset[position])
    }


    class MessageViewHolder(parent: View): RecyclerView.ViewHolder(parent) {
        //private var message: Message? = null
        private var messageTextView: TextView = parent.findViewById(R.id.card_message_textView)
        private var processTextView: TextView = parent.findViewById(R.id.card_title_textView)
        private var timestampTextView: TextView = parent.findViewById(R.id.card_timestamp_textView)

        fun setItem(item: Message) {
            messageTextView.text = item.message
            processTextView.text = item.processName
            timestampTextView.text = item.arrivalTime.format(DateTimeFormatter.ofPattern("hh:mm:ss MM-dd-yyyy"))
        }
    }
}