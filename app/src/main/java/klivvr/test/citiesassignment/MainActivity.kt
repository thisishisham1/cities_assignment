package klivvr.test.citiesassignment

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import klivvr.test.citiesassignment.ui.theme.CitiesAssignmentTheme
import klivvr.test.citiesassignment.view.HomeView
import klivvr.test.citiesassignment.view.MapView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitiesAssignmentTheme {
                MainNavigation()
            }
        }
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied.
                }

            }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


}

@Composable
private fun MainNavigation() {
    val navController = rememberNavController()
    val isLoading = remember { mutableStateOf(false) }


    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeView(navController)
        }
        composable("map/{lat}/{lon}") {
            val lat = it.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = it.arguments?.getString("lon")?.toDoubleOrNull()

            if (lat != null && lon != null) {
                isLoading.value = false
                MapView(lat = lat, lon = lon,navController)
            } else {
                isLoading.value = true
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}