package com.example.alhaj.mediaplayer.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
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

    companion object {
        lateinit var adaptor: SongLIstAdaptor

        @SuppressLint("StaticFieldLeak")
        lateinit var playingSong: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var playPauseBtn: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var progressBar1: ProgressBar
    }

    val mSeekbarUpdateHandler = Handler()
    var mUpdateSeekbar: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_song_list, container, false)
        playingSong = view.findViewById(R.id.textViewplaingsong)
        playPauseBtn = view.findViewById(R.id.imageButtonplasong)
        progressBar1 = view.findViewById(R.id.seekbar2)
        return view
    }

    override fun onStart() {
        super.onStart()

        try {
            if (MyService.mediaPlayer.isPlaying) {
                imageButtonplasong.setBackgroundResource(R.mipmap.play)
            } else {
                imageButtonplasong.setBackgroundResource(R.mipmap.pause)
            }
            seekbar2.max = MyService.mediaPlayer.duration
            changesStatusBarColor()
            adaptor.notifyDataSetChanged()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun refreshUi() {
        adaptor.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewplaingsong.isSelected = true
        initRecyclerView()

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView()
        }
    }

    fun initView(){
        if (MyService.isServiceRunning) {
            setUpDataFromService()
        } else {
            startService()
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

        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 100)

        seekbar2.max = MyService.mediaPlayer.duration


        MyService.mediaPlayer.setOnCompletionListener {
            try {
                if (MyService.playingSongIndex < MyService.songList!!.size - 1) {
                    MyService.playingSongIndex = MyService.playingSongIndex + 1
                    val obj = MyService.songList!![MyService.playingSongIndex]
                    MyService.mediaPlayer.reset()
                    MyService.mediaPlayer.setDataSource(obj.aPath)
                    MyService.mediaPlayer.prepare()
                    MyService.mediaPlayer.start()
                    MyService.currentPlayingSong = obj
                } else {
                    MyService.playingSongIndex = 0
                    val obj = MyService.songList!![MyService.playingSongIndex]
                    MyService.mediaPlayer.reset()
                    MyService.mediaPlayer.setDataSource(obj.aPath)
                    MyService.mediaPlayer.prepare()
                    MyService.mediaPlayer.start()
                    MyService.currentPlayingSong = obj
                }
                refresh()
                refreshUi()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        if (MyService.mediaPlayer.isPlaying) {
            imageButtonplasong.setBackgroundResource(R.mipmap.play)
        } else {
            imageButtonplasong.setBackgroundResource(R.mipmap.pause)
        }

        clickListeners()
        changesStatusBarColor()
    }

    private fun setUpDataFromService() {
        if (MyService.songList?.size != 0) {
            adaptor.setList(MyService.songList!!)
        }
        adaptor.notifyDataSetChanged()
        textViewplaingsong.text = MyService.currentPlayingSong?.aName
    }

    fun initRecyclerView(){

        adaptor = SongLIstAdaptor()
        recyclerveiwlistsongs.adapter = adaptor
    }

    private fun startService() {
        context!!.startService(Intent(context!!, MyService::class.java))
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        context!!.registerReceiver(MyReceiver(), filter)
    }

    fun clickListeners(){
        smallsongpla.setOnClickListener {
            MainActivity.mPager.setCurrentItem(1, true)
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
            refreshUi()
        }

        imageButtonnexsong.setOnClickListener {
            playNextSong()
            refresh()
            refreshUi()
        }

        imageButtonbacksong.setOnClickListener {
            playBackSong()
            refresh()
            refreshUi()
        }
    }

    fun playNextSong() {
        if (MyService.playingSongIndex < MyService.songList!!.size - 1) {
            try {
                MyService.playingSongIndex = MyService.playingSongIndex + 1
                MyService.mediaPlayer.reset()
                val obj = MyService.songList!![MyService.playingSongIndex]
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

    fun playBackSong() {
        if (MyService.playingSongIndex > 0) {
            MyService.playingSongIndex = MyService.playingSongIndex - 1
            MyService.mediaPlayer.reset()
            val obj = MyService.songList!![MyService.playingSongIndex]
            textViewplaingsong.text = obj.aName
            MyService.mediaPlayer.setDataSource(obj.aPath)
            MyService.mediaPlayer.prepare()
            MyService.mediaPlayer.start()
            seekbar2.setMax(MyService.mediaPlayer.getDuration());
            MyService.currentPlayingSong = obj
            imageButtonplasong.setBackgroundResource(R.mipmap.play)
        }
    }

    fun changesStatusBarColor() {
        val sindows = activity!!.window
        sindows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        sindows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        sindows.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorWhite)
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

  