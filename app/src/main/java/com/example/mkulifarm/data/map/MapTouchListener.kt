package com.example.mkulifarm.data.map

import androidx.core.content.ContextCompat
import com.example.mkulifarm.R
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapTouchListener(
    private val mapView: MapView,
    private val points: MutableList<GeoPoint>
) : MapEventsReceiver {

    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
        onPointAdded(mapView, p, points)
        return true
    }

    override fun longPressHelper(p: GeoPoint): Boolean {
        return false
    }

    private fun onPointAdded(mapView: MapView, point: GeoPoint, points: MutableList<GeoPoint>) {
        // Add the point to the list
        points.add(point)

        // Draw the marker
        val marker = Marker(mapView).apply {
            position = point
            icon = ContextCompat.getDrawable(mapView.context, R.drawable.marker)
        }
        mapView.overlays.add(marker)

        // Redraw the polygon
        drawPolygon(mapView, points)
    }

    private fun drawPolygon(mapView: MapView, points: List<GeoPoint>) {
        if (points.size < 3) return

        // Remove any existing polygon overlay
        mapView.overlays.removeIf { it is Polyline }

        // Draw the new polygon
        val polyline = Polyline().apply {
            this.setPoints(points + points.first()) // Close the polygon
            outlinePaint.color = ContextCompat.getColor(mapView.context, R.color.primaryColor)
        }
        mapView.overlays.add(polyline)
        mapView.invalidate()
    }
}

