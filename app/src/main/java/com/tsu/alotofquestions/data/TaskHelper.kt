package com.tsu.alotofquestions.data

import android.location.Location
import com.tsu.alotofquestions.data.model.Answer
import com.tsu.alotofquestions.data.model.Task
import kotlin.math.sin

object TaskHelper {

    val FIRST_TASK_ANSWER = Answer("")

    var currentTask: Task? = null
    var currentLocation: Location? = null

    fun compareLocationWithTask(): Boolean {
        if (currentTask != null && currentLocation != null) {
            var distance = FloatArray(4)
            Location.distanceBetween(currentTask!!.lat!!, currentTask!!.lon!!,
                currentLocation!!.latitude, currentLocation!!.longitude, distance)
            return (distance[0] < 50)
        }
        return false
    }

    fun distance(lat_a: Float, lng_a: Float, lat_b: Float, lng_b: Float): Float {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians((lat_b - lat_a).toDouble())
        val lngDiff = Math.toRadians((lng_b - lng_a).toDouble())
        val a = sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a.toDouble())) * Math.cos(Math.toRadians(lat_b.toDouble())) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = earthRadius * c
        val meterConversion = 1609
        return (distance * meterConversion.toFloat()).toFloat()
    }
}