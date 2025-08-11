package com.example.movieapp.Screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movieapp.Model.Movie
import com.example.movieapp.MovieViewModel
import com.example.movieapp.R
import com.example.movieapp.ui.theme.poppins
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow


@Composable
fun ChooseMovie(navController: NavController, viewModel: MovieViewModel) {
//    val viewModel: MovieViewModel = viewModel
//    val hasRunOnce = rememberSaveable { mutableStateOf(false) }

//    LaunchedEffect(Unit) {
//        val currentDate = java.time.LocalDate.now().toString()
//        val time = java.time.LocalTime.now().toString()
//        viewModel.deletePastBookings(currentDate , time)
//    }


    LazyColumn() {
        // Trending Movies Section
        item {
            Text(
                "Now Playing",
                color = Color.White,
                fontFamily = poppins,
                fontSize = 17.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
            )
            MovieList(
                movies = viewModel.notPlaying,
                onClick = { movie ->
                    val movieJson = Uri.encode(Gson().toJson(movie))
                    navController.navigate("choose_movie/$movieJson")

                }

            )
        }
        // Popular Movies Section
        item {
            Text(
                "Coming Soon",
                color = Color.White,
                fontFamily = poppins,
                fontSize = 17.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),

                )
            MovieList(
                movies = viewModel.upComing,
                onClick = { movie ->
                    val movieJson = Uri.encode(Gson().toJson(movie))
                    navController.navigate("choose_movie/$movieJson")
                }
            )
        }


        // Top Rated Movies Section
        item {
            Text(
                "Top movies",
                color = Color.White,
                fontFamily = poppins,
                fontSize = 17.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
            )
            MovieList(
                movies = viewModel.trendingMovies,
                onClick = { movie ->
                    val movieJson = Uri.encode(Gson().toJson(movie))
                    navController.navigate("choose_movie/$movieJson")
                }

            )
            Spacer(modifier = Modifier.padding(bottom = 200.dp))
        }

    }
}

@Composable
fun MovieList(
    movies: Flow<PagingData<Movie>>,
    onClick: (Movie) -> Unit,
) {
    val lazyMovieItems = movies.collectAsLazyPagingItems()

    when (lazyMovieItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load movies", color = Color.Red)
            }
        }

        is LoadState.NotLoading -> {
            LazyRow {
                items(lazyMovieItems.itemCount) { index ->
                    lazyMovieItems[index]?.let { movie ->

                        MovieItem(
                            movie = movie,
                            onClick = onClick
                        )
                    }
                }

                // Loading indicator
                if (lazyMovieItems.loadState.append is LoadState.Loading) {
                    item {


                        CircularProgressIndicator(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, top = 50.dp))

                    }
                }
            }
        }
    }

}

@Composable
fun MovieItem(
    movie: Movie,
    onClick: (Movie) -> Unit,
) {

    Card(
        modifier = Modifier
            .width(120.dp)
            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
            .clickable { onClick(movie) },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Movie Poster
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .crossfade(true)
                    .dispatcher(Dispatchers.IO)
                    .build(),
                placeholder = painterResource(R.drawable.movieplaceholder),
                contentDescription = movie.title,
                modifier = Modifier
                    .height(130.dp)
                    .graphicsLayer {
                        clip = true
                    },
                contentScale = ContentScale.Crop
            )

        }

    }
}


