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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // App title
        Text(
            text = "GPS Waypoint App",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Location info
        Text(
            text = currentLocation?.let {
                "Lat: %.5f | Lon: %.5f".format(it.latitude, it.longitude)
            } ?: "Waiting for GPS signal...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        // Heading
        Text(
            text = "Heading: %.1f°".format(heading),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Compass
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

        // Scale info
        Text(
            text = "Compass scale: %.0f metres".format(maxDistanceMeters),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onToggleTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (tracking) "Stop Tracking" else "Start Tracking")
            }

            Button(
                onClick = onAddWaypoint,
                enabled = tracking && currentLocation != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Waypoint")
            }

            Button(
                onClick = { showClearDialog = true },
                enabled = waypoints.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear All")
            }
        }

        // Selected waypoint details
        val selected = waypoints.find { it.id == selectedWaypointId }
        if (selected != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Selected Waypoint",
                    fontWeight = FontWeight.Bold
                )
                Text(text = selected.name)

                if (currentLocation != null) {
                    val dist = distanceMeters(
                        currentLocation.latitude, currentLocation.longitude,
                        selected.latitude, selected.longitude
                    )
                    val bearing = bearingToWaypoint(
                        currentLocation.latitude, currentLocation.longitude,
                        selected.latitude, selected.longitude
                    )
                    Text("Distance: %.1f m".format(dist))
                    Text("Direction: %.1f°".format(bearing))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Waypoints list title
        Text(
            text = "Waypoints (${waypoints.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Waypoints list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(waypoints) { wp ->
                val isSelected = wp.id == selectedWaypointId

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color(0xFFBBDEFB) else Color(0xFFF5F5F5),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectWaypoint(wp.id) }
                        .padding(10.dp)
                ) {
                    Text(
                        text = wp.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    // Confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all waypoints?") },
            text = { Text("This will permanently delete all saved waypoints.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearWaypoints()
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
