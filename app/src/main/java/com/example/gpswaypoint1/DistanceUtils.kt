package com.example.gpswaypoint1


import android.location.Location

fun distanceMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Float {
    val result = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, result)
    return result[0]
}

fun bearingToWaypoint(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Float {
    val a = Location("start").apply {
        latitude = lat1
        longitude = lon1
    }
    val b = Location("target").apply {
        latitude = lat2
        longitude = lon2
    }
    return a.bearingTo(b)
}
