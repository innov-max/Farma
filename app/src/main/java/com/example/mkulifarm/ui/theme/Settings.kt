package com.example.mkulifarm.ui.theme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat

class Settings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
    {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AccountSettingsSection()
        NotificationSettingsSection { isEnabled ->

            val message = if (isEnabled) "Notifications Enabled" else "Notifications Disabled"
            println("Notifications toggled: $isEnabled")

        }
        ThemeSettingsSection()
        PrivacySettingsSection()
        AboutAppSection()
    }
}

@Composable
fun AccountSettingsSection() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Divider(Modifier.padding(vertical = 8.dp))

        SettingsItem("Change Password", Icons.Filled.Settings) {
            // Action for password change
        }

        SettingsItem("Manage Account", Icons.Filled.AccountCircle) {
            // Action for account management
        }
    }
}

@Composable
fun NotificationSettingsSection(onNotificationToggle: (Boolean) -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current // Get the context

    // Handle Toast when state changes
    LaunchedEffect(notificationsEnabled) {
        val message = if (notificationsEnabled) "Notifications Enabled" else "Notifications Disabled"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable Notifications",
                fontSize = 16.sp,
                color = Color.Black
            )

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    onNotificationToggle(it) // Notify parent of state change
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00BCD4),
                    uncheckedThumbColor = Color.Gray,
                    checkedTrackColor = Color(0xFF8BC34A),
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}




@Composable
fun ThemeSettingsSection() {
    var darkModeEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Appearance", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Divider(Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode")
            Switch(checked = darkModeEnabled, onCheckedChange = { darkModeEnabled = it })
        }
    }
}

@Composable
fun PrivacySettingsSection() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Privacy", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Divider(Modifier.padding(vertical = 8.dp))

        SettingsItem("Data Usage", Icons.Filled.Settings) {
            // Action for data usage settings
        }

        SettingsItem("Clear Cache", Icons.Filled.Delete) {
            // Action to clear cache
        }
    }
}

@Composable
fun AboutAppSection() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "About", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Divider(Modifier.padding(vertical = 8.dp))

        SettingsItem("Version", Icons.Filled.Info) {
            // Display app version information
        }

        SettingsItem("Licenses", Icons.Filled.Settings) {
            // Display app licenses and acknowledgements
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(text = title, fontSize = 16.sp)
    }
}
fun showNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create notification channel for Android 8.0 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "settings",
            "Notification Toggle",
            NotificationManager.IMPORTANCE_HIGH // Make it more prominent
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Create notification content based on LED action
    val notificationContent = "Notification toggled"

    // Create notification with sound, vibration, and priority for visibility
    val notification = NotificationCompat.Builder(context, "settings")
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle("Sensor Status")
        .setContentText(notificationContent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL) // Add sound and vibration
        .setAutoCancel(true) // Dismiss notification when clicked
        .setLights(0xFF00FF00.toInt(), 1000, 1000) // Optional: LED lights on notification if supported
        .build()

    // Show notification
    notificationManager.notify(0, notification)
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
