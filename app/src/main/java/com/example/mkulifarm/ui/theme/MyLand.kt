package com.example.mkulifarm.ui.theme

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mkulifarm.R
import com.example.mkulifarm.data.map.FarmAreaEntity
import com.example.mkulifarm.data.map.FarmAreaViewModel
import com.example.mkulifarm.data.map.MapTouchListener

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

import org.osmdroid.views.overlay.Polygon
import java.io.File
import kotlin.math.pow


class MyLand : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Get the application context
        val ctx = applicationContext

        // Load osmdroid configuration
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        // Setup custom cache directory if you want to use a specific directory for tiles
        val tileCacheDir = File(ctx.getExternalFilesDir(null), "osmdroid_tiles")
        if (!tileCacheDir.exists()) {
            tileCacheDir.mkdirs()
        }

        // Configure the tile source and the cache location
        Configuration.getInstance().osmdroidTileCache = tileCacheDir


        setContent {
            SelectAreaScreen()
    }
}

@Composable
fun SelectAreaScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for polygon points and area
    val polygonPoints = remember { mutableStateListOf<GeoPoint>() }
    var area by remember { mutableStateOf(0.0) }

    // State for displaying mapped area details
    var mappedAreaDetails by remember { mutableStateOf<List<GeoPoint>?>(null) }



    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My farm", style = MaterialTheme.typography.headlineMedium)


        Spacer(modifier = Modifier.height(16.dp))

        // Map with selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            OpenStreetMapViewWithSelection(
                polygonPoints = polygonPoints,
                onAreaCalculated = { computedArea -> area = computedArea }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { polygonPoints.removeLastOrNull() },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(10.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                        ),
                        shape = MaterialTheme.shapes.medium
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                    contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                )
            ){
                Text("Undo Last Point")
            }
            Button(onClick = { mappedAreaDetails = polygonPoints.toList() },

                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(10.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                        ),
                        shape = MaterialTheme.shapes.medium
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                    contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                )
            )
                 {
                Text("Finalize Mapping")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display mapped area details
        if (mappedAreaDetails != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Use AreaDetailsCard to show the calculated area
                AreaDetailsCard(area = area)

                // Display coordinates separately
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Coordinates:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        mappedAreaDetails?.forEachIndexed { index, point ->
                            Text("Point ${index + 1}: (${point.latitude}, ${point.longitude})")
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    saveAreaToRoomAndFirebase(context, mappedAreaDetails!!, area)
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Area")
                        }
                    }
                }
            }
        }

    }
}

    @SuppressLint("RememberReturnType")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenStreetMapViewWithSelection(
        polygonPoints: MutableList<GeoPoint>,
        onAreaCalculated: (Double) -> Unit
    ) {
        val context = LocalContext.current // Get the context for Geocoder
        val mapView = remember { MapView(context) }  // Initialize mapView
        var isPlacingMarkers by remember { mutableStateOf(false) }
        val threshold = 0.0001

        LaunchedEffect(Unit) {
            polygonPoints.addAll(loadPointsFromPreferences(context))
        }

// Save points when they are updated
        LaunchedEffect(polygonPoints) {
            savePointsToPreferences(context, polygonPoints)
        }

        // Initialize map settings
        LaunchedEffect(Unit) {
            mapView.apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(-1.2921, 36.8219))  // Set initial position
                setTileSource(TileSourceFactory.MAPNIK)
            }
        }

        // Initialize MapTouchListener
        val mapTouchListener = remember {
            MapTouchListener(mapView, polygonPoints)
        }

        // Add the MapTouchListener to the map
        LaunchedEffect(mapView) {
            mapView.overlays.add(MapEventsOverlay(mapTouchListener))
        }

        // Define onSearch function that takes the mapView as a parameter
        fun onSearch(query: String, mapView: MapView) {
            val geocoder = Geocoder(context)

            val addresses = geocoder.getFromLocationName(query, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val lat = addresses[0].latitude
                val lng = addresses[0].longitude
                // Update map center
                mapView.controller.setCenter(GeoPoint(lat, lng))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Farmer's Area Selection") },
                    actions = {
                        IconButton(
                            onClick = {
                                // Refresh the map or any other actions
                                refreshMap(mapView)
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Map")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Map View
                    Box(modifier = Modifier.weight(1f)) {
                        AndroidView(
                            factory = {
                                mapView.apply {
                                    setMultiTouchControls(true)
                                    setOnClickListener { e ->
                                        if (isPlacingMarkers) {
                                            val point = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                                            if (!polygonPoints.contains(point)) {
                                                polygonPoints.add(point)
                                                drawPolygon(mapView, polygonPoints)
                                                drawMarker(mapView, point)
                                                val computedArea = calculatePolygonArea(polygonPoints)
                                                onAreaCalculated(computedArea)
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Search Bar and Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var searchQuery by remember { mutableStateOf("") }

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Location") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Enter location name...") },
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                            shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
                        )

                        IconButton(onClick = { onSearch(searchQuery, mapView) }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }

                    // Control Buttons (Side by side)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                // Undo last point
                                if (polygonPoints.isNotEmpty()) {
                                    polygonPoints.removeLast()
                                    drawPolygon(mapView, polygonPoints)
                                    mapView.overlayManager.clear()
                                    polygonPoints.forEach { drawMarker(mapView, it) }
                                    val computedArea = calculatePolygonArea(polygonPoints)
                                    onAreaCalculated(computedArea)
                                }
                            },
                            modifier = Modifier
                                .weight(1f) // Equal size buttons
                                .height(50.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                                contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,  // Clear icon
                                contentDescription = "Undo",      // Icon description for accessibility
                                tint = Color.White)               // Set icon color
                        }




                        Button(
                            onClick = {
                                val points = mutableListOf<GeoPoint>()
                                clearMap(mapView, points)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                                contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,  // Clear icon
                                contentDescription = "Clear",      // Icon description for accessibility
                                tint = Color.White)
                        }

                        Button(
                            onClick = { mapView.controller.zoomOut() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                                contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.zoomin), // Custom drawable icon from res/drawable
                                contentDescription = "Zoom",      // Icon description for accessibility
                                tint = Color.White)


                        }

                        // Button to toggle marker placement
                        Button(
                            onClick = {
                                isPlacingMarkers = !isPlacingMarkers
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00BCD4), Color(0xFF8BC34A)) // Gradient background
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,  // Set the background to transparent to use custom gradient
                                contentColor = Color.White          // Text color, to ensure it stands out against the gradient
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = if (isPlacingMarkers) R.drawable.marker else R.drawable.marker_stop),  // Choose appropriate icon based on state
                                contentDescription = "Place markers", // Content description for accessibility
                                tint = Color.White // Icon color
                            )
                        }
                    }
                }
            }
        )
    }

    fun drawPolygon(mapView: MapView, points: List<GeoPoint>) {
        if (points.size < 3) return
        val polygon = Polygon().apply {
            this.points = points + points.first()
            fillColor = 0x121212FF
            strokeColor = 0xFF0000FF.toInt()
            strokeWidth = 3.0f
        }
        mapView.overlayManager.clear()
        mapView.overlayManager.add(polygon)
        mapView.invalidate()
    }

    fun drawMarker(mapView: MapView, point: GeoPoint) {
        val marker = Marker(mapView).apply {
            position = point
            icon = ContextCompat.getDrawable(mapView.context, R.drawable.marker)
        }

        // Check for duplicate points and prevent overlapping markers
        if (mapView.overlays.filterIsInstance<Marker>().none { it.position == point }) {
            mapView.overlays.add(marker)
            mapView.invalidate()
        } else {
            Toast.makeText(mapView.context, "Point already exists at this location.", Toast.LENGTH_SHORT).show()
        }
    }


    fun calculatePolygonArea(points: List<GeoPoint>): Double {
        if (points.size < 3) return 0.0 // Minimum 3 points required for a polygon

        val radiusEarth = 6371000.0 // Earth's radius in meters
        var totalAngle = 0.0

        for (i in points.indices) {
            val point1 = points[i]
            val point2 = points[(i + 1) % points.size]
            val point3 = points[(i + 2) % points.size]

            val angle = sphericalExcessAngle(point1, point2, point3)
            totalAngle += angle
        }

        // Spherical excess formula
        val area = (totalAngle - (points.size - 2) * Math.PI) * radiusEarth * radiusEarth
        return kotlin.math.abs(area) // Return absolute value in square meters
    }

    private fun sphericalExcessAngle(p1: GeoPoint, p2: GeoPoint, p3: GeoPoint): Double {
        val lat1 = Math.toRadians(p1.latitude)
        val lon1 = Math.toRadians(p1.longitude)
        val lat2 = Math.toRadians(p2.latitude)
        val lon2 = Math.toRadians(p2.longitude)
        val lat3 = Math.toRadians(p3.latitude)
        val lon3 = Math.toRadians(p3.longitude)

        // Spherical angle computation
        val a = haversineDistance(lat1, lon1, lat2, lon2)
        val b = haversineDistance(lat2, lon2, lat3, lon3)
        val c = haversineDistance(lat3, lon3, lat1, lon1)

        val s = (a + b + c) / 2
        val tanE = Math.sqrt(Math.tan(s / 2) * Math.tan((s - a) / 2) * Math.tan((s - b) / 2) * Math.tan((s - c) / 2))

        return 4 * Math.atan(tanE)
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        val a = Math.sin(dLat / 2).pow(2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2).pow(2)
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }


    fun refreshMap(mapView: MapView) {
        // Refresh map view, typically involves invalidating and resetting its state
        mapView.invalidate()  // Forces the map to refresh
        mapView.controller.setZoom(15.0)  // Reset zoom to default
        mapView.controller.setCenter(GeoPoint(-1.2921, 36.8219))  // Reset center
        mapView.overlayManager.clear()  // Clear all markers and overlays
    }

    fun clearMap(mapView: MapView, points: MutableList<GeoPoint>) {
        // Clear all overlays (markers and polygons)
        mapView.overlays.clear()
        points.clear()
        mapView.invalidate()

        Toast.makeText(mapView.context, "Map cleared. Draw a new area.", Toast.LENGTH_SHORT).show()
    }



    @Composable
fun AreaDetailsCard(area: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Farm Area Details", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Area: %.2f m²".format(area), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
    fun saveAreaToRoomAndFirebase(context: Context, polygonPoints: List<GeoPoint>, area: Double) {
        val coordinates = polygonPoints.map { mapOf("latitude" to it.latitude, "longitude" to it.longitude) }
        val firebaseData = mapOf("coordinates" to coordinates, "area" to area)

        // Save data to Firebase
        FirebaseDatabase.getInstance().reference.child("farmAreas").push().setValue(firebaseData)
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) "Area saved to Firebase" else "Failed to save area"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

        // Save data to Room with try-catch
        try {
            val farmAreaEntity = FarmAreaEntity(
                area = area,
                coordinates = coordinates.joinToString(";") { "${it["latitude"]},${it["longitude"]}" }
            )
            val viewModel = FarmAreaViewModel(context.applicationContext as Application)
            viewModel.insertFarmArea(farmAreaEntity)

            Toast.makeText(context, "Area saved to local database (Room)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save area to local database: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun savePointsToPreferences(context: Context, points: List<GeoPoint>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MapData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val pointsString = points.joinToString(";") { "${it.latitude},${it.longitude}" }
        editor.putString("polygon_points", pointsString)
        editor.apply()
    }
    fun loadPointsFromPreferences(context: Context): List<GeoPoint> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MapData", Context.MODE_PRIVATE)
        val pointsString = sharedPreferences.getString("polygon_points", null)

        return if (!pointsString.isNullOrEmpty()) {
            // Ensure there is at least one valid point
            pointsString.split(";").mapNotNull {
                try {
                    val (latitude, longitude) = it.split(",")
                    GeoPoint(latitude.toDouble(), longitude.toDouble())
                } catch (e: Exception) {
                    null // Skip invalid data
                }
            }
        } else {
            emptyList() // Return empty if no points were found
        }
    }





    @Preview(showBackground = true)
@Composable
fun PreviewSelectAreaScreen() {
    SelectAreaScreen()
}
}
