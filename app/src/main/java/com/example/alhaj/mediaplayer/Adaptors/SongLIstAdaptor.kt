package com.example.alhaj.mediaplayer.Adaptors

import android.content.res.ColorStateList
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
        p0.set(SongList[p1].aName,SongList[p1].aArtist,SongList[p1].time,p1, SongList[p1].aPath)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songName:TextView = itemView.findViewById(R.id.textViewsongname)
        var songArtist:TextView = itemView.findViewById(R.id.textViewArisname)
        var songDuration:TextView = itemView.findViewById(R.id.textViewtime)
        var main_container:View = itemView.findViewById(R.id.main_container)

        fun set(name:String,artist:String,duration:String,position:Int,path:String){
            songName.text=name
            songArtist.text=artist
            songDuration.text=duration

            if(MyService.currentPlayingSong!=null && path==MyService.currentPlayingSong!!.aPath){
                main_container.backgroundTintList= ColorStateList.valueOf(itemView.context.resources.getColor(R.color.colorSelected))
            }else{
                main_container.backgroundTintList= ColorStateList.valueOf(itemView.context.resources.getColor(R.color.colorWhite))
            }

            itemView.setOnClickListener {
                MyService.mediaPlayer.reset()
                val obj = MyService.songList!![position]
                SongListFragment.playingSong.text = obj.aName
                MyService.mediaPlayer.setDataSource(obj.aPath)
                MyService.mediaPlayer.prepare()
                MyService.mediaPlayer.start()
                MyService.currentPlayingSong = obj
                MyService.playingSongIndex = position
                SongListFragment.playPauseBtn.setBackgroundResource(R.mipmap.play)
                MyService.refresh()
                notifyDataSetChanged()
            }
        }
    }
}