package com.example.alhaj.mediaplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alhaj.mediaplayer.Adaptors.SongLIstAdaptor
import com.example.alhaj.mediaplayer.BroadcastReciever.*
import com.example.alhaj.mediaplayer.fragments.SongListFragment
import java.util.*
import kotlin.collections.ArrayList


class MyService : Service() {

    var songname: String = ""

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        pakageName = this.packageName
        context = this
        try {
            songname = intent!!.getStringExtra("songname").toLowerCase(Locale.getDefault())
        } catch (e: Exception) {
            Log.e("erroe", e.toString())
        }
        init()

        return START_NOT_STICKY
    }


    private fun init() {
        if (songname == "pause") {
            mediaPlayer.pause()
            refresh()
            return
        } else if (songname == "play") {
            mediaPlayer.start()
            refresh()
            return
        } else if (songname == "next") {
            next()
            refresh()
            return
        } else if (songname == "back") {
            back()
            refresh()
            return
        } else if (songname.length > 1) {
            getAllAudioFromDevice(this, 1)
            isServiceRunning = true
            startForgroundmService()
            return
        } else {
            Log.e("my service", "get all songs")
            getAllAudioFromDevice(context!!, 0)
        }
        isServiceRunning = true
        startForgroundmService()

    }


    fun startForgroundmService() {
        val collapseview = RemoteViews(packageName, R.layout.notification_layout)

        val clickintent = Intent(applicationContext, NotificationReciever::class.java)
        val pi = PendingIntent.getBroadcast(this, 0, clickintent, 0)

        val clickintent1 = Intent(applicationContext, NotificationRecieverBack::class.java)
        val pi1 = PendingIntent.getBroadcast(this, 0, clickintent1, 0)

        val clickintent2 = Intent(applicationContext, NotificationRecieverNext::class.java)
        val pi2 = PendingIntent.getBroadcast(this, 0, clickintent2, 0)

        val clickintent3 = Intent(applicationContext, NotificationRecieverMain::class.java)
        val pi3 = PendingIntent.getBroadcast(this, 0, clickintent3, 0)

        val clickintent4 = Intent(applicationContext, NotificationRecieverClose::class.java)
        val pi4 = PendingIntent.getBroadcast(this, 0, clickintent4, 0)

        val clickintent5 = Intent(applicationContext, MainActivity::class.java)
        clickintent5.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        clickintent5.putExtra("NotificationMessage", "I am from Notification")
        clickintent5.addCategory(Intent.CATEGORY_LAUNCHER)
        clickintent5.action = Intent.ACTION_MAIN
        val pi5 = PendingIntent.getActivity(this, 0, clickintent5, 0)

        collapseview.setOnClickPendingIntent(R.id.plaupausebuttonnot, pi)
        collapseview.setOnClickPendingIntent(R.id.imageButtonbacksongnot, pi1)
        collapseview.setOnClickPendingIntent(R.id.imageButtonnexsongnot, pi2)
        collapseview.setOnClickPendingIntent(R.id.mainlayoutnot, pi3)
        collapseview.setOnClickPendingIntent(R.id.crossnot, pi4)
        collapseview.setOnClickPendingIntent(R.id.textViewplaingsongnot, pi5)

        val notification = NotificationCompat.Builder(this, App().CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_headset_black_24dp)
            .setCustomContentView(collapseview)
            .setContentIntent(pi5)
            .build()
        startForeground(1, notification)
    }

    fun getAllAudioFromDevice(context: Context, a: Int) {

        val tempAudioList = ArrayList<AudioModel>()
        if (!isServiceRunning) {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST
            )
            val c = context.contentResolver.query(
                uri,
                projection,
                null, null, null
            )

            if (c != null) {
                while (c.moveToNext()) {
                    val audioModel = AudioModel()
                    val path = c.getString(0)
                    val name = c.getString(1)
                    val album = c.getString(2)
                    val artist = c.getString(3)
                    val aj = MediaPlayer()
                    aj.setDataSource(path)
                    aj.prepare()
                    if (aj.duration >= 60000) {
                        audioModel.setaName(name)
                        audioModel.setaAlbum(album)
                        audioModel.setaArtist(artist)
                        audioModel.setaPath(path)
                        audioModel.setabitmap(aj.seconds)
                        tempAudioList.add(audioModel)
                        //adaptor.add(usersiten1(audioModel))
                    }
                }
                c.close()
            }
            if (tempAudioList.size != 0) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(tempAudioList.get(0).aPath)
                currentPlayingSong = tempAudioList.get(0)
                mediaPlayer.prepare()
                mediaPlayer.pause()
                refresh()

                SongLIstAdaptor().setList(tempAudioList)
                SongListFragment.adaptor.notifyDataSetChanged()
            }
            if (songList != null) {
                songList!!.clear()
            }
            songList = tempAudioList

        }

        if (a == 1) {
            Log.e("error11", songname)
            for (i in 0 until songList!!.size) {
                if (songList!![i].aName.toLowerCase(Locale.ROOT).indexOf(songname) != -1) {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(songList!!.get(i).aPath)
                    currentPlayingSong = songList!!.get(i)
                    playingsong = i
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    refresh()
                    return
                }
            }
        }
        return
    }

    fun refreshnotification() {
        val collapseview = RemoteViews(pakageName, R.layout.notification_layout)

        val clickintent = Intent(context, NotificationReciever::class.java)

        val pi = PendingIntent.getBroadcast(context, 0, clickintent, 0)

        val clickintent1 = Intent(context, NotificationRecieverBack::class.java)
        val pi1 = PendingIntent.getBroadcast(context, 0, clickintent1, 0)

        val clickintent2 = Intent(context, NotificationRecieverNext::class.java)
        val pi2 = PendingIntent.getBroadcast(context, 0, clickintent2, 0)

        val clickintent3 = Intent(context, NotificationRecieverMain::class.java)
        val pi3 = PendingIntent.getBroadcast(context, 0, clickintent3, 0)

        val clickintent4 = Intent(context, NotificationRecieverClose::class.java)
        val pi4 = PendingIntent.getBroadcast(context, 0, clickintent4, 0)


        val clickintent5 = Intent(context, MainActivity::class.java)
        clickintent5.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        clickintent5.putExtra("NotificationMessage", "I am from Notification")
        clickintent5.addCategory(Intent.CATEGORY_LAUNCHER)
        clickintent5.action = Intent.ACTION_MAIN
        val pi5 = PendingIntent.getActivity(context, 0, clickintent5, 0)

        collapseview.setOnClickPendingIntent(R.id.plaupausebuttonnot, pi)
        collapseview.setOnClickPendingIntent(R.id.imageButtonbacksongnot, pi1)
        collapseview.setOnClickPendingIntent(R.id.imageButtonnexsongnot, pi2)
        collapseview.setOnClickPendingIntent(R.id.mainlayoutnot, pi3)
        collapseview.setOnClickPendingIntent(R.id.crossnot, pi4)
        collapseview.setOnClickPendingIntent(R.id.textViewplaingsongnot, pi5)

        collapseview.setTextViewText(R.id.textViewplaingsongnot, currentPlayingSong?.aName)
        if (mediaPlayer.isPlaying) {
            collapseview.setImageViewResource(R.id.mplaypause, R.mipmap.play)
        } else {
            collapseview.setImageViewResource(R.id.mplaypause, R.mipmap.pause)
        }
        val notification = NotificationCompat.Builder(context!!, App().CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_headset_black_24dp)
            .setCustomContentView(collapseview)
            .setContentIntent(pi5)
            .build()
        val notificationManagerCompat = NotificationManagerCompat.from(context!!)
        notificationManagerCompat.notify(1, notification)


    }

    companion object {

        var pakageName = "com.example.alhaj.mediaplayer"

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        var mediaPlayer: MediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
        }
        var playingsong = 0
        var isServiceRunning = false
        var songList: ArrayList<AudioModel>? = null
        var currentPlayingSong: AudioModel? = null

        fun play() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
            refresh()
        }

        fun refresh() {
            MyService().refreshnotification()
        }

        fun back() {
            if (playingsong > 0) {
                playingsong = playingsong - 1
                mediaPlayer.reset()
                val obj = songList!![playingsong]
                mediaPlayer.setDataSource(obj.aPath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                currentPlayingSong = obj
            }
            refresh()
        }

        fun next() {
            try {
                if (playingsong < songList!!.size - 1) {
                    playingsong = playingsong + 1
                    mediaPlayer.reset()
                    val obj = songList!![playingsong]
                    mediaPlayer.setDataSource(obj.aPath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    currentPlayingSong = obj
                    Toast.makeText(context, "yeh i run", Toast.LENGTH_SHORT).show()
                    refresh()
                }
            } catch (e: Exception) {
            }
        }

    }

    private val MediaPlayer.seconds: String
        get() {
            var seconds1 = this.duration / 1000
            var i = 0
            while (seconds1 > 59) {
                i += 1
                seconds1 -= 60
            }
            return if (seconds1 < 10) {
                "$i:0$seconds1"
            } else {
                "$i:$seconds1"
            }
        }
}
