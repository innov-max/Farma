package com.example.mkulifarm.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mkulifarm.R
import kotlinx.coroutines.delay

class Splash_screen :  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set the Compose content for the splash screen
        setContent {
            FarmMonitoringApp()
        }
    }
}

@Composable
fun FarmMonitoringApp() {
    Splashscreen()
}

@Composable

fun Splashscreen() {
    val isLoggedIn = remember { mutableStateOf(true) } // Replace with actual auth status
    val context = LocalContext.current // Get the current context

    // Delay to simulate loading time
    LaunchedEffect(Unit) {
        delay(5000L) // 5 seconds delay
        if (isLoggedIn.value) {
            // Start the Dashboard activity
            val intent = Intent(context, login::class.java)
            context.startActivity(intent)
            // Close the Splash screen to prevent navigating back to it
            (context as? ComponentActivity)?.finish()
        }
    }

    // Splash Screen UI
    SplashScreenContent()
}


@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo or branding icon
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.farm)) // Adjust with your animation file

            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            // App name or tagline
            Text(
                text = "Farminikia",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Optional loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.Green
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreenContent() {
    SplashScreenContent()
}
