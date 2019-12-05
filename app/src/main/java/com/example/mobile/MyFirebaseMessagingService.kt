package com.example.mobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification?.body != null) {
            var body = remoteMessage.notification!!.body
            sendNotification(body!!)
        }
        Log.e("mynoti", "message : " + remoteMessage.notification?.body)

    }

    override fun onNewToken(p0: String) {
        sendRegistrationToServer(p0)
        Log.e("mynoti", "new token :$p0")
    }

    private fun sendNotification(messageBody: String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alert")
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    fun sendRegistrationToServer(token: String) {
        var data = { "token" to token }
        db.collection("user").document(auth.currentUser!!.uid).set(data, SetOptions.merge()).addOnSuccessListener {
            Log.e("mynoti", "updated token")
        }.addOnFailureListener { e ->
            Log.e("mynoti", e.toString())
        }
    }
}