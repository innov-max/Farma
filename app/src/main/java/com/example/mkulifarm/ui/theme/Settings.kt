package com.example.mkulifarm.ui.theme

import android.os.Bundle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        .padding(16.dp)) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AccountSettingsSection()
        NotificationSettingsSection()
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
fun NotificationSettingsSection() {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Notifications", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Divider(Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Notifications")
            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
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

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
