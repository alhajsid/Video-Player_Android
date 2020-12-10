package com.example.alhaj.mediaplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App: Application(){

    val CHANNEL_ID="examplrchannel"
    override fun onCreate() {
        super.onCreate()
        createnotificationchannel()
    }

    fun createnotificationchannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "EXAMPLE CHANNEL",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager=getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}