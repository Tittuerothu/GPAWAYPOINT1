package com.example.gpswaypoint1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class OrientationSensor(
    private val sensorManager: SensorManager,
    private val onRotationChanged: (Float) -> Unit
) : SensorEventListener {

    private val rotationVector =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    fun start() {
        rotationVector?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)

        val azimuthRad = orientation[0]
        val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()

        // normalize to 0–360°
        onRotationChanged((azimuthDeg + 360) % 360)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
