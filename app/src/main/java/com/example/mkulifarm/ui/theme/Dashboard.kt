package com.example.mkulifarm.ui.theme

import Article
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.example.mkulifarm.R
import com.example.mkulifarm.data.NewsResponse
import com.example.mkulifarm.data.RetrofitClient
import com.example.mkulifarm.data.RetrofitInstance
import com.example.mkulifarm.data.WeatherData
import com.example.mkulifarm.data.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale



class Dashboard : ComponentActivity() {



     val apiKey = "edd9318bcadb4fa295854bb3f810b053"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setContent {
            DashboardScreen(apiKey)
        }
    }
    fun getCurrentLocation(onLocationRetrieved: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val locationName = if (addresses?.isNotEmpty() == true) {
                    "${addresses.get(0)?.locality}, ${addresses[0].countryName}"
                } else {
                    "Unknown Location"
                }
                onLocationRetrieved(locationName)
            } else {
                onLocationRetrieved("Unable to fetch location")
            }
        }
    }

}

@Composable

fun DashboardScreen(apiKey: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var articles by remember { mutableStateOf(emptyList<Article>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }


    // Fetch articles when the DashboardScreen is first composed

    LaunchedEffect(Unit) {
        fetchFarmingNews(apiKey) { fetchedArticles, error ->
            articles = fetchedArticles ?: emptyList()
            Log.d("DashboardScreen", "Fetched ${articles.size} articles")
            isLoading = false
            errorMessage = error ?: ""
        }
    }



    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab)

        { selectedTab = it }
        }
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
                2 -> TrendsSection(articles, isLoading, errorMessage)
            }
            QuickActionsSection()
            TrendsSection(articles, isLoading, errorMessage)
            WeatherScreen()



        }
    }
}

@Composable
fun TopBar() {
    val context = LocalContext.current
    // Top bar containing logo, title, and action icons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .padding(horizontal = 10.dp)

        )
        Column(
            modifier = Modifier.padding(8.dp),

            ) {

            Text(
                text = "Farmacare", fontSize = 34.sp,
                color = Color.Black,
            )
            /*val locationName by remember { mutableStateOf("...") }
            Text(
                text = locationName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )*/
        }

        Column(
            modifier = Modifier.padding(8.dp),

        ){
        Row {
            IconButton(onClick = { /* Refresh Data */ }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")


            }
            IconButton(onClick = { /* Navigate to Settings */

                navigateTo(context, Settings::class.java)



            }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }




            }


        }

    }
}

@Composable
fun MetricsSection() {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
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
            MetricCard(title = "Temperature", value = "25°C", lottieFile = R.raw.temp.toString())
            MetricCard(title = "Humidity", value = "60%", lottieFile = R.raw.water.toString())
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
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
            QuickActionButton(
                label = "Water Plants",
                animationRes = R.raw.water
            ) {
                

                // Add action for Water Plants
            }

            QuickActionButton(
                label = "Adjust Temperature",
                animationRes = R.raw.temp
            ) {
                // Add action for Adjust Temperature
            }

            QuickActionButton(
                label = "Enable Sensors",
                animationRes = R.raw.water

            ) {
                // Add action for Enable Sensors
            }
        }
    }
}


@Composable
fun QuickActionButton(
    label: String,
    animationRes: Int,
    onClick: () -> Unit
) {
    var ledStatus by remember { mutableStateOf(false) }
    val cardColor = if (ledStatus) Color(0xFFFF5722) else Color(0xFF8BC34A)
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .size(100.dp, 80.dp)
            .padding(4.dp)
            .clickable {
                // Toggle the LED status and send request based on label
                when (label) {
                    "Enable Sensors" -> {
                        toggleLED(if (ledStatus) "off" else "on") // Toggle LED state
                        ledStatus = !ledStatus // Update state after the request
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                    }
                    "Water Plants" -> {
                        toggleLED(if (ledStatus) "off" else "on") // Toggle LED state
                        ledStatus = !ledStatus // Update state after the request
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                    }
                    // Add more actions as necessary
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor), // Set the card color based on LED status
        elevation = CardDefaults.cardElevation(defaultElevation = 50.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun TrendsSection(articles: List<Article>, isLoading: Boolean, errorMessage: String) {
    val lazyListState = rememberLazyListState() // State for LazyRow
    var currentIndex by remember { mutableStateOf(0) } // Track current index for scaling effect

    // Change the focused article every 2 seconds
    LaunchedEffect(articles) {
        while (articles.isNotEmpty()) {
            delay(20000)
            currentIndex = (currentIndex + 1) % articles.size
            // Scroll to the next item
            lazyListState.animateScrollToItem(currentIndex) // Scroll to the current index
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Text("Farming News", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))

            if (isLoading) {
                // Show loading indicator while data is being fetched
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                // Show error message
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
            } else if (articles.isEmpty()) {
                Text("No news available", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                LazyRow(
                    state = lazyListState, // Set the state here
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(articles) { index, article ->
                        ArticleItem(article, isFocused = index == currentIndex)
                    }
                }
            }
        }
    }
}


@Composable
fun ArticleItem(article: Article, isFocused: Boolean) {
    // Animate scale of focused item
    val scale by animateFloatAsState(if (isFocused) 1.1f else 1.0f, label = "") // Scale up if focused

    Column(
        modifier = Modifier
            .width(150.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (article.imageUrl != null) {
            val imagePainter = rememberImagePainter(
                data = article.imageUrl,
                builder = {
                    placeholder(R.drawable.profile)
                    error(R.drawable.error) 
                }
            )
            Image(
                painter = imagePainter,
                contentDescription = article.title,
                modifier = Modifier
                    .size(130.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

        }
        Text(
            text = article.title,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


fun fetchFarmingNews(apiKey: String, callback: (List<Article>?, String?) -> Unit) {
    RetrofitInstance.api.getEverything("farming", apiKey).enqueue(object : Callback<NewsResponse> {
        override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
            if (response.isSuccessful) {


                val articles = response.body()?.articles?.map { networkArticle ->
                    // Map each network article to the UI-specific Article model
                    Article(
                        title = networkArticle.title,
                        description = networkArticle.description,
                        url = networkArticle.url,
                        imageUrl = networkArticle.urlToImage
                    )
                } ?: emptyList()
                callback(articles, null)  // Pass mapped articles if successful
            } else {
                callback(null, "Failed to fetch news: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
            callback(null, "API call failed: ${t.message}")
        }
    })


}
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = WeatherViewModel()) {
    val weatherData by viewModel.weatherData.observeAsState()

    if (weatherData == null) {
        // Display loading or error message when weatherData is null
        Text(text = "No weather data available", color = Color.Red)
    } else {

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 50.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Weather Icon and Temperature
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cloud), // Replace with actual weather icon
                        contentDescription = "Weather Icon",
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    weatherData?.let {
                        Text(
                            text = "${weatherData?.temperature ?: "--"}°C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                    }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Weather Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weatherData?.let {
                        WeatherDetailItem(value = "${it.soilTemperature}°C", label = "Soil Temp")
                        WeatherDetailItem(value = "${it.humidity}%", label = "Humidity")
                        WeatherDetailItem(value = "${it.windSpeed} m/s", label = "Wind")
                        WeatherDetailItem(value = "${it.precipitation} mm", label = "Precipitation")
                        Log.d("WeatherScreen", "Weather data: $weatherData")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sunrise and Sunset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                        Text(
                            text = "${weatherData?.sunrise ?: "--"}°C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                    }
                        Text(
                            text = weatherData?.sunset ?: "--",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )

                    }

                    }
                }





@Composable
fun WeatherDetailItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF424242)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
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
fun navigateTo(context: Context, targetActivity: Class<*>) {
    val intent = Intent(context, targetActivity)
    context.startActivity(intent)
}



fun toggleLED(action: String) {
    val api = RetrofitClient.instance
    val call = api.toggleLED(action)



    call.enqueue(object : retrofit2.Callback<Void> {
        override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
            if (response.isSuccessful) {
                Log.d("QuickActionButton", "LED toggled successfully")

            } else {
                Log.e("QuickActionButton", "Error response: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("QuickActionButton", "Failed to send request: ${t.message}")
        }
    })
}
fun showLEDNotification(context: Context, action: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create notification channel for Android 8.0 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "led_toggle_channel",
            "Sensor arm Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Create notification content based on LED action
    val notificationContent = if (action == "on") {
        "Sensors Armed"
    } else {
        "LED has been turned OFF"
    }

    // Create notification
    val notification = NotificationCompat.Builder(context, "led_toggle_channel")
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle("sensor Status")
        .setContentText(notificationContent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // Show notification
    notificationManager.notify(0, notification)
}








@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    DashboardScreen(apiKey = "edd9318bcadb4fa295854bb3f810b053")
}

