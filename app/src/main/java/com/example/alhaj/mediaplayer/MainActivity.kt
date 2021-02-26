package com.example.alhaj.mediaplayer


import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.alhaj.mediaplayer.fragments.SongDetailFragment
import com.example.alhaj.mediaplayer.fragments.SongListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    val alhaj=this
    lateinit var mAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPager = findViewById(R.id.viewpager)
        if (isReadStoragePermissionGranted()){
            initView()
        }
        buttonpermisiion.setOnClickListener{
            isReadStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initView()
            }
        }
    }

    enum class fragments{
        list,detail
    }

    var currentFragment=fragments.list

    fun initView(){
        buttonpermisiion.visibility=View.GONE
        mAdapter = MyAdapter(this)
        mAdapter.addFragment(SongListFragment())
        mAdapter.addFragment(SongDetailFragment())
        mPager.adapter=mAdapter
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentFragment=if (position==0) fragments.list else fragments.detail
            }
        })

    }


    override fun onBackPressed() {
        if (currentFragment==fragments.detail){
            mPager.setCurrentItem(0,true)
        }else{
            super.onBackPressed()
        }
    }


    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("FragmentActivity", "Permission is granted1")
                true
            } else {
                Log.e("FragmentActivity", "Permission is revoked1")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.FOREGROUND_SERVICE), 3)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    class MyAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

        private val arrayList: ArrayList<Fragment> = ArrayList()

        override fun getItemCount(): Int {
            return arrayList.size
        }

        override fun createFragment(position: Int): Fragment {
            return arrayList[position]
        }


        fun addFragment(fragment: Fragment?) {
            arrayList.add(fragment!!)
        }

    }


    companion object {
        lateinit var mPager: ViewPager2

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
