package com.thewire.notification_experiment

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thewire.notification_experiment.ui.theme.Notification_experimentTheme
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "MY_NOTIFICATION_CHANNEL"

class MainActivity : ComponentActivity() {
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
                        notify = { notification(
                            this,
                            "MyNotification",
                            "My description",
                            "My longer message"
                        ) },
                        alarm = this::alarm,
                        notificationAlarm = this::notificationAlarm,
                        addChannel = this::addChannel,
                        createWorker = this::createWorkManager,
                        createPeriodWorkManager = this::createScheduledWorkManager,
                        notificationWorker = this::notificationWorker,
                         cancelPeriodWorkManager =  this::cancelScheduleWorkManager,
                    )
                }
            }
        }
    }

    fun notificationAlarm() {
        val requestId = 0
        val intent = Intent(this, AlarmNotificationReceiver::class.java)
        intent.action = "MYNOTIFICATIONACTION"
        intent.putExtra("MYSTRING", " this is my string")

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, requestId, intent, PendingIntent.FLAG_IMMUTABLE)
        val time = System.currentTimeMillis() + 10000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    fun alarm() {
        val requestId = 0
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "MYACTION"
        intent.putExtra("MYSTRING", " this is my string")

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, requestId, intent, PendingIntent.FLAG_IMMUTABLE)
        val time = System.currentTimeMillis() + 10000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    fun addChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    fun createWorkManager() {
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

    fun createScheduledWorkManager(tag: String) {
        val workManager = WorkManager.getInstance(application)

        val workRequest = PeriodicWorkRequestBuilder<MyBackgroundWorker>(15, TimeUnit.MINUTES)
            .addTag(tag)
            .build()
        workManager.enqueue(workRequest)
    }

    fun cancelScheduleWorkManager(tag: String) {
        WorkManager.getInstance(application).cancelAllWorkByTag(tag)
    }

    fun notificationWorker(tag: String) {
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
    alarm: () -> Unit,
    notificationAlarm: () -> Unit,
    addChannel: () -> Unit,
    createWorker: () -> Unit,
    createPeriodWorkManager: (String) -> Unit,
    notificationWorker: (String) -> Unit,
    cancelPeriodWorkManager: (String) -> Unit,
) {
    Column() {
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
            onClick = alarm
        ) {
            Text("Alarm")
        }
        Button(
            onClick = notificationAlarm
        ) {
            Text("Notification Alarm")
        }
    }

}
