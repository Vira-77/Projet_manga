package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("_id")
    val id: String,
    val nom: String,
    val adresse: String?,
    val telephone: String?,
    val email: String?,
    val position: Position
)

data class Position(
    val type: String,
    val coordinates: List<Double>  // [lon, lat]
)
