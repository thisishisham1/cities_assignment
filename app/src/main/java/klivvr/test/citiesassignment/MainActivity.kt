package klivvr.test.citiesassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import klivvr.test.citiesassignment.model.Destination
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
    }
}

@Composable
private fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.Home) {
        composable<Destination.Home> {
            HomeView()
        }
        composable<Destination.Map> {
            MapView()
        }
    }
}