package com.example.alhaj.mediaplayer.BroadcastReciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.alhaj.mediaplayer.MyService
import com.example.alhaj.mediaplayer.MyService.Companion.alhaj1
import com.example.alhaj.mediaplayer.OnilnesonglisActivity

class NotificationReciever :BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        MyService.play()
        Toast.makeText(p0,"broadcast",Toast.LENGTH_SHORT).show()

    }

}

class NotificationRecieverBack :BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        MyService.back()
    }

}

class NotificationRecieverNext :BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        MyService.next()

    }

}
class NotificationRecieverClose :BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        val int=Intent(p0,MyService::class.java)
        p0!!.stopService(int)
        MyService.msongplaer.stop()
    }

}

class NotificationRecieverMain :BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        val intent=Intent(p0,OnilnesonglisActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or( Intent.FLAG_ACTIVITY_NEW_TASK)
        p0!!.startActivity(intent)
    }

}