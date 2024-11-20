package com.example.mkulifarm.ui.theme
import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import com.example.mkulifarm.R
import com.example.mkulifarm.data.BasicDataHandling.MetricData
import com.example.mkulifarm.data.BasicDataHandling.TaskViewModel
import com.example.mkulifarm.data.BasicDataHandling.TaskViewModelFactory
import com.example.mkulifarm.data.MetricsAnalysis.MetricsViewModel
import com.example.mkulifarm.data.room_backup.FakeTaskDao
import com.example.mkulifarm.data.room_backup.Task
import com.example.mkulifarm.data.room_backup.TaskDao
import com.example.mkulifarm.data.room_backup.pushTaskToFirebase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataAnalysis : ComponentActivity() {

    val fakeTaskDao = FakeTaskDao() // Or get it from your database, depending on your implementation

    // Use the application context properly
    val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this, TaskViewModelFactory(application, fakeTaskDao))[TaskViewModel::class.java]
    }

    private val metricsViewModel: MetricsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setTheme(R.style.Theme_MKuliFarm)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        setContent {

            Column(modifier = Modifier.fillMaxSize()) {

                FarmAnalysisScreen(viewModel = metricsViewModel)
                Spacer(modifier = Modifier.height(16.dp))
                TodayActivitySection(taskViewModel = taskViewModel)

            }
        }

    }
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAnalysisScreen(viewModel: MetricsViewModel) {
    val metrics by viewModel.metrics.collectAsState(initial = emptyList())


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Farm Data Analysis",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Usage",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (metrics.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Line Chart for Weekly Trends
                WeeklyTrendChart(metrics = metrics)
                Spacer(modifier = Modifier.height(16.dp))

                // Insights Section
                InsightsSection(metrics = metrics)
            }
        }
    }
}


@Composable
fun WeeklyTrendChart(metrics: List<MetricData>) {
    // Create data points for a LineChart
    val entries = metrics.mapIndexed { index, metric ->
        Entry(index.toFloat(), metric.value)
    }
    val dataSet = LineDataSet(entries, "Weekly Metrics").apply {
        color = Color.Green.toArgb()
        lineWidth = 2f
        setCircleColor(Color.Green.toArgb())
        circleRadius = 4f
        valueTextColor = Color.Gray.toArgb()
        valueTextSize = 10f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    val lineData = LineData(dataSet)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                this.data = lineData
                this.description.isEnabled = false
                this.setDrawGridBackground(false)
                this.xAxis.apply {
                    granularity = 1f
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                }
                this.axisLeft.setDrawGridLines(false)
                this.axisRight.isEnabled = false
                this.invalidate() // Refresh the chart
            }
        }
    )
}

@Composable
fun InsightsSection(metrics: List<MetricData>) {
    val lightGreen = Color(0xFF8BC34A)

    // Column to hold the entire section
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Weekly Insights",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Use LazyRow to display cards side by side
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between cards
        ) {
            items(metrics) { metric ->
                Card(
                    modifier = Modifier
                        .width(120.dp) // Fixed width for each card
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = lightGreen)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Metric Name and Value
                        Text(
                            text = metric.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${metric.value} ${metric.unit}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Displaying advice for each metric
                        val advice = getMetricAdvice(metric)
                        Spacer(modifier = Modifier.height(8.dp)) // Add space before advice
                        Text(
                            text = advice,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

// Function to provide advice based on the metric
fun getMetricAdvice(metric: MetricData): String {
    return when (metric.name) {
        "Fertilizer Use" -> "Recommended fertilizer usage is within optimal range."
        "Water Usage" -> "Water usage is slightly high. Consider reducing irrigation."
        "Soil Health" -> "Soil health is stable, but periodic testing is advised."
        else -> "No advice available for this metric."
    }
}



@Composable
fun TodayActivitySection(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.tasks.observeAsState(emptyList())

    Column(modifier = Modifier.padding(6.dp)) {
        Text("Today's Tasks", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Divider(Modifier.padding(vertical = 4.dp))

        LazyColumn {
            items(tasks) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(task.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(task.description, fontSize = 14.sp, color = Color.Gray)
                    }
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            taskViewModel.updateTaskCompletion(task, it)
                        }
                    )
                }
            }
        }
    }
}



fun generateTasksFromSensorData(moistureLevel: Int, nutrientLevel: Int, dao: TaskDao) {
    val tasks = mutableListOf<Task>()

    if (moistureLevel < 30) {
        tasks.add(Task(title = "Water Crops", description = "Moisture level low: $moistureLevel%", time = "07:00"))
    }
    if (nutrientLevel < 50) {
        tasks.add(Task(title = "Add Fertilizer", description = "Nutrient level low: $nutrientLevel%", time = "08:00"))
    }

    tasks.forEach {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertTask(it)
            pushTaskToFirebase(it)
        }
    }
}



// Example function that uses ML to generate insights
fun getWeeklyInsights(metrics: List<MetricData>): String {
    // Placeholder logic for generating insights
    var fertilizerAdvice = "No fertilizer data"
    var waterAdvice = "No water data"
    var soilHealthAdvice = "Soil health data missing"

    metrics.forEach { metric ->
        // Simple logic based on certain metric names
        when (metric.name) {
            "Fertilizer Use" -> {
                fertilizerAdvice = "Weekly fertilizer usage is optimal"
            }
            "Water Usage" -> {
                waterAdvice = "Water usage is slightly high. Reduce watering"
            }
            "Soil Health" -> {
                soilHealthAdvice = "Soil health is good"
            }
        }
    }

    return "Fertilizer Advice: $fertilizerAdvice\nWater Usage: $waterAdvice\nSoil Health: $soilHealthAdvice"
}







@Preview(showBackground = true)
@Composable
fun PreviewFarmAnalysisScreen() {

    val context = LocalContext.current
    val fakeTaskDao = FakeTaskDao() // Initialize FakeTaskDao for the preview
    val taskViewModel = TaskViewModel(context.applicationContext as Application, fakeTaskDao)// Pass FakeTaskDao

    val previewMetrics = listOf(
        MetricData(name = "Moisture", value = 45f, unit = "%"),
        MetricData(name = "Temperature", value = 23f, unit = "°C"),
        MetricData(name = "PH Level", value = 6.5f, unit = "")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WeeklyTrendChart(metrics = previewMetrics)
        Spacer(modifier = Modifier.height(16.dp))
        InsightsSection(metrics = previewMetrics)
        TodayActivitySection(taskViewModel = taskViewModel)
    }
}