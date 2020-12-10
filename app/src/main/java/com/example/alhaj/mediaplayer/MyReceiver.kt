package com.example.alhaj.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if(intent.action== Intent.ACTION_HEADSET_PLUG){
            var op=false
            val state=intent.getIntExtra("state",-1)
            when(state){
                0->{
                    if (op)
                    Toast.makeText(context,"Headphone Unplugged",Toast.LENGTH_SHORT).show()
                    op=true
                }
                1->{
                    Toast.makeText(context,"Headphone plugged",Toast.LENGTH_SHORT).show()
                }
                else->{

                }

            }
        }
    }
}
