package com.example.gpswaypoint1

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object WaypointStorage {

    private const val FILE_NAME = "waypoints.txt"

    fun saveWaypoints(context: Context, waypoints: List<Waypoint>) {
        val output = OutputStreamWriter(
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
        )

        output.use { writer ->
            waypoints.forEach { wp ->
                writer.write("${wp.id},${wp.latitude},${wp.longitude},${wp.name}\n")
            }
        }
    }

    fun loadWaypoints(context: Context): List<Waypoint> {
        val file = context.getFileStreamPath(FILE_NAME)
        if (file == null || !file.exists()) return emptyList()

        val list = mutableListOf<Waypoint>()

        val reader = BufferedReader(
            InputStreamReader(context.openFileInput(FILE_NAME))
        )

        reader.useLines { lines ->
            lines.forEach { line ->
                if (line.isBlank()) return@forEach   // Prevent crash on empty lines
                val parts = line.split(",")

                if (parts.size < 4) return@forEach  // Prevent crash on corrupt lines

                val id = parts[0].toIntOrNull() ?: return@forEach
                val lat = parts[1].toDoubleOrNull() ?: return@forEach
                val lon = parts[2].toDoubleOrNull() ?: return@forEach
                val name = parts[3]

                list.add(Waypoint(id, lat, lon, name))
            }
        }

        return list
    }
}
