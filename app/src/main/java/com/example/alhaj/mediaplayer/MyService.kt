package com.example.alhaj.mediaplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.alhaj.mediaplayer.Adaptors.SongLIstAdaptor
import com.example.alhaj.mediaplayer.BroadcastReciever.*
import com.example.alhaj.mediaplayer.fragments.SongListFragment
import com.example.alhaj.mediaplayer.fragments.SongListFragment.Companion.adaptor

class MyService : Service() {

    var songname:String = ""

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        aj1=this.packageName
        alhaj1=this
        try {
            songname = intent!!.getStringExtra("songname").toLowerCase()
        }catch (e:Exception){
            Log.e("erroe",e.toString())
        }
        init()

        return START_NOT_STICKY
    }


    fun init(){
        if(songname=="pause"){
            msongplaer.pause()
            refresh()
            return
        }else if(songname=="play"){
            msongplaer.start()
            refresh()
            return
        }
        else if(songname=="next"){
            next()
            refresh()
            return
        }else if(songname=="back"){
            back()
            refresh()
            return
        }
        else if (songname.length>1) {
            getAllAudioFromDevice(this,1)
            isserviserunning = true
            startForgroundmService()
            return
        }else{
            Log.e("my service","get all songs")
            getAllAudioFromDevice(alhaj1!!,0)
        }
        isserviserunning = true
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

        collapseview.setOnClickPendingIntent(R.id.plaupausebuttonnot, pi)
        collapseview.setOnClickPendingIntent(R.id.imageButtonbacksongnot, pi1)
        collapseview.setOnClickPendingIntent(R.id.imageButtonnexsongnot, pi2)
        collapseview.setOnClickPendingIntent(R.id.mainlayoutnot, pi3)
        collapseview.setOnClickPendingIntent(R.id.crossnot, pi4)

        val notification = NotificationCompat.Builder(this, App().CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_headset_black_24dp)
            .setCustomContentView(collapseview)
            .build()
        startForeground(1, notification)
    }

    fun getAllAudioFromDevice(context: Context,a:Int) {

        val tempAudioList = ArrayList<AudioModel>()
        if(!isserviserunning){
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
                    if(aj.duration>=60000){
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
                msongplaer.reset()
                MyService.msongplaer.setDataSource(tempAudioList.get(0).aPath)
                MyService.currentplaingsong = tempAudioList.get(0)
                MyService.msongplaer.prepare()
                msongplaer.pause()
                refresh()

                SongLIstAdaptor().setList(tempAudioList)
                SongListFragment.adaptor.notifyDataSetChanged()
            }
            if (allsonglist != null) {
                allsonglist!!.clear()
            }
            allsonglist = tempAudioList

        }

        if(a==1){
            Log.e("error11",songname)
            for (i in 0..allsonglist!!.size-1){
                if(allsonglist!![i].aName.toLowerCase().indexOf(songname)!=-1){
                    msongplaer.reset()
                    MyService.msongplaer.setDataSource(allsonglist!!.get(i).aPath)
                    MyService.currentplaingsong = allsonglist!!.get(i)
                    playingsong=i
                    MyService.msongplaer.prepare()
                    msongplaer.start()
                    refresh()
                    return
                }
            }
        }
        return
    }

    fun refreshnotification() {
            val collapseview = RemoteViews(aj1, R.layout.notification_layout)

            val clickintent = Intent(alhaj1, NotificationReciever::class.java)

            val pi = PendingIntent.getBroadcast(alhaj1, 0, clickintent, 0)

            val clickintent1 = Intent(alhaj1, NotificationRecieverBack::class.java)
            val pi1 = PendingIntent.getBroadcast(alhaj1, 0, clickintent1, 0)

            val clickintent2 = Intent(alhaj1, NotificationRecieverNext::class.java)
            val pi2 = PendingIntent.getBroadcast(alhaj1, 0, clickintent2, 0)

            val clickintent3 = Intent(alhaj1, NotificationRecieverMain::class.java)
            val pi3 = PendingIntent.getBroadcast(alhaj1, 0, clickintent3, 0)

            val clickintent4 = Intent(alhaj1, NotificationRecieverClose::class.java)
            val pi4 = PendingIntent.getBroadcast(alhaj1, 0, clickintent4, 0)

            collapseview.setOnClickPendingIntent(R.id.plaupausebuttonnot, pi)
            collapseview.setOnClickPendingIntent(R.id.imageButtonbacksongnot, pi1)
            collapseview.setOnClickPendingIntent(R.id.imageButtonnexsongnot, pi2)
            collapseview.setOnClickPendingIntent(R.id.mainlayoutnot, pi3)
            collapseview.setOnClickPendingIntent(R.id.crossnot, pi4)

            collapseview.setTextViewText(R.id.textViewplaingsongnot, currentplaingsong?.aName)
            if (msongplaer.isPlaying) {
                collapseview.setImageViewResource(R.id.mplaypause, R.mipmap.play)
            } else {
                collapseview.setImageViewResource(R.id.mplaypause, R.mipmap.pause)
            }
            val notification = NotificationCompat.Builder(alhaj1!!, App().CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_headset_black_24dp)
                .setCustomContentView(collapseview)
                .build()
            val Noti = NotificationManagerCompat.from(alhaj1!!)
            Noti.notify(1, notification)


    }


    companion object {

        var aj1="com.example.alhaj.mediaplayer"
        @SuppressLint("StaticFieldLeak")
        var alhaj1:Context?=null
        var msongplaer: MediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        var playingsong = 0
        var isserviserunning = false
        var allsonglist: ArrayList<AudioModel>? = null
        var currentplaingsong: AudioModel? = null

        fun play() {
            if (msongplaer.isPlaying) {
                msongplaer.pause()
            } else {
                msongplaer.start()
            }
            refresh()
        }

        fun refresh() {
            MyService().refreshnotification()
        }

        fun back() {
            if (playingsong > 0) {
                playingsong = playingsong - 1
                MyService.msongplaer.reset()
                val obj = allsonglist!![playingsong]
                msongplaer.setDataSource(obj.aPath)
                msongplaer.prepare()
                msongplaer.start()
                currentplaingsong = obj
            }
            refresh()
        }

        fun next() {
            try {
                if (playingsong < allsonglist!!.size - 1) {
                    playingsong = playingsong + 1
                    MyService.msongplaer.reset()
                    val obj = allsonglist!![playingsong]
                    msongplaer.setDataSource(obj.aPath)
                    msongplaer.prepare()
                    msongplaer.start()
                    currentplaingsong = obj
                    Toast.makeText(alhaj1, "yeh i run", Toast.LENGTH_SHORT).show()
                    refresh()
                }
            } catch (e: Exception) {
            }
        }

    }

    val MediaPlayer.seconds: String
        get() {
            var seconds1 = this.duration / 1000
            var i = 0
            while (seconds1 > 59) {
                i += 1
                seconds1 -= 60
            }
            var o = ""
            if (seconds1 < 10) {
                o = i.toString() + ":0" + seconds1.toString()
            } else {
                o = i.toString() + ":" + seconds1.toString()
            }
            return o
        }
}
