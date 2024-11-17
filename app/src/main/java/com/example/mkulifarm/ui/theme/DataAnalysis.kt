package com.example.mkulifarm.ui.theme

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mkulifarm.data.MetricData
import com.example.mkulifarm.data.MetricsAnalysis.MetricsViewModel
import com.example.mkulifarm.data.MetricsAnalysis.MockMetricsViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow

class DataAnalysis : AppCompatActivity() {

    private val metricsViewModel: MetricsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        setContent {
            FarmAnalysisScreen(viewModel = metricsViewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAnalysisScreen(viewModel: MetricsViewModel) {
    val metrics by viewModel.metrics.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                {
                    Text(
                        text = "Farm Data Analysis",
                        style = TextStyle(
                            fontSize = 24.sp,  // Set your desired font size
                            fontWeight = FontWeight.Bold,  // Set the font weight
                            color = MaterialTheme.colorScheme.primary  // Set text color
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Farm Data Analysis",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (metrics.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Bar Chart
                MyBarChart(metrics)

                Spacer(modifier = Modifier.height(24.dp))

                // Insights Section
                InsightsSection(metrics)
            }
        }
    }
}


@Composable
fun MyBarChart(metrics: List<MetricData>) {
    // Create a list of BarEntry from metrics data
    val entries = metrics.mapIndexed { index, metric ->
        BarEntry(index.toFloat(), metric.value)
    }

    // Create a BarDataSet from the entries
    val dataSet = BarDataSet(entries, "Farm Metrics").apply {
        val colors = listOf(Color.Blue, Color.Green, Color.Yellow)
// Customize the bar colors
    }

    // Create the BarData object with the dataSet
    val barData = BarData(dataSet)

    // Using AndroidView to embed the MPAndroidChart BarChart in Jetpack Compose
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                this.data = barData
                this.invalidate() // Refresh the chart
            }
        }
    )
}

@Composable
fun InsightsSection(metrics: List<MetricData>) {
    Column {
        Text(
            text = "Insights",
            style = MaterialTheme.typography.titleLarge, // use titleLarge for headings
            modifier = Modifier.padding(bottom = 16.dp)
        )
        metrics.forEach { metric ->
            Text(
                text = "${metric.name}: ${metric.value} ${metric.unit}",
                style = MaterialTheme.typography.bodyLarge, // use bodyLarge for body text
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

}
@Preview(showBackground = true)
@Composable
fun PreviewFarmAnalysisScreen() {
    val viewModel = MockMetricsViewModel()
    FarmAnalysisScreen(viewModel = viewModel)
}






