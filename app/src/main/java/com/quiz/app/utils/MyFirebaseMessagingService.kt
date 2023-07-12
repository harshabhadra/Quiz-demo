package com.quiz.app.utils

import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.e("MyFirebaseMessagingService","message: $message")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        NotificationUtil.sendNotification(
            "Test",
            "Quiz Starting",
            notificationManager,
            application.applicationContext
        )
    }
}