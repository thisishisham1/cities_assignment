package klivvr.test.citiesassignment.model

import kotlinx.serialization.Serializable

@Serializable
data class Coord(
    val lon: Double,
    val lat: Double
)

@Serializable
data class City(
    val country: String,
    val name: String,
    val _id: Int,
    val coord: Coord
)
