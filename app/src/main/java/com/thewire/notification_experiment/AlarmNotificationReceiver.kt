package com.thewire.notification_experiment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("notification alarm received")
        try {
            if (intent.action == "MYNOTIFICATIONACTION") {
                val myString = intent.getStringExtra("MYSTRING")
                if(myString != null) {
                    notification(context, "alarm notification", myString, myString)
                } else {
                    println("error alarm received no string")
                }
                println("notification alarm alarm alarm")
            }
        } catch(e: Exception) {
            Log.e("ALARM", e.message ?: "Unknown error")
        }
    }
}