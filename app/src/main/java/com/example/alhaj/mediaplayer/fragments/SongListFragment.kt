package com.example.alhaj.mediaplayer.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alhaj.mediaplayer.Adaptors.SongLIstAdaptor
import com.example.alhaj.mediaplayer.MainActivity
import com.example.alhaj.mediaplayer.MyReceiver
import com.example.alhaj.mediaplayer.MyService
import com.example.alhaj.mediaplayer.MyService.Companion.refresh
import com.example.alhaj.mediaplayer.R
import kotlinx.android.synthetic.main.layout_song_list.*

@SuppressLint("ValidFragment")
class SongListFragment : Fragment() {


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
        val view= inflater.inflate(R.layout.layout_song_list, container, false)
        playingSong=view.findViewById(R.id.textViewplaingsong)
        playPauseBtn=view.findViewById(R.id.imageButtonplasong)
        progressBar1=view.findViewById(R.id.seekbar2)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewplaingsong.isSelected=true
        adaptor= SongLIstAdaptor()
        recyclerveiwlistsongs.adapter = adaptor
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!MyService.isServiceRunning) {
                //getAllAudioFromDevice(context!!)
                Log.e("service started"," SongListFragment")
                context!!.startService(Intent(context!!, MyService::class.java))
                val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
                context!!.registerReceiver(MyReceiver(), filter)
            } else {
                /////maybe error #################################################################
                if (MyService.songList?.size != 0) {
                    adaptor.setList(MyService.songList!!)
                }
                adaptor.notifyDataSetChanged()
                textViewplaingsong.text = MyService.currentPlayingSong?.aName
            }

            mUpdateSeekbar = object : Runnable {
                override fun run() {
                    seekbar2.progress = MyService.mediaPlayer.currentPosition
                    playingSong.text = MyService.currentPlayingSong?.aName
                    seekbar2.max = MyService.mediaPlayer.duration
                    if (MyService.mediaPlayer.isPlaying) {
                        imageButtonplasong.setBackgroundResource(R.mipmap.play)
                    } else {
                        imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                    }


                    mSeekbarUpdateHandler.postDelayed(this, 1000)
                }
            }
            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 100);
            seekbar2.setMax(MyService.mediaPlayer.getDuration());

            smallsongpla.setOnClickListener {
                MainActivity.mPager.setCurrentItem(1,true)
            }

            MyService.mediaPlayer.setOnCompletionListener {
                try {
                    if (MyService.playingsong < MyService.songList!!.size - 1) {
                        MyService.playingsong = MyService.playingsong + 1
                        val obj = MyService.songList!![MyService.playingsong]
                        MyService.mediaPlayer.reset()
                        MyService.mediaPlayer.setDataSource(obj.aPath)
                        MyService.mediaPlayer.prepare()
                        MyService.mediaPlayer.start()
                        MyService.currentPlayingSong = obj
                    }
                    else{
                        MyService.playingsong =0
                        val obj = MyService.songList!![MyService.playingsong]
                        MyService.mediaPlayer.reset()
                        MyService.mediaPlayer.setDataSource(obj.aPath)
                        MyService.mediaPlayer.prepare()
                        MyService.mediaPlayer.start()
                        MyService.currentPlayingSong = obj
                    }
                    refresh()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }


            if (MyService.mediaPlayer.isPlaying) {
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
            } else {
                imageButtonplasong.setBackgroundResource(R.mipmap.pause)
            }

            playpauseback.setOnClickListener {
                if (MyService.mediaPlayer.isPlaying) {
                    imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                    MyService.mediaPlayer.pause()
                } else {
                    imageButtonplasong.setBackgroundResource(R.mipmap.play)
                    MyService.mediaPlayer.start()
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
        if (MyService.playingsong < MyService.songList!!.size - 1) {
            try {
                MyService.playingsong = MyService.playingsong + 1
                MyService.mediaPlayer.reset()
                val obj = MyService.songList!![MyService.playingsong]
                textViewplaingsong.text = obj.aName
                MyService.mediaPlayer.setDataSource(obj.aPath)
                MyService.mediaPlayer.prepare()
                MyService.mediaPlayer.start()
                MyService.currentPlayingSong = obj
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
                seekbar2.max = MyService.mediaPlayer.duration
            } catch (e: Exception) {
                Toast.makeText(context!!, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun backwardSong(){
        if (MyService.playingsong > 0) {
            MyService.playingsong = MyService.playingsong - 1
            MyService.mediaPlayer.reset()
            val obj = MyService.songList!![MyService.playingsong]
            textViewplaingsong.text = obj.aName
            MyService.mediaPlayer.setDataSource(obj.aPath)
            MyService.mediaPlayer.prepare()
            MyService.mediaPlayer.start()
            seekbar2.setMax(MyService.mediaPlayer.getDuration());
            MyService.currentPlayingSong = obj
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
                if (MyService.mediaPlayer.isPlaying) {
                    imageButtonplasong.setBackgroundResource(R.mipmap.play)
                } else {
                    imageButtonplasong.setBackgroundResource(R.mipmap.pause)
                }

                seekbar2.max = MyService.mediaPlayer.duration;

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

  