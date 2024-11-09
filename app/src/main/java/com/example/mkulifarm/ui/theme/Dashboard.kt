package com.example.mkulifarm.ui.theme

import Article
import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.example.mkulifarm.R
import com.example.mkulifarm.data.NewsResponse
import com.example.mkulifarm.data.RetrofitClient
import com.example.mkulifarm.data.RetrofitInstance
import com.example.mkulifarm.data.SoilData.SoilData

import com.example.mkulifarm.data.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
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
                .size(60.dp)
                .clip(CircleShape)
                .padding(horizontal = 5.dp)

        )
        Column(
            modifier = Modifier.padding(8.dp),

            ) {

            Text(
                text = "Farminikia", fontSize = 34.sp,
                color = Color.Black,
            )
            val locationName by remember { mutableStateOf("...") }
            Text(
                text = locationName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
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

    val database = FirebaseDatabase.getInstance()
    val soilRef = database.getReference("soilData") // Reference to your soil data in Firebase

    // State variables for storing soil data
    var soilTemp by remember { mutableStateOf<String>("Loading...") }
    var humidity by remember { mutableStateOf<String>("Loading...") }
    var soilMoisture by remember { mutableStateOf<String>("Loading...") }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {

        soilRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val soilData = snapshot.getValue(SoilData::class.java)
                if (soilData != null) {
                    // Update state with values from Firebase
                    soilTemp = soilData.soilTemperature?.toString() ?: "N/A"
                    humidity = soilData.soilMoisture?.toString() ?: "N/A"
                    soilMoisture = soilData.soilHumidity?.toString() ?: "N/A"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible error
                soilTemp = "Error"
                humidity = "Error"
                soilMoisture = "Error"
            }
        })
    }

    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Current Conditions", fontSize = 18.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display the data fetched from Firebase
            MetricCard(
                title = "Soil Temp",
                value = soilTemp,
                lottieFile = R.raw.temp.toString()
            )
            MetricCard(
                title = "Humidity",
                value = humidity,
                lottieFile = R.raw.water.toString()
            )
            MetricCard(
                title = "Soil Moisture",
                value = soilMoisture,
                lottieFile = "soil_moisture.json"
            )
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, lottieFile: String) {
    // Card containing a metric title, value, and Lottie animation as background
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.water))
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


@SuppressLint("UnrememberedMutableState")
@Composable
fun QuickActionButton(
    label: String,
    animationRes: Int,
    onClick: () -> Unit
) {
    var ledStatus by remember { mutableStateOf(false) }
    val ledStates = mutableStateMapOf(1 to false, 2 to false, 3 to false, 4 to false)

    val context = LocalContext.current

// Determine the card color based on LED state
    val cardColor = if (ledStatus) Color(0xFFFF5722) else Color(0xFF8BC34A)

    Card(
        modifier = Modifier
            .size(100.dp, 80.dp)
            .padding(4.dp)
            .clickable {
                // Toggle the LED status and send request based on label
                when (label) {
                    "Enable Sensors" -> {
                        val currentState = ledStates[1] ?: false
                        toggleLED(if (currentState) "off" else "on", 1) // Toggle LED 1
                        ledStates[1] = !currentState
                        ledStatus = ledStates[1] ?: false
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                        Toast
                            .makeText(
                                context,
                                "Sensor ${if (ledStatus) "armed" else "disarmed"}",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }

                    "Water Plants" -> {
                        val currentState = ledStates[2] ?: false
                        toggleLED(if (currentState) "off" else "on", 2) // Toggle LED 2
                        ledStates[2] = !currentState
                        ledStatus = ledStates[2] ?: false
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                        Toast
                            .makeText(
                                context,
                                "Water pump ${if (ledStatus) "Armed" else "Disarmed"}",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }

                    "Adjust Temperature" -> {
                        val currentState = ledStates[3] ?: false
                        toggleLED(if (currentState) "off" else "on", 3) // Toggle LED 3
                        ledStates[3] = !currentState
                        ledStatus = ledStates[3] ?: false
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                        Toast
                            .makeText(
                                context,
                                "Temperature regulator ${if (ledStatus) "Armed" else "Disarmed"}",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }

                    "Activate Alarm" -> {
                        val currentState = ledStates[4] ?: false
                        toggleLED(if (currentState) "off" else "on", 4) // Toggle LED 4
                        ledStates[4] = !currentState
                        ledStatus = ledStates[4] ?: false
                        showLEDNotification(context, if (ledStatus) "on" else "off")
                        Toast
                            .makeText(
                                context,
                                "LED 4 ${if (ledStatus) "On" else "Off"}",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
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
    val location by viewModel.location.observeAsState()

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

                Spacer(modifier = Modifier.height(8.dp))

                // Weather Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weatherData?.let {
                        WeatherDetailItem(value = "${it.soilTemperature}°F", label = "Soil Temp")
                        WeatherDetailItem(value = "${it.humidity}%", label = "Humidity")
                        WeatherDetailItem(value = "${it.windSpeed} m/s", label = "Wind")
                        WeatherDetailItem(value = "${it.precipitation} mm", label = "Precipitation")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sunrise and Sunset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weatherData?.let {
                        Text(
                            text = "Sunrise: ${it.sunrise}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                        Text(
                            text = "Sunset: ${it.sunset}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun WeatherDetailItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(date)
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



fun toggleLED(action: String, ledNumber: Int) {
    val api = RetrofitClient.instance
    val call = when (ledNumber) {
        1 -> api.toggleLED1(action)
        2 -> api.toggleLED2(action)
        3 -> api.toggleLED3(action)
        else -> null
    }

    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("ToggleLED", "LED $ledNumber toggled $action successfully")
            } else {
                Log.e("ToggleLED", "Error response for LED $ledNumber: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("ToggleLED", "Failed to send request for LED $ledNumber: ${t.message}")
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
            NotificationManager.IMPORTANCE_HIGH // Make it more prominent
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Create notification content based on LED action
    val notificationContent = if (action == "on") {
        "Sensors Armed"
    } else {
        "Sensors not Armed"
    }

    // Create notification with sound, vibration, and priority for visibility
    val notification = NotificationCompat.Builder(context, "led_toggle_channel")
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
fun PreviewDashboardScreen() {
    DashboardScreen(apiKey = "edd9318bcadb4fa295854bb3f810b053")


}

