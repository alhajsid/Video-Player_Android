package com.example.alhaj.mediaplayer


import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.media.MediaPlayer
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.alhaj.mediaplayer.OnilnesonglisActivity.Companion.aj
import com.example.alhaj.mediaplayer.fragments.SongDetailFragment
import com.example.alhaj.mediaplayer.fragments.SongListFragment
import com.example.alhaj.mediaplayer.viewpager.VerticalViewPager
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.layout_songdetail.view.*
import kotlinx.android.synthetic.main.recviewitemsong.view.*
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.*
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_onilnesonglis.*


class OnilnesonglisActivity : AppCompatActivity() {


    val alhaj=this
    lateinit var mAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onilnesonglis)

        mAdapter = MyAdapter(supportFragmentManager,alhaj)
        mPager = findViewById(R.id.viewpager)
        isReadStoragePermissionGranted()
        buttonpermisiion.setOnClickListener{
            isReadStoragePermissionGranted()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mPager.adapter = mAdapter
                buttonpermisiion.visibility=View.GONE
            }
        }
    }


    fun isReadStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("FragmentActivity", "Permission is granted1")
                mPager.adapter = mAdapter
                buttonpermisiion.visibility=View.GONE
                return true
            } else {
                Log.e("FragmentActivity", "Permission is revoked1")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.FOREGROUND_SERVICE), 3)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    class MyAdapter(fm: FragmentManager,val alhaj:Context) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment? {

            when (position) {
                0 -> return SongListFragment(alhaj)
                1 ->
                    // return a different Fragment class here
                    // if you want want a completely different layout
                    return SongDetailFragment(alhaj)
                else -> return null
            }
        }
    }


    companion object {
        lateinit var mPager: VerticalViewPager
        val aj=MediaPlayer()
        var CURRENT_PAGE = 1

    }

}

class AudioModel {
    internal var aPath: String=""
    internal var aName: String=""
    internal var aAlbum: String=""
    internal var aArtist: String=""
    internal var time: String=""

    fun getaPath(): String {
        return aPath
    }

    fun setabitmap(bitmap: String) {
        this.time = bitmap
    }

    fun setaPath(aPath: String) {
        this.aPath = aPath
    }

    fun getaName(): String {
        return aName
    }

    fun setaName(aName: String) {
        this.aName = aName
    }

    fun getaAlbum(): String {
        return aAlbum
    }

    fun setaAlbum(aAlbum: String) {
        this.aAlbum = aAlbum
    }

    fun getaArtist(): String {
        return aArtist
    }

    fun setaArtist(aArtist: String) {
        this.aArtist = aArtist
    }
}
/*
class usersiten1(val use8:AudioModel): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewArisname.text=use8.aArtist
        viewHolder.itemView.textViewsongname.text=use8.aName
        viewHolder.itemView.textViewtime.text=use8.time
    }

    override fun getLayout(): Int {
        return R.layout.recviewitemsong
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
}

*/
