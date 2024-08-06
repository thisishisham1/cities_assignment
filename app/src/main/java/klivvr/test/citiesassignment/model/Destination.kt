package klivvr.test.citiesassignment.model

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object Home : Destination()
    @Serializable
    data object Map : Destination()
}