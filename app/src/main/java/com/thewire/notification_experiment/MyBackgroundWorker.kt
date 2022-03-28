package com.thewire.notification_experiment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class MyBackgroundWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        for (i in 1..10) {
            println("working $i")
            Thread.sleep(1000)
        }
        return Result.success()
    }
}

class MyDataWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        Thread.sleep(2000)
        return try {
            val str = inputData.getString("MYSTRING")
            val int = inputData.getInt("MYINT", -1)
            println("$int $str")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }


    }
}


class MyNotificationWorker(private val ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        println("notification worker")
        delay(10000)
        notification(
            ctx,
            "notification worker",
            "notification worker description",
            "notification worker longer bigger message"
        )
        println("notification worker done")
        return Result.success()
    }
}

class MyAlarmNotificationWorker(private val ctx: Context, params: WorkerParameters) :
    Worker(ctx, params) {
    override fun doWork(): Result {
        println("alarm notification worker")
        Thread.sleep(10000)
        val requestId = 0
        val intent = Intent(ctx, AlarmNotificationReceiver::class.java)
        intent.action = "MYNOTIFICATIONACTION"
        intent.putExtra("MYSTRING", " alarm notification worker")

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(ctx, requestId, intent, PendingIntent.FLAG_IMMUTABLE)
        val time = System.currentTimeMillis() + 10000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        println("alarm notification worker done")
        return Result.success()
    }
}