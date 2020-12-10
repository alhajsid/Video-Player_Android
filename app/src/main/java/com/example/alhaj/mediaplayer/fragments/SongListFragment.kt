package com.example.alhaj.mediaplayer.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.example.alhaj.mediaplayer.*
import com.example.alhaj.mediaplayer.Adaptors.SongLIstAdaptor
import com.example.alhaj.mediaplayer.BroadcastReciever.NotificationReciever
import com.example.alhaj.mediaplayer.MyService.Companion.msongplaer
import com.example.alhaj.mediaplayer.MyService.Companion.refresh
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_onilnesonglis.*
import kotlinx.android.synthetic.main.layout_songlist.*

@SuppressLint("ValidFragment")
class SongListFragment(var mContext: Context) : Fragment() {


    companion object{
        lateinit var adaptor :SongLIstAdaptor
        @SuppressLint("StaticFieldLeak")
        lateinit var playingSong:TextView
        @SuppressLint("StaticFieldLeak")
        lateinit var playPauseBtn:ImageView
        @SuppressLint("StaticFieldLeak")
        lateinit var progressBar1:ProgressBar
    }

    val mSeekbarUpdateHandler = Handler()
    var mUpdateSeekbar: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.layout_songlist, container, false)
        playingSong=view.findViewById(R.id.textViewplaingsong)
        playPauseBtn=view.findViewById(R.id.imageButtonplasong)
        progressBar1=view.findViewById(R.id.seekbar2)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adaptor= SongLIstAdaptor()
        recyclerveiwlistsongs.adapter = adaptor

        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!MyService.isserviserunning) {
                //getAllAudioFromDevice(mContext)
                Log.e("service started"," SongListFragment")
                mContext.startService(Intent(mContext, MyService::class.java))
                val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
                mContext.registerReceiver(MyReceiver(), filter)
            } else {
                /////maybe error #################################################################
                if (MyService.allsonglist?.size != 0) {
                    adaptor.setList(MyService.allsonglist!!)
                }
                adaptor.notifyDataSetChanged()
                textViewplaingsong.text = MyService.currentplaingsong?.aName
            }

            mUpdateSeekbar = object : Runnable {
                override fun run() {
                    seekbar2.progress = MyService.msongplaer.currentPosition
                    playingSong.text = MyService.currentplaingsong?.aName
                    seekbar2.max = MyService.msongplaer.duration
                    if (MyService.msongplaer.isPlaying) {
                        imageButtonplasong.setBackgroundResource(R.mipmap.play)
                    } else {
                        imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                    }


                    mSeekbarUpdateHandler.postDelayed(this, 1000)
                }
            }
            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 100);
            seekbar2.setMax(MyService.msongplaer.getDuration());

            smallsongpla.setOnClickListener {
                OnilnesonglisActivity.mPager.setCurrentItem(1,true)
            }

            MyService.msongplaer.setOnCompletionListener {
                try {
                    if (MyService.playingsong < MyService.allsonglist!!.size - 1) {
                        MyService.playingsong = MyService.playingsong + 1
                        val obj = MyService.allsonglist!![MyService.playingsong]
                        MyService.msongplaer.reset()
                        MyService.msongplaer.setDataSource(obj.aPath)
                        MyService.msongplaer.prepare()
                       // MyService.msongplaer.start()
                        MyService.currentplaingsong = obj

                        Toast.makeText(context,"a",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        MyService.playingsong =0
                        val obj = MyService.allsonglist!![MyService.playingsong]
                        MyService.msongplaer.reset()
                        MyService.msongplaer.setDataSource(obj.aPath)
                        MyService.msongplaer.prepare()
                        MyService.msongplaer.start()
                        MyService.currentplaingsong = obj

                        Toast.makeText(context,"b",Toast.LENGTH_SHORT).show()
                    }
                    refresh()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }


            if (MyService.msongplaer.isPlaying) {
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
            } else {
                imageButtonplasong.setBackgroundResource(R.mipmap.pause)
            }

            playpauseback.setOnClickListener {
                if (MyService.msongplaer.isPlaying) {
                    imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                    MyService.msongplaer.pause()
                } else {
                    imageButtonplasong.setBackgroundResource(R.mipmap.play)
                    MyService.msongplaer.start()
                }
                refresh()
            }

            imageButtonnexsong.setOnClickListener {
                nextSong()
                refresh()
            }

            imageButtonbacksong.setOnClickListener {
                backwardSong()
                refresh()
            }
            changesStatusBarColor()

            //  val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // ActivityCompat.requestPermissions(OnilnesonglisActivity(), permissions, 0)
            // val permissions1 = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            //ActivityCompat.requestPermissions(OnilnesonglisActivity(), permissions1, 1)

           /* adaptor.setOnItemClickListener { item, view ->
                MyService.msongplaer.reset()
                val obj = MyService.allsonglist!![adaptor.getAdapterPosition(item)]
                textViewplaingsong.text = obj.aName
                MyService.msongplaer.setDataSource(obj.aPath)
                MyService.msongplaer.prepare()
                MyService.msongplaer.start()
                MyService.currentplaingsong = obj
                MyService.playingsong = adaptor.getAdapterPosition(item)
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
                refresh()
            }
               */
            //seekbar2.max = MyService.msongplaer!!.duration;
        } else {

        }
    }

    fun nextSong(){
        if (MyService.playingsong < MyService.allsonglist!!.size - 1) {
            try {
                MyService.playingsong = MyService.playingsong + 1
                MyService.msongplaer.reset()
                val obj = MyService.allsonglist!![MyService.playingsong]
                textViewplaingsong.text = obj.aName
                MyService.msongplaer.setDataSource(obj.aPath)
                MyService.msongplaer.prepare()
                MyService.msongplaer.start()
                MyService.currentplaingsong = obj
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
                seekbar2.max = MyService.msongplaer.duration
            } catch (e: Exception) {
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun backwardSong(){
        if (MyService.playingsong > 0) {
            MyService.playingsong = MyService.playingsong - 1
            MyService.msongplaer.reset()
            val obj = MyService.allsonglist!![MyService.playingsong]
            textViewplaingsong.text = obj.aName
            MyService.msongplaer.setDataSource(obj.aPath)
            MyService.msongplaer.prepare()
            MyService.msongplaer.start()
            seekbar2.setMax(MyService.msongplaer.getDuration());
            MyService.currentplaingsong = obj
            imageButtonplasong.setBackgroundResource(R.mipmap.play)
        }
    }

    fun changesStatusBarColor(){
        val sindows=activity!!.window
        sindows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        sindows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        sindows.statusBarColor=ContextCompat.getColor(activity!!,R.color.colorWhite)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            try {
                if (MyService.msongplaer.isPlaying) {
                    imageButtonplasong.setBackgroundResource(R.mipmap.play)
                } else {
                    imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                }

                seekbar2.max = MyService.msongplaer.duration;

                changesStatusBarColor()
            } catch (e: Exception) {

            }

        }
    }

    override fun onDestroy() {
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
        super.onDestroy()
    }
}

  