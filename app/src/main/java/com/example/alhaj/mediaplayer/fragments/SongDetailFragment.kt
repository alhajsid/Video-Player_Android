package com.example.alhaj.mediaplayer.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alhaj.mediaplayer.MyService
import com.example.alhaj.mediaplayer.MainActivity
import com.example.alhaj.mediaplayer.R
import kotlinx.android.synthetic.main.layout_song_detail.*

@SuppressLint("ValidFragment")
class SongDetailFragment  : Fragment() {

    val mSeekbarUpdateHandler = Handler()
    var mUpdateSeekbar:Runnable?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_song_detail,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewsongnamefullscreen.isSelected=true

        mUpdateSeekbar = object : Runnable {
            override fun run() {
                seekBarprogress.progress = MyService.mediaPlayer.currentPosition
                textViewcurrensongime.text = MyService.mediaPlayer.currentSeconds
                textViewmaxsongime.text = MyService.mediaPlayer.seconds
                /*
                seekBarprogress.max = MyService.msongplaer.duration;
                textViewsongnamefullscreen.text= MyService.currentplaingsong?.aName
                if (MyService.msongplaer.isPlaying){
                    buttonplapause.setBackgroundResource(R.mipmap.play)
                }else{
                    buttonplapause.setBackgroundResource(R.mipmap.pause)
                }
               */
                mSeekbarUpdateHandler.postDelayed(this, 1000)
            }
        }

        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 200);

        textViewsongnamefullscreen.text= MyService.currentPlayingSong?.aName

        seekBarprogress.max = MyService.mediaPlayer.duration;

        if (MyService.mediaPlayer.isPlaying){
            buttonplapause.setBackgroundResource(R.mipmap.play)
        }else{
            buttonplapause.setBackgroundResource(R.mipmap.pause)
        }

        buttonpaypau.setOnClickListener {
            if (MyService.mediaPlayer.isPlaying){
                buttonplapause.setBackgroundResource(R.mipmap.pause)
                MyService.mediaPlayer.pause()
            }else{
                seekBarprogress.max = MyService.mediaPlayer.duration
                buttonplapause.setBackgroundResource(R.mipmap.play)
                MyService.mediaPlayer.start()
            }

            MyService.refresh()
        }

        seekBarprogress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                if(b)
                    MyService.mediaPlayer.seekTo(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        buttonnexskip.setOnClickListener {
            if (MyService.playingsong < MyService.songList!!.size-1){
                MyService.playingsong = MyService.playingsong +1

                MyService.mediaPlayer.reset()
                val obj= MyService.songList!![MyService.playingsong]
                MyService.mediaPlayer.setDataSource(obj.aPath)
                MyService.mediaPlayer.prepare()
                MyService.mediaPlayer.start()
                MyService.currentPlayingSong =obj
                textViewsongnamefullscreen.text=obj.aName
                buttonplapause.setBackgroundResource(R.mipmap.play)
                seekBarprogress.max = MyService.mediaPlayer.duration
            }
            MyService.refresh()
        }

        buttonbackskip.setOnClickListener {
            if (MyService.playingsong >0){
                MyService.playingsong = MyService.playingsong -1
                MyService.mediaPlayer.reset()
                val obj= MyService.songList!![MyService.playingsong]
                textViewsongnamefullscreen.text=obj.aName
                MyService.mediaPlayer.setDataSource(obj.aPath)
                MyService.mediaPlayer.prepare()
                MyService.mediaPlayer.start()
                MyService.currentPlayingSong =obj
                buttonplapause.setBackgroundResource(R.mipmap.play)
                seekBarprogress.setMax(MyService.mediaPlayer.getDuration());
            }
            MyService.refresh()
        }
        buttonbackdeatil.setOnClickListener {
            MainActivity.mPager.setCurrentItem(0, true)
        }
    }

    fun setbuttons(){
        if (MyService.mediaPlayer.isPlaying){
            buttonplapause.setBackgroundResource(R.mipmap.play)
        }else{
            buttonplapause.setBackgroundResource(R.mipmap.pause)
        }

    }

    fun changesStatusBarColor(){
        val sindows=activity!!.window
        sindows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        sindows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        sindows.statusBarColor= ContextCompat.getColor(activity!!,R.color.colorAccent)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setbuttons()
            changesStatusBarColor()
        }
    }

    val MediaPlayer.seconds:String
        get() {
            var seconds1=this.duration / 1000
            var i=0
            while (seconds1>59){
                i += 1
                seconds1 -= 60
            }
            var o=""
            if(seconds1<10){
                o= "$i:0$seconds1"
            }else{
                o= "$i:$seconds1"

            }
            return o
        }


    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:String
        get() {
            var seconds1=this.currentPosition / 1000
            var i=0
            while (seconds1>59){
                i=i+1
                seconds1=seconds1-60
            }
            var o=""
            o = if(seconds1<10){
                "$i:0$seconds1"
            }else{
                "$i:$seconds1"

            }
            return o
        }

    override fun onDestroy() {
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
        super.onDestroy()
    }
}