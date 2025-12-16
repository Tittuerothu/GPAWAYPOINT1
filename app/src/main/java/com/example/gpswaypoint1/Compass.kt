package com.example.gpswaypoint1


import android.location.Location
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.compareTo
import kotlin.div
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.text.toDouble

@Composable
fun Compass(
    heading: Float,
    currentLocation: Location?,
    waypoints: List<Waypoint>,
    selectedWaypointId: Int?,
    maxDistanceMeters: Float,
    onWaypointTouched: (Int) -> Unit,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .size(250.dp)
                // Tap selection
                .pointerInput(heading, currentLocation, waypoints, maxDistanceMeters) {
                    detectTapGestures { tapOffset ->
                        val loc = currentLocation ?: return@detectTapGestures

                        val w = size.width
                        val h = size.height
                        val radius = minOf(w, h) / 2f * 0.8f
                        val center = Offset(w / 2f, h / 2f)

                        val tapUnrotated = rotatePoint(tapOffset, center, heading)

                        for (wp in waypoints) {
                            val dist = distanceMeters(
                                loc.latitude, loc.longitude,
                                wp.latitude, wp.longitude
                            )

                            if (dist <= maxDistanceMeters) {
                                val bearing = bearingToWaypoint(
                                    loc.latitude, loc.longitude,
                                    wp.latitude, wp.longitude
                                )

                                val angleRad = Math.toRadians(bearing.toDouble()).toFloat()
                                val norm = (dist / maxDistanceMeters).coerceIn(0f, 1f)
                                val r = radius * norm

                                val point = Offset(
                                    x = center.x + r * sin(angleRad),
                                    y = center.y - r * cos(angleRad)
                                )

                                val dx = tapUnrotated.x - point.x
                                val dy = tapUnrotated.y - point.y
                                val d = sqrt(dx * dx + dy * dy)

                                val hitRadius = 24f
                                if (d <= hitRadius) {
                                    onWaypointTouched(wp.id)
                                    return@detectTapGestures
                                }
                            }
                        }
                    }
                }
                // Pinch zoom
                .pointerInput(maxDistanceMeters) {
                    detectTransformGestures { _, _, zoom, _ ->
                        if (zoom != 1f) {
                            val newScale = maxDistanceMeters / zoom
                            onScaleChange(newScale)
                        }
                    }
                }
        ) {
            val radius = size.minDimension / 2f * 0.8f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer circle
            drawCircle(
                color = Color.DarkGray,
                radius = radius,
                center = center,
                style = Stroke(width = 6f)
            )

            // Rotate opposite to heading
            rotate(-heading, center) {

                // Cross lines
                drawLine(
                    color = Color.LightGray,
                    start = Offset(center.x, center.y - radius),
                    end = Offset(center.x, center.y + radius),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.LightGray,
                    start = Offset(center.x - radius, center.y),
                    end = Offset(center.x + radius, center.y),
                    strokeWidth = 4f
                )

                // Direction labels
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.RED
                        textSize = 60f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }

                    drawText("N", center.x, center.y - radius + 70f, paint)

                    paint.color = android.graphics.Color.WHITE

                    drawText("S", center.x, center.y + radius - 30f, paint)
                    drawText("E", center.x + radius - 40f, center.y + 20f, paint)
                    drawText("W", center.x - radius + 40f, center.y + 20f, paint)
                }

                // Waypoint circles
                val loc = currentLocation
                if (loc != null) {
                    waypoints.forEach { wp ->
                        val dist = distanceMeters(
                            loc.latitude, loc.longitude,
                            wp.latitude, wp.longitude
                        )

                        if (dist <= maxDistanceMeters) {
                            val bearing = bearingToWaypoint(
                                loc.latitude, loc.longitude,
                                wp.latitude, wp.longitude
                            )

                            val angleRad = Math.toRadians(bearing.toDouble()).toFloat()
                            val norm = (dist / maxDistanceMeters).coerceIn(0f, 1f)
                            val r = radius * norm

                            val point = Offset(
                                x = center.x + r * sin(angleRad),
                                y = center.y - r * cos(angleRad)
                            )

                            val isSelected = wp.id == selectedWaypointId

                            drawCircle(
                                color = if (isSelected) Color.Yellow else Color.Cyan,
                                radius = if (isSelected) 14f else 10f,
                                center = point
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun rotatePoint(p: Offset, center: Offset, degrees: Float): Offset {
    val rad = Math.toRadians(degrees.toDouble())
    val cosA = cos(rad).toFloat()
    val sinA = sin(rad).toFloat()

    val dx = p.x - center.x
    val dy = p.y - center.y

    val x = dx * cosA - dy * sinA
    val y = dx * sinA + dy * cosA

    return Offset(center.x + x, center.y + y)
}
