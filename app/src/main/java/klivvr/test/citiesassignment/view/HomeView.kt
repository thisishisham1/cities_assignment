package klivvr.test.citiesassignment.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import klivvr.test.citiesassignment.model.City
import klivvr.test.citiesassignment.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(navController: NavController) {
    val vm = remember {
        HomeViewModel()
    }
    var showSearchField by remember { mutableStateOf(false) }
    val isLoading by vm.isLoading.collectAsState()
    val citiesToDisplay by vm.citiesToDisplay.collectAsState()
    val searchText by vm.searchText.collectAsState()
    Scaffold(topBar = {
        if (showSearchField) {
            OutlinedTextField(value = searchText,
                onValueChange = {
                    vm.onSearchTextChange(it)
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        showSearchField = false
                        vm.onSearchTextChange("")
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Search")
                    }
                })
        } else {
            CenterAlignedTopAppBar(title = { Text("Cities") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showSearchField = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search, contentDescription = "Search"
                        )
                    }
                })
        }
    }) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Cities(
                    cities = citiesToDisplay,
                    onLoadMore = vm::loadMoreCities,
                    navController = navController
                )
            }
        }
    }

}

@SuppressLint("RememberReturnType")
@Composable
fun Cities(cities: List<City>, onLoadMore: () -> Unit, navController: NavController) {
    val listState = rememberLazyListState()
    var shouldLoadMore by remember { mutableStateOf(false) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(cities) { city ->
            CityCard(
                city = city,
                onClick = {
                    navController.navigate("map/${city.coord.lat}/${city.coord.lon}")
                }
            )
        }

        if (cities.isNotEmpty()) {
            item {
                if (shouldLoadMore) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        onLoadMore()
                        shouldLoadMore = false
                    }) {
                        Text("Load More")
                    }
                }
            }
        }

        if (cities.isEmpty()) {
            item {
                Text("No cities found.")
            }
        }
    }

    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()) {
        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == cities.size - 1 && !shouldLoadMore) {
            shouldLoadMore = true
        }
        shouldLoadMore = true

    }
}

@Composable
fun CityCard(city: City, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "${city.name}, ${city.country}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "City Location",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${city.coord.lat}, ${city.coord.lon}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
