package com.example.movieapp.Screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.movieapp.MovieViewModel
import com.example.movieapp.ui.theme.poppins
import com.google.gson.Gson
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearch(navController: NavController, viewModel: MovieViewModel) {
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-focus when composable appears
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF2E1371), Color(0xFF130B2B))
                )
            )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Search",
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontFamily = poppins
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
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
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clickable { keyboardController?.hide() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        viewModel.updateSearchQuery(it)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x7676801F).copy(.12f),
                        unfocusedContainerColor = Color(0x7676801F).copy(.12f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .clip(RoundedCornerShape(15.dp))
                        .focusRequester(focusRequester),
                    label = {
                        Text(
                            "Search",
                            style = TextStyle(fontSize = 17.sp, color = Color(0xD2FFFFFF))
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF)
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                )

                // Collect search results
                val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

                when {
                    searchResults.loadState.refresh is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    searchResults.loadState.refresh is LoadState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Failed to load search results", color = Color.Red)
                        }
                    }
                    searchResults.itemCount == 0 && searchText.isNotEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No results found", color = Color.White)
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(searchResults.itemCount) { index ->
                                searchResults[index]?.let { movie ->
                                    MovieItem(
                                        movie = movie,
                                        onClick = { selectedMovie ->
                                            val movieJson = Uri.encode(Gson().toJson(selectedMovie))
                                            navController.navigate("choose_movie/$movieJson")
                                        },
//                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}






