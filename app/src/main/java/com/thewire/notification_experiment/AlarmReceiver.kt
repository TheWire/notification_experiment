package com.thewire.notification_experiment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "MYACTION") {
            val myString = intent.getStringExtra("MYSTRING")
            Toast.makeText(context, myString, Toast.LENGTH_SHORT).show()
            println("alarm alarm alarm")
        }
    }
}