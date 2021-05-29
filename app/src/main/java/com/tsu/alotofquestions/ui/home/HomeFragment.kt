package com.tsu.alotofquestions.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.tsu.alotofquestions.R
import com.tsu.alotofquestions.databinding.FragmentHomeBinding
import com.tsu.alotofquestions.ui.dashboard.DashboardViewModel
import kotlin.math.abs
import kotlin.math.atan2

class HomeFragment : Fragment() {

    companion object {
        private const val KEY_LAT = "KEY_LAT"
        private const val KEY_LON = "KEY_LON"

       /* fun start(context: Context, objectLat: Double, objectLon: Double) =
            context.startActivity(
                Intent(context, MainActivity::class.java)
                    .putExtra(KEY_LAT, objectLat)
                    .putExtra(KEY_LON, objectLon)
            )*/
    }

    // region compass
    // Мне лень было пихать сюда нормальынй сенсор listener, плюс гуглы там как всегда понавертели
    // что легче просто юзать депрекейтед нормальный функционал
    private val sensorManager by lazy {  context?.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager }
    private val compass by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) }
    private var currentRotationAngle = 0f

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val rotation = event?.values?.get(0) ?: 0f
            currentRotationAngle = rotation
            calculateAngle()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }
    // endregion

    // region location
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(context) }
    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    private var currentLat = 0.0
    private var currentLon = 0.0

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            currentLat = locationResult.lastLocation.latitude
            currentLon = locationResult.lastLocation.longitude

            calculateAngle()
        }
    }
    // endregion

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    /**
     * Keep in mind that:
     * @param [latitude] Lat is Y coordinate
     * @param [longitude] Lon is X coordinate
     */
    private val objectLat = 56.469528 //by lazy { intent.getDoubleExtra(KEY_LAT, 0.0) } // 56.477296
    private val objectLon = 84.947578 //by lazy { intent.getDoubleExtra(KEY_LON, 0.0) } // 84.963072

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = binding.root

        return root
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, compass, 100000)

        //todo ADD CHECK PERMISSION HERE
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun calculateAngle() {
        val angle = (atan2(
            currentLat - objectLat,
            currentLon - objectLon
        ).toFloat() * 180f / Math.PI).toFloat()

        val imageRotationAngle = when {
            angle in 0f..90f -> 90 - angle
            angle in 90f..180f -> 450 - angle
            angle < 0f -> 90 + abs(angle)
            else -> 0f
        }

        val actualAngle = imageRotationAngle - currentRotationAngle

        binding.navigationArrowImage.rotation = actualAngle
    }

    private fun Editable?.toDouble() =
        (if (this.isNullOrEmpty()) "0.0" else this.toString()).toDouble()

}