package xyz.keyurved.processwatcher.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import xyz.keyurved.processwatcher.R
import xyz.keyurved.processwatcher.adapters.MessageViewAdapter
import xyz.keyurved.processwatcher.entities.Message
import xyz.keyurved.processwatcher.entities.WebSocketMessageReceiver
import xyz.keyurved.processwatcher.entities.WebSocketService
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val messageDataset: ArrayList<Message> = ArrayList()
    private var serviceStarted: Boolean = false
    private var notificationManager: NotificationManagerCompat? = null
    private var notificationId = 0

    companion object {
        const val DEFAULT_CHANNEL_ID = "DEFAULT_CHANNEL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        notificationManager = NotificationManagerCompat.from(this)


        viewManager = LinearLayoutManager(this)
        viewAdapter = MessageViewAdapter(messageDataset = messageDataset)

        messageRecyclerView = findViewById<RecyclerView>(R.id.message_recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val statusIntentFilter = IntentFilter(WebSocketService.BROADCAST_ACTION)
        val webSocketMessageReceiver = WebSocketMessageReceiver(this)

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(webSocketMessageReceiver, statusIntentFilter)

        open_connection_button.setOnClickListener {
            startWebSocketService()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DEFAULT_CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    private fun startWebSocketService() {
        if (!serviceStarted) {
            serviceStarted = true
            var port: String? = null
            if (port_editText.text.toString() != "") {
                port = port_editText.text.toString()
            }
            val intentService = Intent(this, WebSocketService::class.java).apply {
                putExtra(WebSocketService.HOSTNAME, ip_editText.text.toString())
                putExtra(WebSocketService.PORT, port)
            }
            startService(intentService)
        }
    }

    private fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (notificationManager != null) {
            notificationManager!!.notify(notificationId++, builder.build())
        }
    }

    fun updateUI(status: String, message: Message?) {
        if (status == WebSocketService.SOCKET_OPEN) {
            connected_textView.text =  getString(R.string.active)
            connected_textView.setTextColor(getColor(R.color.green))
        } else if (status == WebSocketService.SOCKET_CLOSED) {
            serviceStarted = false
            connected_textView.text =  getString(R.string.inactive)
            connected_textView.setTextColor(getColor(R.color.red))
        } else if (status == WebSocketService.SOCKET_ERROR) {
            serviceStarted = false
            connected_textView.text =  getString(R.string.error)
            connected_textView.setTextColor(getColor(R.color.red))
        } else if (status == WebSocketService.MESSAGE && message != null) {
            if (message.messageType == Message.MessageType.EXIT) {
                showNotification(message.processName, message.message)
            }
            messageDataset.add(0, message)
            viewAdapter.notifyDataSetChanged()
        }

    }

}
