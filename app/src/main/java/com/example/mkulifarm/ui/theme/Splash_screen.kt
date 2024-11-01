package com.example.mkulifarm.ui.theme

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { Splashscreen(navController) }
        composable("login") { Login(navController) }
        composable("dashboard") { Dashboard(navController) }
    }
}

@Composable
fun Splashscreen(navController: androidx.navigation.NavController) {
    val isLoggedIn = remember { mutableStateOf(false) } // Replace with actual auth status

    // Delay to simulate loading time
    LaunchedEffect(Unit) {
        delay(5000L) // 5 seconds delay
        // Replace with actual authentication check logic
        if (isLoggedIn.value) {
            navController.navigate("dashboard") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("dashboard") {
                popUpTo("splash") { inclusive = true }
            }
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
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.temp)) // Adjust with your animation file

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
