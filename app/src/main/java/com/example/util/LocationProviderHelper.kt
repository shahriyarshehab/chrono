package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object LocationProviderHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        if (!hasLocationPermission(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        // Try getting last known location first from available providers
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.time > bestLocation.time) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore provider error
            }
        }

        // If a recent location (within last 30 minutes) is found, return it immediately
        if (bestLocation != null && (System.currentTimeMillis() - bestLocation.time) < 30 * 60 * 1000L) {
            return Pair(bestLocation.latitude, bestLocation.longitude)
        }

        // Otherwise request a single fresh location update with 5 second timeout
        val freshLocation = withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine<Location?> { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (cont.isActive) cont.resume(location)
                    }

                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                try {
                    val provider = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        LocationManager.NETWORK_PROVIDER
                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        LocationManager.GPS_PROVIDER
                    } else {
                        null
                    }

                    if (provider != null) {
                        locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                        cont.invokeOnCancellation {
                            locationManager.removeUpdates(listener)
                        }
                    } else {
                        cont.resume(bestLocation)
                    }
                } catch (e: Exception) {
                    cont.resume(bestLocation)
                }
            }
        }

        val finalLoc = freshLocation ?: bestLocation
        return finalLoc?.let { Pair(it.latitude, it.longitude) }
    }
}
