package com.thewire.notification_experiment

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyBackgroundWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        for (i in 1..10) {
            println("working $i")
            Thread.sleep(1000)
        }
        return Result.success()
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