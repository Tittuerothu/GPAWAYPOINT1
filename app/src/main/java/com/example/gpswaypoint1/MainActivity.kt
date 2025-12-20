package com.example.gpswaypoint1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.gpswaypoint1.ui.theme.GPSWAYPOINT1Theme

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                locationPermissionGranted = true
            }
        }

    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted = true
        }

        setContent {
            GPSWAYPOINT1Theme {

                val context = LocalContext.current

                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val sensorManager =
                    context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

                var tracking by remember { mutableStateOf(false) }
                var currentLocation by remember { mutableStateOf<Location?>(null) }
                var waypoints by remember { mutableStateOf(emptyList<Waypoint>()) }
                var heading by remember { mutableStateOf(0f) }
                var selectedWaypointId by remember { mutableStateOf<Int?>(null) }

                // Compass scale: 500m .. 2000m
                var maxDistanceMeters by remember { mutableStateOf(500f) }

                var orientationSensor: OrientationSensor? by remember { mutableStateOf(null) }

                // Load waypoints on startup
                LaunchedEffect(Unit) {
                    waypoints = WaypointStorage.loadWaypoints(context)
                }

                // Save waypoints whenever they change
                LaunchedEffect(waypoints) {
                    WaypointStorage.saveWaypoints(context, waypoints)

                    // Remove selection if waypoint no longer exists
                    val sel = selectedWaypointId
                    if (sel != null && waypoints.none { it.id == sel }) {
                        selectedWaypointId = null
                    }
                }

                // GPS tracking (GPS + NETWORK)
                if (tracking && locationPermissionGranted) {
                    DisposableEffect("gps") {
                        val listener = android.location.LocationListener { loc ->
                            currentLocation = loc

                            //  Auto-select previous waypoint within 10 metres
                            val selId = selectedWaypointId
                            if (selId != null && waypoints.isNotEmpty()) {
                                val index = waypoints.indexOfFirst { it.id == selId }

                                if (index > 0) { // previous waypoint exists
                                    val currentWp = waypoints[index]
                                    val distance = distanceMeters(
                                        loc.latitude, loc.longitude,
                                        currentWp.latitude, currentWp.longitude
                                    )

                                    if (distance <= 10f) {
                                        selectedWaypointId = waypoints[index - 1].id
                                    }
                                }
                            }
                        }

                        try {
                            locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                5000L,
                                0f,
                                listener
                            )
                            locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                5000L,
                                0f,
                                listener
                            )
                        } catch (_: Exception) { }

                        onDispose {
                            try {
                                locationManager.removeUpdates(listener)
                            } catch (_: Exception) { }
                        }
                    }
                }

                // Orientation sensor tracking
                DisposableEffect(tracking) {
                    if (tracking) {
                        orientationSensor = OrientationSensor(sensorManager) { deg ->
                            heading = deg
                        }.apply { start() }
                    }

                    onDispose {
                        orientationSensor?.stop()
                    }
                }

                RootScreenPlaceholder(
                    waypoints = waypoints,
                    tracking = tracking,
                    currentLocation = currentLocation,
                    heading = heading,
                    selectedWaypointId = selectedWaypointId,
                    maxDistanceMeters = maxDistanceMeters,
                    onToggleTracking = { tracking = !tracking },
                    onAddWaypoint = {
                        val loc = currentLocation
                        if (loc != null) {
                            val newId = (waypoints.maxOfOrNull { it.id } ?: 0) + 1
                            val wp = Waypoint(
                                id = newId,
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                name = "WP $newId"
                            )
                            waypoints = waypoints + wp
                            selectedWaypointId = newId
                        }
                    },
                    onClearWaypoints = {
                        waypoints = emptyList()
                        selectedWaypointId = null
                    },
                    onSelectWaypoint = { id ->
                        selectedWaypointId = id
                    },
                    onScaleChange = { newScale ->
                        maxDistanceMeters = newScale.coerceIn(500f, 2000f)
                    }
                )
            }
        }
    }
}
