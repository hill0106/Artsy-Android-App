package com.example.hw4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hw4.data.api.ApiClient
import com.example.hw4.ui.ArtistScreen
import com.example.hw4.ui.HomeScreen
import com.example.hw4.ui.LoginScreen
import com.example.hw4.ui.RegisterScreen
import com.example.hw4.ui.SearchScreen
import com.example.hw4.ui.theme.Hw4Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiClient.init(this)
//        enableEdgeToEdge()
        setContent {
            Hw4Theme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }

}


object AppDestinations {
    const val HOME_ROUTE = "home"
    const val SEARCH_ROUTE = "search"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val ARTIST_DETAIL_ROUTE = "artistDetail"
    const val ARTIST_ID_ARG = "artistId"
    val artistDetailRoute = "$ARTIST_DETAIL_ROUTE/{$ARTIST_ID_ARG}"
}


@Composable
fun MyApp(
    modifier: Modifier = Modifier,
) {
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()
    val apiService = ApiClient.apiService

    LaunchedEffect(Unit) {
        try {
            val resp = apiService.getMyData()
            isLoggedIn = resp.isSuccessful
        } catch (e: Exception) {
            isLoggedIn = false
        }
    }

    NavHost(
        navController    = navController,
        startDestination = AppDestinations.HOME_ROUTE,
    ) {
        composable("home") {
            HomeScreen(
                modifier = modifier,
                isLoggedIn = isLoggedIn,
                onNavigateToSearch = {navController.navigate(AppDestinations.SEARCH_ROUTE) },
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE)
                },
                onNavigateToArtist = { artistId ->
                    navController.navigate("${AppDestinations.ARTIST_DETAIL_ROUTE}/$artistId")
                },
                onDeleteAccSuccessful = {
                    isLoggedIn = false
                    navController.navigate(AppDestinations.HOME_ROUTE)
                },
                onLogoutSuccessful = {
                    isLoggedIn = false
                    navController.navigate(AppDestinations.HOME_ROUTE)
                }
            )
        }
        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() },
                isLoggedIn = isLoggedIn,
                onNavigateToArtist = { artistId ->
                    navController.navigate("${AppDestinations.ARTIST_DETAIL_ROUTE}/$artistId")
                }
            )
        }
        composable(
            route = AppDestinations.artistDetailRoute,
            arguments = listOf(navArgument(AppDestinations.ARTIST_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString(AppDestinations.ARTIST_ID_ARG)
            if (artistId != null) {
                ArtistScreen(
                    artistId = artistId,
                    isLoggedIn = isLoggedIn,
                    onBack = { navController.popBackStack() },
                    onNavigateToArtist = { artistId ->
                        navController.navigate("${AppDestinations.ARTIST_DETAIL_ROUTE}/$artistId")
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(route = AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER_ROUTE) {
                    }

                },
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate(AppDestinations.HOME_ROUTE)
                }
            )
        }

        composable(route = AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                    }
                },
                onRegisterSuccess = {
                    isLoggedIn = true
                    navController.navigate(AppDestinations.HOME_ROUTE)
                }
            )
        }
    }
}





@Preview(showBackground = true, widthDp = 320)
@Composable
fun Preview() {
    Hw4Theme {
        MyApp()
    }
}