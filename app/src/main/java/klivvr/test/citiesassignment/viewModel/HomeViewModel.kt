package klivvr.test.citiesassignment.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klivvr.test.citiesassignment.MyApplication
import klivvr.test.citiesassignment.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

class HomeViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> get() = _cities

    private var currentIndex = 0
    private val chunkSize = 20

    init {
        loadCities()
    }

    private fun loadCities() {
        _isLoading.value = true
        viewModelScope.launch {
            val newCities = withContext(Dispatchers.IO) {
                readCitiesFromJson(currentIndex, chunkSize)
            }
            _cities.value = (_cities.value ?: emptyList()) + newCities
            currentIndex += chunkSize
            _isLoading.value = false
        }
    }

    private fun readCitiesFromJson(startIndex: Int, chunkSize: Int): List<City> {
        Log.d("HomeViewModel", "Reading cities from JSON file")
        val fileName = "cities.json"

        return try {
            val jsonString = MyApplication.appContext.assets.open(fileName).bufferedReader()
                .use { it.readText() }
            val allCities: List<City> = Json.decodeFromString(jsonString)
            allCities.drop(startIndex).take(chunkSize)
        } catch (e: IOException) {
            Log.e("HomeViewModel", "Error reading file: ${e.message}")
            emptyList()
        } catch (e: SerializationException) {
            Log.e("HomeViewModel", "Error parsing JSON: ${e.message}")
            emptyList()
        }
    }

    fun loadMoreCities() {
        if (_isLoading.value == true) return
        loadCities()
    }

}