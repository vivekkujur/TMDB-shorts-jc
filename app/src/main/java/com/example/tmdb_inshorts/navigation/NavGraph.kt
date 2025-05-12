package com.example.tmdb_inshorts.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tmdb_inshorts.ui.screens.HomeScreen
import com.example.tmdb_inshorts.ui.screens.MovieDetailScreen
import com.example.tmdb_inshorts.ui.viewmodel.HomeViewModel
import com.example.tmdb_inshorts.ui.viewmodel.MovieDetailViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                navController = navController

            )
        }
        
        composable(
            route = "${Screen.MovieDetail.route}/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                }
            )
        ) {
            val movieDetailViewModel: MovieDetailViewModel = viewModel()
            MovieDetailScreen(
                viewModel = movieDetailViewModel,
                navController = navController,
                movieId = it.arguments?.getInt("movieId")!!

            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MovieDetail : Screen("movie_detail")
} 