package com.example.mkulifarm.ui.theme

import Article
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.example.mkulifarm.R


import fetchFarmingNews

class Dashboard(navController: NavHostController) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the main content using Compose
        setContent {
            DashboardScreen()
        }
    }
}


@Composable

fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var articles by remember { mutableStateOf(emptyList<Article>()) }

    // Fetch articles when the DashboardScreen is first composed
    LaunchedEffect(Unit) {
        articles = fetchFarmingNews() // Make sure this returns a List<Article>
    }

    // Main layout with Scaffold for bottom navigation and content
    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F1F1))
                .padding(paddingValues)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TopBar()
            when (selectedTab) {
                0 -> MetricsSection()
                1 -> QuickActionsSection()
                2 -> TrendsSection(articles)
            }

            QuickActionsSection()
            TrendsSection(articles)
        }
    }
}


@Composable
fun TrendsSection(articles: List<Article>, isLoading: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Text("Farming News", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))

            if (isLoading) {
                // Show loading indicator while data is being fetched
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(articles) { article ->
                        ArticleItem(article)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(article: Article) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (article.urlToImage != null) {
            Image(
                painter = rememberImagePainter(data = article.urlToImage),
                contentDescription = article.title,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
            )
        }
        Text(text = article.title, fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center)
    }
}



@Composable
fun TopBar() {
    // Top bar containing logo, title, and action icons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(55.dp)
                .padding(horizontal = 10.dp)
        )
        Text(text = "Farminika", fontSize = 24.sp, color = Color.Green)
        Row {
            IconButton(onClick = { /* Refresh Data */ }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }
            IconButton(onClick = { /* Navigate to Settings */ }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    }
}
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MetricsSection() {
    // Container for displaying main metrics with a title
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Current Conditions", fontSize = 18.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        // Metrics row displaying temperature, humidity, and soil moisture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricCard(title = "Temperature", value = "25°C", lottieFile = "temp.json")
            MetricCard(title = "Humidity", value = "60%", lottieFile = "humidity.json")
            MetricCard(title = "Soil Moisture", value = "45%", lottieFile = "soil_moisture.json")
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, lottieFile: String) {
    // Card containing a metric title, value, and Lottie animation as background
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.temp))
    val progress by animateLottieCompositionAsState(composition)

    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            // Display Lottie animation in the background
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = title, fontSize = 10.sp, color = Color.Gray)
                Text(text = value, fontSize = 20.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun QuickActionsSection() {
    // Section for quick actions like "Water Plants", "Adjust Temperature", etc.
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Quick Actions", fontSize = 18.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionButton(label = "Water Plants")
            QuickActionButton(label = "Adjust Temperature")
            QuickActionButton(label = "Enable Sensors")
        }
    }
}

@Composable
fun QuickActionButton(label: String) {
    // Single action button within the Quick Actions section
    Card(
        modifier = Modifier
            .size(100.dp, 80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8BC34A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 50.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,  // Center the content within the Box
            modifier = Modifier.fillMaxSize()      // Ensure Box fills entire card space
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.Center)  // Center-align the Text explicitly
            )
        }
    }

}


@Composable

fun TrendsSection(articles: List<Article>) {
    // Card containing the title and a LazyColumn to display articles
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Text("Farming News", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))

            LazyColumn {
                items(articles) { article ->
                    Column(
                        modifier = Modifier
                            .width(150.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (article.urlToImage != null) {
                            Image(
                                painter = rememberImagePainter(data = article.urlToImage),
                                contentDescription = article.title,
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                            )
                        }
                        Text(text = article.title, fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}







@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.DarkGray
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Refresh, contentDescription = "Actions") },
            label = { Text("Actions") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Trends") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    DashboardScreen()
}
