package xyz.keyurved.processwatcher.entities

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException


class WebSocketService: IntentService("WebSocketService") {
    private var mWebSocketClient: WebSocketClient? = null

    companion object {
       const val JOB_ID = 1000
        const val BROADCAST_ACTION = "xyz.keyurved.processwatcher.BROADCAST"
        const val EXTENDED_DATA_STATUS = "xyz.keyurved.processwatcher.STATUS"
        const val HOSTNAME = "xyz.keyurved.processwatcher.HOSTNAME"
        const val PORT = "xyz.keyurved.processwatcher.PORT"
        const val SOCKET_OPEN = "WEBSOCKETOPEN"
        const val SOCKET_CLOSED  = "WEBSOCKETCLOSED"
        const val MESSAGE = "MESSAGE"

    }

    override fun onHandleIntent(intent: Intent) {
        val hostname = intent.getStringExtra(HOSTNAME)
        val port = intent.getStringExtra(PORT)
        val context = this
        val uri: URI?

        try {
            uri = if (port != null) {
                URI("ws://$hostname:$port/")
            } else {
                URI("ws://$hostname/")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid hostname or port", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i("uri", uri.toString())

        if (mWebSocketClient == null) {
            mWebSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {

                    val localIntent = Intent(BROADCAST_ACTION).apply {
                        putExtra(EXTENDED_DATA_STATUS, SOCKET_OPEN)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
                    Log.i("Websocket", "Opened")
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.i("Websocket", "Closed $reason")

                    val localIntent = Intent(BROADCAST_ACTION).apply {
                        putExtra(EXTENDED_DATA_STATUS, SOCKET_CLOSED)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
                }

                override fun onMessage(message: String?) {
                    //      Log.i("Websocket", "message")
                    val newMessage = Message.fromJSON(JSONObject(message))

                    val localIntent = Intent(BROADCAST_ACTION).apply {
                        putExtra(EXTENDED_DATA_STATUS, MESSAGE)
                        putExtra(MESSAGE, newMessage)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)


                }

                override fun onError(ex: Exception?) {
                    val localIntent = Intent(BROADCAST_ACTION).apply {
                        putExtra(EXTENDED_DATA_STATUS, SOCKET_CLOSED)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
                }

            }

            mWebSocketClient!!.connect()
        }

    }




}