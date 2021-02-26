package com.example.alhaj.mediaplayer.Adaptors

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alhaj.mediaplayer.AudioModel
import com.example.alhaj.mediaplayer.MyService
import com.example.alhaj.mediaplayer.R
import com.example.alhaj.mediaplayer.fragments.SongListFragment

class SongLIstAdaptor: RecyclerView.Adapter<SongLIstAdaptor.ViewHolder>() {
    companion object{
       var SongList=ArrayList<AudioModel>()
    }
    fun setList(list:ArrayList<AudioModel>){

        SongList=list
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view=LayoutInflater.from(p0.context).inflate(R.layout.recview_item_song,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return SongList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.set(SongList[p1].aName,SongList[p1].aArtist,SongList[p1].time,p1)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var songName:TextView
        lateinit var songArtist:TextView
        lateinit var songDuration:TextView

        init {
            songName=itemView.findViewById(R.id.textViewsongname)
            songArtist=itemView.findViewById(R.id.textViewArisname)
            songDuration=itemView.findViewById(R.id.textViewtime)


        }
        fun set(name:String,artist:String,duration:String,position:Int){
            songName.text=name
            songArtist.text=artist
            songDuration.text=duration

            itemView.setOnClickListener {
                MyService.msongplaer.reset()
                val obj = MyService.allsonglist!![position]
                SongListFragment.playingSong.text = obj.aName
                MyService.msongplaer.setDataSource(obj.aPath)
                MyService.msongplaer.prepare()
                MyService.msongplaer.start()
                MyService.currentplaingsong = obj
                MyService.playingsong = position
                SongListFragment.playPauseBtn.setBackgroundResource(R.mipmap.play)
                MyService.refresh()
            }
        }
    }
}