package com.thewire.notification_experiment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "MYNOTIFICATIONACTION") {
            val myString = intent.getStringExtra("MYSTRING")
            myString?.let {
                notification(context, "alarm notification", myString, myString)
            }
            println("notification alarm alarm alarm")
        }
    }
}