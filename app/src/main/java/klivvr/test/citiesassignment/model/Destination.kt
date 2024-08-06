package klivvr.test.citiesassignment.model

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object Home : Destination()

    @Serializable
    data class Map(val lat: Double, val lon: Double) : Destination()
}