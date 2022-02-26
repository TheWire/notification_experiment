package com.thewire.notification_experiment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
                        { notification(
                            this,
                            "MyNotification",
                            "My description",
                            "My longer message"
                        ) },
                        this::addChannel,
                        this::createWorkManager,
                        this::createScheduledWorkManager,
                        this::notificationWorker,
                        this::cancelScheduleWorkManager,
                    )
                }
            }
        }
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

        val workRequest = OneTimeWorkRequestBuilder<MyBackgroundWorker>()
            .addTag("TEST_TAG")
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
    }

}
