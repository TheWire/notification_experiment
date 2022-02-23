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
import com.thewire.notification_experiment.ui.theme.Notification_experimentTheme

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
                        this::notification,
                        this::addChannel
                    )
                }
            }
        }
    }

    fun notification() {
        val notificationId = 42
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.test_icon)
            .setContentTitle("My notification")
            .setContentText("This is my message")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("This is my message with more text and even more text..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
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
}



@Composable
fun Notifier(
    notify: () -> Unit,
    addChannel: () -> Unit
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
    }

}
