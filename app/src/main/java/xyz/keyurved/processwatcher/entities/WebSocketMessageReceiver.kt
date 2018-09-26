package xyz.keyurved.processwatcher.entities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.keyurved.processwatcher.activities.MainActivity

class WebSocketMessageReceiver(private val activity: Activity): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getStringExtra(WebSocketService.EXTENDED_DATA_STATUS)
        var message: Message? = null

        if (status == WebSocketService.MESSAGE) {
           message = intent.getParcelableExtra(WebSocketService.MESSAGE)
        }

        if (activity is MainActivity) {
            activity.updateUI(status, message)
        }
    }
}