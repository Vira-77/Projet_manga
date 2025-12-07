package com.mangaproject.screens.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mangaproject.data.model.Store
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun StoreMapScreen(
    stores: List<Store>,
    vm: LocationViewModel,
    onBack: () -> Unit
) {
    val permissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val userLocation by vm.location.collectAsState()

    var mapLibre by remember { mutableStateOf<MapLibreMap?>(null) }
    var zoomLevel by remember { mutableStateOf(12.0) }

    // Pour éviter d’ajouter les markers 15 fois
    var markersAdded by remember { mutableStateOf(false) }

    // Demande permission
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    // Charger localisation quand la permission est ok
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            vm.loadLastLocation()
            vm.startLocationUpdates()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carte des magasins") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (!permissionState.status.isGranted) {
                Text(
                    "Permission localisation requise",
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Box
            }

            if (userLocation == null) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                return@Box
            }

            // ----- MAPLIBRE -----
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        onCreate(null)
                        onStart()
                        onResume()

                        getMapAsync { map ->
                            mapLibre = map

                            map.setStyle("https://demotiles.maplibre.org/style.json") {
                                // Gestes activés
                                map.uiSettings.apply {
                                    isZoomGesturesEnabled = true
                                    isScrollGesturesEnabled = true
                                    isRotateGesturesEnabled = true
                                    isTiltGesturesEnabled = true
                                }
                            }
                        }
                    }
                },
                update = {
                    val map = mapLibre
                    val loc = userLocation

                    // On attend que :
                    //  - la carte soit prête
                    //  - la localisation soit dispo
                    //  - on n’ait pas déjà posé les markers
                    if (map != null && loc != null && !markersAdded) {
                        markersAdded = true

                        val userLatLng = LatLng(loc.latitude, loc.longitude)

                        // Marqueur utilisateur
                        map.addMarker(
                            MarkerOptions()
                                .position(userLatLng)
                                .title("Vous êtes ici")
                        )

                        // Marqueurs magasins
                        stores.forEach { store ->
                            map.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(
                                            store.position.coordinates[1], // lat
                                            store.position.coordinates[0]  // lon
                                        )
                                    )
                                    .title(store.nom)
                                    .snippet(store.adresse ?: "")
                            )
                        }

                        // On centre sur l'utilisateur au départ
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                userLatLng,
                                zoomLevel.toFloat().toDouble()
                            )
                        )
                    }
                }
            )

            // ----- ZOOM + -----
            FloatingActionButton(
                onClick = {
                    zoomLevel += 1.0
                    mapLibre?.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel))
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 80.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Zoom +")
            }

            // ----- ZOOM – -----
            FloatingActionButton(
                onClick = {
                    zoomLevel -= 1.0
                    mapLibre?.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel))
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Zoom -")
            }

            // ----- RECENTRER SUR L’UTILISATEUR -----
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        mapLibre?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                zoomLevel.toFloat().toDouble()
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Recentrer")
            }
        }
    }
}
