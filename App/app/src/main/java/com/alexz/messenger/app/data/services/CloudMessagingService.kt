package com.alexz.messenger.app.data.services

import android.annotation.SuppressLint
import com.alexz.messenger.app.data.providers.imp.DaggerPhoneAuthProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerProfileProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.AuthProvider
import com.alexz.messenger.app.data.providers.interfaces.PhoneAuthCallback
import com.alexz.messenger.app.data.providers.interfaces.ProfileProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessagingService : FirebaseMessagingService(){

    private val profileProvider : ProfileProvider by lazy {
        DaggerProfileProviderComponent.create().getProfileProvider()
    }
    private val authProvider : AuthProvider by lazy {
        DaggerPhoneAuthProviderComponent.builder().setCallback(object  : PhoneAuthCallback{})
                .build().getPhoneAuthProvider()
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
            profileProvider.setNotificationToken(token)
        } else {
            authProvider.doOnAuthenticated(Runnable {
                profileProvider.setNotificationToken(token).subscribe({}, {})
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