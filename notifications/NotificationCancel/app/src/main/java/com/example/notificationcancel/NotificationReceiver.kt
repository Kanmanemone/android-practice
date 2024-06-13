package com.example.notificationcancel

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification 취소
        val notificationId = intent.getIntExtra("notificationId", -1)
        notificationManager.cancel(notificationId)

        // Activity 실행
        var activityToStart: Class<*>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityToStart = intent.getSerializableExtra("activityToStart", Class::class.java)
        } else {
            activityToStart = intent.getSerializableExtra("activityToStart") as Class<*>
        }

        val activityIntent = Intent(context, activityToStart)
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(activityIntent)
    }
}