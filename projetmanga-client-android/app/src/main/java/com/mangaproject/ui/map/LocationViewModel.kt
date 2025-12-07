package com.mangaproject.screens.map

import android.annotation.SuppressLint
import android.app.Application
import android.location.LocationRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserLocation(val latitude: Double, val longitude: Double)

class LocationViewModel(app: Application) : AndroidViewModel(app) {

    private val fused = LocationServices.getFusedLocationProviderClient(app)

    private val _location = MutableStateFlow<UserLocation?>(null)
    val location: StateFlow<UserLocation?> = _location

    // Requête location en continu
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1500 // toutes les 1.5 secondes
        ).build()

        fused.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation ?: return
                    _location.value = UserLocation(loc.latitude, loc.longitude)
                }
            },
            null
        )
    }

    // Fallback : essayer une fois avec lastLocation
    @SuppressLint("MissingPermission")
    fun loadLastLocation() {
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                _location.value = UserLocation(loc.latitude, loc.longitude)
            }
        }
    }
}
