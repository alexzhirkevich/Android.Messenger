package com.alexz.messenger.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.converters.GsonTypeConverter
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.imp.MediaContent
import com.alexz.messenger.app.util.FirebaseUtil
import java.util.*

class ChatApplication : Application() {

    var isRunning :Boolean = false
        set(value) {
            field = value
            if (value && !isOnline) {
                FirebaseUtil.setOnline(true)
                isOnline = true
            }}

    private var isOnline = false
    private val refreshRate = 500L
    private val timer = Timer()

    override fun onCreate() {
        super.onCreate()
        AppContext = applicationContext
        LocalDatabase.INSTANCE = Room.databaseBuilder(applicationContext, LocalDatabase::class.java, "local_database")
                .addTypeConverter(GsonTypeConverter<Map<String, ChannelAdmin>>())
                .addTypeConverter(GsonTypeConverter<List<MediaContent>>())
                .build()
        isOnline = true
        FirebaseUtil.setOnline(true)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!isRunning && isOnline) {
                    FirebaseUtil.setOnline(false)
                    isOnline = false
                }
            }
        }, 0, refreshRate)
    }

    override fun onTerminate() {
        timer.cancel()
        FirebaseUtil.setOnline(false)
        super.onTerminate()
    }


    companion object{
        lateinit var AppContext : Context
        private set
    }
}