package com.alexz.messenger.app.data.services

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessagingService : FirebaseMessagingService(){

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        msg.notification?.let {
            sendNotification("qwe")
        }

    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(User().id)
                .child("token")
                .setValue(token)

    }

    private fun sendNotification(messageBody: String) {
//        val intent = ChatActivity.getIntent(this, Chat())
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
//        val channelId = "1"
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.logo256)
//                .setContentTitle("Title")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}