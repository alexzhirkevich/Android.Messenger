package com.community.messenger.app.data.services

import android.annotation.SuppressLint
import com.community.messenger.core.providers.components.DaggerAuthProviderComponent
import com.community.messenger.core.providers.components.DaggerUserProfileProviderComponent
import com.community.messenger.core.providers.interfaces.AuthProvider
import com.community.messenger.core.providers.interfaces.UserProfileProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessagingService : FirebaseMessagingService(){

    private val userProfileProvider : UserProfileProvider by lazy {
        DaggerUserProfileProviderComponent.create().getProvider()
    }
    private val authProvider : AuthProvider by lazy {
        DaggerAuthProviderComponent.create().getProvider()
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        msg.notification?.let {
            sendNotification("qwe")
        }

    }
    @SuppressLint("CheckResult")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (authProvider.isAuthenticated) {
            authProvider.setNotificationToken(token)
        } else {
            authProvider.doOnAuthenticated(Runnable {
                authProvider.setNotificationToken(token).subscribe({}, {})
            })
        }
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