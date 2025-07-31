package com.example.movieapp.Screens.Authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun LogInScreen(navController: NavController,viewModel: MovieViewModel) {
   val context = LocalContext.current
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
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
                    "LogIn", style = TextStyle(
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

                Text(
                    "Email",
                    color = Color.White,
                    fontFamily = poppins,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x7676801F).copy(.12f),
                        unfocusedContainerColor = Color(0x7676801F).copy(.12f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { },
                    placeholder = {
                        Text(
                            "Enter Your Email",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color(0xD2FFFFFF)
                            ),
                        )
                    },

                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.padding(top = 24.dp))

                Text(
                    "Password",
                    color = Color.White,
                    fontFamily = poppins,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x7676801F).copy(.12f),
                        unfocusedContainerColor = Color(0x7676801F).copy(.12f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.RemoveRedEye,
                            contentDescription = null,
                            tint = Color(0xD2FFFFFF)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { },
                    placeholder = {
                        Text(
                            "Enter Password",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color(0xD2FFFFFF)
                            ),
                        )
                    },

                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.padding(top = 24.dp))

                Button(
                    onClick = {
                        if(email != "" && password != "") {
                            viewModel.login(email, password) { result ->
                                if (result==true) {
                                    Toast.makeText(
                                        context,
                                        "LogIn Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("Main")
                                } else {
                                    Toast.makeText(context, "Invalid User", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }else{
                            Toast.makeText(
                                context,
                                "Email Or Password Is Empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                        "LogIn",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text("Don't Have  Account?", color = Color.White)
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        navController.navigate("SignUp")
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
                        "Create Account",
                        color = Color.White
                    )
                }
            }
        }
    }
}
