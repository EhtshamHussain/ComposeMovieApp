package com.example.movieapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.MovieViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: MovieViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadUserOnAppState()
    }
    Scaffold() { innerpadding ->

        Surface(
            modifier = Modifier
                .padding(innerpadding)
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF2E1371),
                            Color(0xFF130B2B)
                        )
                    )
                ),
            color = Color.Transparent
        ) {


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Choose Movie", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(top = 24.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .clickable { navController.navigate("Search") },
                    shape = RoundedCornerShape(15.dp),
                    color = Color(0x7676801F).copy(.12f)

                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(), // important for full width row
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Search", color = Color.White)

                        Spacer(modifier = Modifier.weight(1f)) // pushes mic to end

                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF),
                        )
                    }

                }

                Spacer(modifier = Modifier.padding(top = 24.dp))
                ChooseMovie(navController, viewModel)
            }


        }
    }
}

