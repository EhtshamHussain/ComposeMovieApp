package com.example.movieapp.Screens.SelectedMovie


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movieapp.Model.Movie
import com.example.movieapp.MovieViewModel
import com.example.movieapp.Screens.ZigZagScrollList
import com.example.movieapp.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SelectedMovieScreen(navController: NavController, movie: Movie, viewModel: MovieViewModel) {

    val movies = com.example.movieapp.RoomDatabase.dbModels.Movie(
        movieId = movie.id,
        title = movie.title,
        posterUrl = movie.poster_path,
    )

    LaunchedEffect(Unit) {

        viewModel.initializeSelectedMovie(movies)

    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background Image
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = "Movie Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {

                        IconButton(
                            onClick = { navController.navigate("Main") },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60FFCA),
                                            Color(0x0060FFCA),
                                            Color(0x0060FFCA)
                                        ),
                                        start = Offset(0f, 20f),
                                        end = Offset(0f, 100f)
                                    ),
                                    CircleShape
                                ),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xC88631B6),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60FFCA),
                                            Color(0x0060FFCA),
                                            Color(0x0060FFCA)
                                        ),
                                        start = Offset(0f, 20f),
                                        end = Offset(0f, 100f)
                                    ),
                                    CircleShape
                                ),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xC88631B6),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Action",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->


            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontFamily = poppins,
                                fontWeight = FontWeight.W700,
                                fontSize = 20.sp,
                            )
                        ) { append(movie.title + "\n") }
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.W300,
                                fontSize = 17.sp
                            )
                        ) { append(movie.original_title) }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                ExpandableText(movie)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Select date and time",
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                ZigZagScrollList(viewModel, navController) { selectedShowId ->
                    if (selectedShowId != null) {
                        viewModel.selectShowId = selectedShowId.showId
//                        viewModel.loadSeatsForShow(selectedShowId.showId)
                    }
                }
                Log.d("id", "SelectedMovieScreen: ${viewModel.selectShowId}")


                Button(
                    onClick = {
                        navController.navigate("SeatScreen")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFB6116B),
                                    Color(0xFF3B1578),
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "Reservation",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            }
        }
    }
}


@Composable
fun ExpandableText(movie: Movie) {
    var expanded by remember { mutableStateOf(false) }
    val maxLines = if (expanded) Int.MAX_VALUE else 3 // Show 3 lines when collapsed
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = movie.overview,
            color = Color.White,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Justify,
            fontWeight = FontWeight.W400,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()

                .clickable {
                    expanded = !expanded
                }
        )


        Text(
            text = if (expanded) "Read less" else "Read more",
            color = Color(0xFF60FFCA), // Your gradient color
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .align(Alignment.End)
        )


    }
}







