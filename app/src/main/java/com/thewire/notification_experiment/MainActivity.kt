package com.thewire.notification_experiment

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thewire.notification_experiment.ui.theme.Notification_experimentTheme
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "MY_NOTIFICATION_CHANNEL"

const val ALARM_TAG = "ALARM"

class MainActivity : ComponentActivity() {

    private val alarms = arrayListOf<Alarm>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Notification_experimentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Notifier(
                        notify = {
                            notification(
                                this,
                                "MyNotification",
                                "My description",
                                "My longer message"
                            )
                        },
                        alarm = this::alarm,
                        cancelAlarm = this::cancelAlarm,
                        cancelAlarms = this::cancelAllAlarms,
                        notificationAlarm = this::notificationAlarm,
                        addChannel = this::addChannel,
                        createWorker = this::createWorkManager,
                        createPeriodWorkManager = this::createScheduledWorkManager,
                        notificationWorker = this::notificationWorker,
                        workerNotificationAlarm = this::workerNotificationAlarm,
                        cancelPeriodWorkManager = this::cancelScheduleWorkManager,
                    )
                }
            }
        }
    }

    private fun workerNotificationAlarm(tag: String) {
        addChannel()
        val workManager = WorkManager.getInstance(application)

        val workRequest = OneTimeWorkRequestBuilder<MyAlarmNotificationWorker>()
            .addTag(tag)
            .build()
        workManager.enqueue(workRequest)
    }

    private fun notificationAlarm() {
        val requestId = 0
        val action = "MYNOTIFICATIONACTION"
        val stringName = "MYSTRING"
        val string = " this is my string"
        val intent = Intent(this, AlarmNotificationReceiver::class.java)
        intent.action = action
        intent.putExtra(stringName, string)
        alarms.add(Alarm(requestId, requestId))
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                requestId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        val time = System.currentTimeMillis() + 10000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun alarm(id: Int, time: Long) {
        val newTime = System.currentTimeMillis() + time
        try {
            val intent = Intent(this, AlarmReceiver::class.java)
            val action = "MYACTION"
            val stringName = "MYSTRING"
            val string = "this is my string $id $newTime"
            intent.action = action
            intent.putExtra(stringName, string)
            val requestId = Pair(id, newTime).hashCode()
            alarms.add(Alarm(id, requestId))
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent =
                PendingIntent.getBroadcast(applicationContext, requestId.hashCode(), intent, 0)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, newTime, pendingIntent)
            Log.i(ALARM_TAG, "alarm $id with requestId $requestId launched for $newTime")
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        } catch (e: Exception) {
            Log.e(ALARM_TAG, e.message ?: "Unknown error")
        }

    }

    private fun cancelAlarm(id: Int) {
        alarms.forEach { alarm ->
            if (alarm.id == id) {
                val intent = Intent(this, AlarmReceiver::class.java)
                intent.action = "MYACTION"
                val alarmManager =
                    applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        applicationContext,
                        alarm.requestId,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                if (pendingIntent == null) {
                    Log.e(
                        ALARM_TAG,
                        "error on cancelling alarm $id with requestId ${alarm.requestId} pending Intent null"
                    )
                } else {
                    Log.i(ALARM_TAG, "alarm $id canceled with requestId ${alarm.requestId}")
                    pendingIntent.cancel()
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        alarms.removeIf { it.id == id }
    }

    private fun cancelAllAlarms() {
        alarms.forEach { alarm ->
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.action = "MYACTION"
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent =
                PendingIntent.getBroadcast(
                    applicationContext,
                    alarm.requestId,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            if (pendingIntent == null) {
                Log.e(
                    ALARM_TAG,
                    "error on cancelling alarm ${alarm.id} with requestId ${alarm.requestId} pending Intent null"
                )
            } else {
                Log.i(ALARM_TAG, "alarm ${alarm.id} with requestId ${alarm.requestId} canceled")
                pendingIntent.cancel()
                alarmManager.cancel(pendingIntent)
            }
        }
        alarms.clear()
    }

    private fun addChannel() {
        val name = "my channel"
        val descriptionText = "my experimental channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createWorkManager() {
        val workManager = WorkManager.getInstance(application)
        val data = Data.Builder()
            .putString("MYSTRING", "this is a string")
            .putInt("MYINT", 42)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<MyDataWorker>()
            .addTag("TEST_TAG")
            .setInputData(data)
            .build()
        workManager.beginWith(workRequest).enqueue()
    }

    private fun createScheduledWorkManager(tag: String) {
        val workManager = WorkManager.getInstance(application)

        val workRequest = PeriodicWorkRequestBuilder<MyBackgroundWorker>(15, TimeUnit.MINUTES)
            .addTag(tag)
            .build()
        workManager.enqueue(workRequest)
    }

    private fun cancelScheduleWorkManager(tag: String) {
        WorkManager.getInstance(application).cancelAllWorkByTag(tag)
    }

    private fun notificationWorker(tag: String) {
        addChannel()
        val workManager = WorkManager.getInstance(application)

        val workRequest = PeriodicWorkRequestBuilder<MyNotificationWorker>(15, TimeUnit.MINUTES)
            .addTag(tag)
            .build()
        workManager.enqueue(workRequest)
    }
}


@Composable
fun Notifier(
    notify: () -> Unit,
    alarm: (Int, Long) -> Unit,
    cancelAlarm: (Int) -> Unit,
    cancelAlarms: () -> Unit,
    notificationAlarm: () -> Unit,
    addChannel: () -> Unit,
    createWorker: () -> Unit,
    createPeriodWorkManager: (String) -> Unit,
    notificationWorker: (String) -> Unit,
    workerNotificationAlarm: (String) -> Unit,
    cancelPeriodWorkManager: (String) -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Button(
            onClick = addChannel
        ) {
            Text("Add Channel")
        }
        Button(
            onClick = notify
        ) {
            Text("Notification")
        }
        Button(
            onClick = createWorker
        ) {
            Text("Create one time Worker")
        }
        Button(
            onClick = { createPeriodWorkManager("TEST_TAG2") }
        ) {
            Text("Create periodic Worker")
        }
        Button(
            onClick = { cancelPeriodWorkManager("TEST_TAG2") }
        ) {
            Text("Cancel periodic Worker")
        }
        Button(
            onClick = { createPeriodWorkManager("TEST_TAG3") }
        ) {
            Text("Create periodic Worker 3")
        }
        Button(
            onClick = { cancelPeriodWorkManager("TEST_TAG3") }
        ) {
            Text("Cancel periodic Worker 3")
        }
        Button(
            onClick = { notificationWorker("TEST_TAG4") }
        ) {
            Text("Create notification Worker")
        }
        Button(
            onClick = { cancelPeriodWorkManager("TEST_TAG4") }
        ) {
            Text("Cancel notification Worker")
        }
        Button(
            onClick = { alarm(1, 1000) }
        ) {
            Text("Alarm 1")
        }
        Button(
            onClick = { cancelAlarm(1) }
        ) {
            Text("Cancel alarm 1")
        }
        Button(
            onClick = { alarm(2, 10000) }
        ) {
            Text("Alarm 2")
        }
        Button(
            onClick = { cancelAlarm(2) }
        ) {
            Text("Cancel alarm 2")
        }
        Button(
            onClick = cancelAlarms
        ) {
            Text("Cancel all alarms")
        }
        Button(
            onClick = notificationAlarm
        ) {
            Text("Notification Alarm")
        }
        Button(
            onClick = { workerNotificationAlarm("TEST_TAG4") }
        ) {
            Text("Worker Notification Alarm")
        }
    }

}
