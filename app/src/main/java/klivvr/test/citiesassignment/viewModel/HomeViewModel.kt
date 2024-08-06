package klivvr.test.citiesassignment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klivvr.test.citiesassignment.MyApplication
import klivvr.test.citiesassignment.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

class HomeViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _allCities = MutableStateFlow<List<City>>(emptyList())
    private val _citiesToDisplay = MutableStateFlow<List<City>>(emptyList())
    val citiesToDisplay = _citiesToDisplay.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private var currentIndex = 0
    private val chunkSize = 100

    init {
        loadCities()
    }

    private fun loadCities() {

        viewModelScope.launch {
            _isLoading.value = true
            val newCities = withContext(Dispatchers.IO) {
                readCitiesFromJson(currentIndex, chunkSize)
            }
            _allCities.value += newCities
            currentIndex += chunkSize
            _citiesToDisplay.value = getFilteredCities(_allCities.value, _searchText.value)
            _isLoading.value = false
        }
    }

    private fun readCitiesFromJson(startIndex: Int, chunkSize: Int): List<City> {
        val fileName = "cities.json"
        return try {
            val jsonString = MyApplication.appContext.assets.open(fileName).bufferedReader()
                .use { it.readText() }
            val allCities: List<City> = Json.decodeFromString(jsonString)
            allCities.drop(startIndex).take(chunkSize)
        } catch (e: IOException) {
            emptyList()
        } catch (e: SerializationException) {
            emptyList()
        }
    }

    fun loadMoreCities() {
        if (_isLoading.value) return
        loadCities()
    }

    fun onSearchTextChange(newText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _searchText.value = newText
            currentIndex = chunkSize
            _citiesToDisplay.value = getFilteredCities(_allCities.value, newText)
                .take(chunkSize)
            _isLoading.value = false
        }
    }

    private fun getFilteredCities(allCities: List<City>, searchText: String): List<City> {
        return if (searchText.isBlank()) {
            allCities.take(currentIndex)
        } else {
            allCities.filter { city ->
                city.name.startsWith(searchText, ignoreCase = true) ||
                        (city.country.startsWith(
                            searchText,
                            ignoreCase = true
                        ) && city.name.startsWith(searchText, ignoreCase = true))
            }
        }
    }
}