package com.alexz.messenger.app

import android.app.Application
import com.alexz.messenger.app.util.FirebaseUtil
import java.util.*

class ChatApplication : Application() {
    private var online = false
    private val REFRESH_RATE = 500L
    private val timer  = Timer()
    var isRunning :Boolean = false
    set(value) {
        field = value
        if (isRunning && !online) {
            FirebaseUtil.setOnline(true)
            online = true
        }}

    override fun onCreate() {
        super.onCreate()
        online = true
        FirebaseUtil.setOnline(true)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!isRunning && online) {
                    FirebaseUtil.setOnline(false)
                    online = false
                }
            }
        }, 0, REFRESH_RATE)
    }

    override fun onTerminate() {
        timer.cancel()
        FirebaseUtil.setOnline(false)
        super.onTerminate()
    }
}