package com.example.gpswaypoint1

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RootScreenPlaceholder(
    waypoints: List<Waypoint>,
    tracking: Boolean,
    currentLocation: Location?,
    heading: Float,
    selectedWaypointId: Int?,
    maxDistanceMeters: Float,
    onToggleTracking: () -> Unit,
    onAddWaypoint: () -> Unit,
    onClearWaypoints: () -> Unit,
    onSelectWaypoint: (Int) -> Unit,
    onScaleChange: (Float) -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "GPS Waypoint App",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = currentLocation?.let {
                "Lat: %.5f | Lon: %.5f".format(it.latitude, it.longitude)
            } ?: "Waiting for GPS...",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Heading: %.1f°".format(heading),
            style = MaterialTheme.typography.bodyMedium
        )

        // (Optional) debug: show selected ID
        // Text("DEBUG selectedWaypointId = ${selectedWaypointId ?: "none"}")

        Spacer(modifier = Modifier.height(8.dp))

        // Compass with waypoints + touch + pinch zoom
        Compass(
            heading = heading,
            currentLocation = currentLocation,
            waypoints = waypoints,
            selectedWaypointId = selectedWaypointId,
            maxDistanceMeters = maxDistanceMeters,
            onWaypointTouched = onSelectWaypoint,
            onScaleChange = onScaleChange,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Scale label (edge distance)
        Text(
            text = "Scale: edge = %.0f m".format(maxDistanceMeters),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onToggleTracking) {
                Text(if (tracking) "Stop Tracking" else "Start Tracking")
            }
            Button(
                onClick = onAddWaypoint,
                enabled = tracking && currentLocation != null
            ) {
                Text("Add Waypoint")
            }
            Button(
                onClick = { showClearDialog = true },
                enabled = waypoints.isNotEmpty()
            ) {
                Text("Clear")
            }
        }

        // Selected waypoint info
        val selected = waypoints.find { it.id == selectedWaypointId }
        if (selected != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selected: ${selected.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (currentLocation != null) {
                val dist = distanceMeters(
                    currentLocation.latitude, currentLocation.longitude,
                    selected.latitude, selected.longitude
                )
                val bearing = bearingToWaypoint(
                    currentLocation.latitude, currentLocation.longitude,
                    selected.latitude, selected.longitude
                )
                Text("Distance to waypoint: %.1f m".format(dist))
                Text("Direction to waypoint: %.1f°".format(bearing))
            } else {
                Text("Waiting for GPS fix to show distance and direction...")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Waypoints (${waypoints.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Scrollable list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(waypoints) { wp ->
                val distText = currentLocation?.let { loc ->
                    val d = distanceMeters(
                        loc.latitude, loc.longitude,
                        wp.latitude, wp.longitude
                    )
                    " – %.1f m".format(d)
                } ?: ""

                val isSelected = wp.id == selectedWaypointId

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color(0xFFCCE5FF) else Color(0xFFF0F0F0),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectWaypoint(wp.id) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${wp.name} (${wp.latitude}, ${wp.longitude})$distText",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    // Clear confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text("Clear waypoints?")
            },
            text = {
                Text("Are you sure you want to delete all waypoints? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearWaypoints()
                    }
                ) {
                    Text("Yes, clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
