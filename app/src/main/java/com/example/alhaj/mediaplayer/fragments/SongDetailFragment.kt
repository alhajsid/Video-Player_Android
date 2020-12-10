package com.example.alhaj.mediaplayer.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import com.example.alhaj.mediaplayer.MyService
import com.example.alhaj.mediaplayer.OnilnesonglisActivity
import com.example.alhaj.mediaplayer.R
import kotlinx.android.synthetic.main.activity_onilnesonglis.*
import kotlinx.android.synthetic.main.layout_songdetail.*

@SuppressLint("ValidFragment")
class SongDetailFragment (s:Context) : Fragment() {



    val mSeekbarUpdateHandler = Handler()
    var mUpdateSeekbar:Runnable?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_songdetail,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mUpdateSeekbar = object : Runnable {
            override fun run() {
                seekBarprogress.progress = MyService.msongplaer.currentPosition
                textViewcurrensongime.text = MyService.msongplaer.currentSeconds
                textViewmaxsongime.text = MyService.msongplaer.seconds
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

        textViewsongnamefullscreen.text= MyService.currentplaingsong?.aName

        seekBarprogress.max = MyService.msongplaer.duration;

        if (MyService.msongplaer.isPlaying){
            buttonplapause.setBackgroundResource(R.mipmap.play)
        }else{
            buttonplapause.setBackgroundResource(R.mipmap.pause)
        }

        buttonpaypau.setOnClickListener {
            if (MyService.msongplaer.isPlaying){
                buttonplapause.setBackgroundResource(R.mipmap.pause)
                MyService.msongplaer.pause()
            }else{
                seekBarprogress.setMax(MyService.msongplaer.getDuration());
                buttonplapause.setBackgroundResource(R.mipmap.play)
                MyService.msongplaer.start()
            }

            MyService.refresh()
        }

        seekBarprogress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                if(b)
                    MyService.msongplaer.seekTo(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        buttonnexskip.setOnClickListener {
            if (MyService.playingsong < MyService.allsonglist!!.size-1){
                MyService.playingsong = MyService.playingsong +1

                MyService.msongplaer.reset()
                val obj= MyService.allsonglist!![MyService.playingsong]
                MyService.msongplaer.setDataSource(obj.aPath)
                MyService.msongplaer.prepare()
                MyService.msongplaer.start()
                MyService.currentplaingsong =obj
                buttonplapause.setBackgroundResource(R.mipmap.play)
                seekBarprogress.setMax(MyService.msongplaer.getDuration());
            }

            MyService.refresh()
        }

        buttonbackskip.setOnClickListener {
            if (MyService.playingsong >0){
                MyService.playingsong = MyService.playingsong -1
                MyService.msongplaer.reset()
                val obj= MyService.allsonglist!![MyService.playingsong]
                textViewsongnamefullscreen.text=obj.aName
                MyService.msongplaer.setDataSource(obj.aPath)
                MyService.msongplaer.prepare()
                MyService.msongplaer.start()
                MyService.currentplaingsong =obj
                buttonplapause.setBackgroundResource(R.mipmap.play)
                seekBarprogress.setMax(MyService.msongplaer.getDuration());
            }
            MyService.refresh()
        }
        buttonbackdeatil.setOnClickListener {
            OnilnesonglisActivity.mPager.setCurrentItem(0, true)
        }
    }

    fun setbuttons(){
        if (MyService.msongplaer.isPlaying){
            buttonplapause.setBackgroundResource(R.mipmap.play)
        }else{
            buttonplapause.setBackgroundResource(R.mipmap.pause)
        }

    }
    fun changesStatusBarColor(){
        val sindows=activity!!.window
        sindows.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        sindows.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        sindows.statusBarColor=ContextCompat.getColor(activity!!,R.color.colorAccent)
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
                o=i.toString()+":0"+seconds1.toString()
            }else{
                o=i.toString()+":"+seconds1.toString()

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
                i.toString()+":0"+seconds1.toString()
            }else{
                i.toString()+":"+seconds1.toString()

            }
            return o
        }

    override fun onDestroy() {
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
        super.onDestroy()
    }
}