package com.example.movieapp.Screens

import android.app.LocaleConfig
import android.net.Uri
import android.window.SplashScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movieapp.Model.Movie
import com.example.movieapp.MovieViewModel
import com.example.movieapp.Screens.Authentication.LogInScreen
import com.example.movieapp.Screens.Authentication.SignUp
import com.example.movieapp.Screens.SeatScreen.SelectSeaat
import com.example.movieapp.Screens.SelectedMovie.SelectedMovieScreen
import com.example.movieapp.Screens.TicketDetailScreen.TicketDetailScreen
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers

@Composable
fun NavigationScreen(viewModel: MovieViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "Splash") {
        composable("Splash") {
            SplashScreen(navController,viewModel)
        }

        composable("SignUp"){
            SignUp(navController ,viewModel)
        }
        composable("Login"){
            LogInScreen(navController,viewModel)
        }
        composable("Main") {
            MainScreen(navController, viewModel)
        }

        composable("Home") {
            HomeScreen(navController, viewModel)
        }
        composable("location") {
            LocationScreen(navController)
        }
        composable("Ticket") {
            TicketScreen(navController, viewModel)
        }

        composable("Menu") {
            MenuScreen(navController)
        }
        composable("Person") {
            PersonScreen(navController,viewModel)
        }
        composable("Search") {
            MovieSearch(navController,viewModel)
        }
        composable("SeatScreen") {
            SelectSeaat(
                navController,
                viewModel,
                viewModel.selectShowId,
                viewModel.getUser()
            )
        }
        composable("choose_movie/{movieJson}") { navBackStackEntry ->
            val movieJson = Uri.decode(navBackStackEntry.arguments?.getString("movieJson"))
            val movie = Gson().fromJson(movieJson, Movie::class.java)
            SelectedMovieScreen(navController, movie, viewModel)
        }
        composable("TicketDetailScreen/{date}/{time}/{vipSeats}/{regularSeats}/{Price}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("vipSeats") { type = NavType.StringType},
                navArgument("regularSeats") { type = NavType.StringType },
                navArgument("Price") { type = NavType.FloatType }
            )
            ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val time = backStackEntry.arguments?.getString("time") ?: ""
            val vip = backStackEntry.arguments?.getString("vipSeats")?.split(",") ?: emptyList()
            val regular = backStackEntry.arguments?.getString("regularSeats")?.split(",") ?: emptyList()
            val price = backStackEntry.arguments?.getFloat("Price") ?: 0f


            TicketDetailScreen(navController, date, time, vip, regular, price,viewModel)
        }

        composable(
            route = "ConfirmationScreen/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getLong("movieId")
            ConfirmationScreen(movieId = movieId, viewModel, navController)
        }
    }
}

@Composable
fun ConfirmationScreen(movieId: Long?, viewModel: MovieViewModel, navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var movie by remember {
            mutableStateOf<com.example.movieapp.RoomDatabase.dbModels.Movie?>(
                null
            )
        }
        LaunchedEffect(Unit) {
            if (movieId != null) {
                movie = viewModel.repository.movieDao.getMovieById(movieId)
            }
        }


        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://image.tmdb.org/t/p/w500${movie?.posterUrl}")
                .crossfade(false)
                .dispatcher(Dispatchers.IO)
                .build(),
            contentDescription = movie?.title,
            modifier = Modifier
                .height(130.dp),
            contentScale = ContentScale.Crop
        )


        Column {
            Text("Booking Confirmed for Movie ID: $movieId")
            Button(
                onClick = {
                    // Set the selected tab to 3 (MenuScreen)
                    viewModel.selectedTab.value = 2
                    // Pop back to the Main screen
                    navController.navigate("Main")
                }
            ) {
                Text("Back to Menu")
            }
        }
    }

}
