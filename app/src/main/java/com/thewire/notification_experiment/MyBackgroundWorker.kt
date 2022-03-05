package com.thewire.notification_experiment

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

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
        try {
            val str = inputData.getString("MYSTRING")
            val int = inputData.getInt("MYINT", -1)
            println("$int $str")
            return Result.success()
        } catch(e: Exception) {
            return Result.failure()
        }


    }
}


class MyNotificationWorker(private val ctx: Context, params: WorkerParameters) :
    Worker(ctx, params) {
    override fun doWork(): Result {
        println("notification worker")
        Thread.sleep(10000)
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