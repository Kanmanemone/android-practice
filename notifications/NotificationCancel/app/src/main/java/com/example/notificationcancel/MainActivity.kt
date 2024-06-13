package com.example.notificationcancel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManager? = null
    private val channelId = "com.example.notificationcancel.channel1"
    private val myRequestCode1 = 12345
    private val myRequestCode2 = 12346
    private val myRequestCode3 = 12347
    private val notificationId = 777 // 알림 채널 Id와는 또 다른 알림 하나하나의 Id. 동일한 Id의 알림이 상태바에 있을 때, 같은 Id인 알림을 발생시키면, 원래 있던 알림이 최신 알림으로 갈아끼워진다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelId, "My Channel", "It's my channel")

        button.setOnClickListener {
            displayNotification()
        }
    }

    private fun displayNotification() {
        val tabBehavior = getSecondActivityPendingIntent()
        val actionButton1 = getThirdActivityAction()
        val actionButton2 = getFourthActivityAction()

        val notification = NotificationCompat.Builder(this@MainActivity, channelId).apply {
            setContentTitle("My Notification Title") // 상태바 내리면 화면에 보일 알림 제목
            setContentText("It's my notification") // 상태바 내리면 화면에 보일 알림 본문
            setSmallIcon(android.R.drawable.ic_dialog_info) // 알림 아이콘 설정
            setAutoCancel(true) // 상태바에서 알림 클릭 시, 알림이 사라지게 만드는 옵션 (하지만, PendingIntent가 없으면 옵션 적용 안됨)
            setPriority(NotificationCompat.PRIORITY_HIGH) // API 26 미만을 위한 알림 중요도 옵션. API 26 이상부터는 NotificationChannel에 할당한 알림 중요도가 우선시된다.
            setContentIntent(tabBehavior)
            addAction(actionButton1)
            addAction(actionButton2)
        }.build()

        notificationManager?.notify(notificationId, notification)
    }

    private fun getSecondActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, SecondActivity::class.java)
        return PendingIntent.getActivity(
            this, myRequestCode1, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getThirdActivityAction(): NotificationCompat.Action {
        val intent = Intent(this, NotificationReceiver::class.java)
            .putExtra("notificationId", notificationId)
            .putExtra("activityToStart", ThirdActivity::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this, myRequestCode2, intent, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, "Go Third", pendingIntent).build()
    }

    private fun getFourthActivityAction(): NotificationCompat.Action {
        val intent = Intent(this, NotificationReceiver::class.java)
            .putExtra("notificationId", notificationId)
            .putExtra("activityToStart", FourthActivity::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this, myRequestCode3, intent, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, "Go Fourth", pendingIntent).build()
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 숫자 0이 아니라 Oreo의 첫 글자인 알파벳 대문자 O다.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}