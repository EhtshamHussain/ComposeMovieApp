package com.example.movieapp.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movieapp.MovieViewModel
import com.example.movieapp.ui.theme.poppins

@Composable
fun PersonScreen(navController: NavController, viewModel: MovieViewModel) {
    val context = LocalContext.current
    val user = viewModel.userDetail.value
    Log.d("user", "PersonScreen:${user.toString()} ")
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
            color = androidx.compose.ui.graphics.Color.Transparent
        ) {

            var state = remember { mutableStateOf("") }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Personnl Details", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                        color = androidx.compose.ui.graphics.Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(top = 24.dp))

                Icon(
                    imageVector = Icons.Default.Person, contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.padding(top = 24.dp))
                if (user != null) {

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Text("Email:", color = Color.White, fontFamily = poppins)
                        Spacer(modifier = Modifier
//                            .padding(top = 24.dp)
                            .width(17.dp))
                        Text(
                            text = user.email.toString(), color = Color.White, fontFamily = poppins,
                            modifier = Modifier.padding(end = 24.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 24.dp))
                    }
//                    Spacer(modifier = Modifier.padding(top = 24.dp))
//                    Row(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(start = 20.dp),
//                        horizontalArrangement = Arrangement.Start
//                    ) {
//                        Text("Password:", color = Color.White, fontFamily = poppins)
//                        Spacer(modifier = Modifier
//                            .width(17.dp))
//                        Text(
//                            text = user.password, color = Color.White, fontFamily = poppins,
//                        )
//                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate("Login") {
                            popUpTo("main") { inclusive = true }
                            viewModel.selectedTab.value=0
                        }
                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
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
                        "Log Out",
                        color = Color.White
                    )
                }
            }
        }
    }
}


