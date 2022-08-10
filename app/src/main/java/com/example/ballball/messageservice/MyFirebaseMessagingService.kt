package com.example.ballball.messageservice

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ballball.main.MainActivity
import com.example.ballball.utils.Animation
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: NotificationCompat.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (remoteMessage.data.isNotEmpty()) {
            showNotification(remoteMessage.data["title"], remoteMessage.data["content"])
        }

        remoteMessage.notification?.let {
            Log.d("Notifications Handle", it.body.toString())
        }
    }

    private fun showNotification(title: String?, content: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.sym_def_app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.sym_def_app_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        } else {
            builder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.sym_def_app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.sym_def_app_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }
        notificationManager.notify(0, builder.build())
    }
}