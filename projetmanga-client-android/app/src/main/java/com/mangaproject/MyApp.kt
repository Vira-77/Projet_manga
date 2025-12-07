package com.mangaproject

import android.app.Application
import org.maplibre.android.MapLibre

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialisation correcte pour MapLibre v10+
        MapLibre.getInstance(this)
    }
}
