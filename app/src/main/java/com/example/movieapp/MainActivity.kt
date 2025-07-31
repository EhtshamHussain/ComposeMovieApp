package com.example.movieapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.MovieRepository.MovieTheaterRepository
import com.example.movieapp.RoomDatabase.db.AppDatabase
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.Screens.DataStore.AuthPreference
import com.example.movieapp.Screens.NavigationScreen
import com.example.movieapp.ui.theme.MovieAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var repository: MovieTheaterRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        repository = MovieTheaterRepository(
            database.movieDao(),
            database.theaterDao(),
            database.showTimeDao(),
            database.seatDao(),
            database.bookingDao(),
            database.movieTheaterDao(),
            database.authDao(),
            authPrefs = AuthPreference(context = this),
        )
        CoroutineScope(Dispatchers.IO).launch {
            initializeSampleData()
        }
        setContent {
            MovieAppTheme {
//              NavigationScreen()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MovieViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MovieViewModel(repository) as T
                            }
                        }
                    )
                    NavigationScreen(viewModel)
                }
            }


        }
    }


    private suspend fun initializeSampleData() {
// Create 2 theaters
        val theater1Id = repository.createNewTheater()
        val theater2Id = repository.createNewTheater()

        val movies = listOf(
            Movie(movieId = 1, title = "Avengers", posterUrl = ""),
            Movie(movieId = 2, title = "Spider-Man", posterUrl = ""),
            Movie(movieId = 3, title = "Batman", posterUrl = "")
        )
        movies.forEach { movie ->
            repository.movieDao.insertMovie(movie)
            repository.scheduleMovieShows(movie)

        }
    }
}

@Composable
fun MainScreen(viewModel: MovieViewModel) {
    val movies by viewModel.movies.collectAsState(initial = emptyList())
    val selectedMovie by viewModel.selectedMovie
    val showTimes by viewModel.showTimes
    val seats by viewModel.seats

    Column(modifier = Modifier.padding(16.dp)) {
        // Movie List Section
        Text("Available Movies", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(movies) { movie ->
                MovieCard(movie) {
                    viewModel.selectMovie(movie)
                }
            }
        }


        // Show Times Section
        if (selectedMovie != null) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                "Show Times for: ${selectedMovie?.title}",
                style = MaterialTheme.typography.headlineSmall
            )
            if (showTimes.isEmpty()) {
                Text("Loading shows...", modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(showTimes) { show ->
                        ShowTimeCard(show) {
                            viewModel.loadSeatsForShow(show.showId)
                        }
                    }
                }
            }
        }
        // Seat Selection Section
        if (seats.isNotEmpty()) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Available Seats", style = MaterialTheme.typography.headlineSmall)
            SeatMap(seats) { seat ->
                showTimes.firstOrNull()?.let { show ->
//                    viewModel.("user_123", show.showId, seat.seatId)
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(movie.title, style = MaterialTheme.typography.titleLarge)
            Text("Duration: ${movie.duration} min", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ShowTimeCard(show: ShowTime, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${show.date}  ${show.startTime}", fontWeight = FontWeight.Bold)
            Text("Theater: ${show.theaterId}")
            Text("Ends: ${show.endTime}")
        }
    }
}

@Composable
fun SeatMap(seats: List<Seat>, onSeatSelected: (Seat) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(300.dp)
    ) {
        items(seats) { seat ->
            val color = when {
                seat.isBooked -> Color.Red
                seat.section == "VIP" -> Color.Yellow
                else -> Color.LightGray
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .background(color)
                    .border(1.dp, Color.DarkGray)
                    .clickable(enabled = !seat.isBooked) {
                        onSeatSelected(seat)
                    }
            ) {
                Text(
                    text = seat.seatNumber.toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

