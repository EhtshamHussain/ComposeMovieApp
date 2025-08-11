package com.example.movieapp.Screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.movieapp.MovieViewModel
import com.example.movieapp.Screens.DataStore.AuthPreference
import com.example.movieapp.ui.theme.ChangeSystemBarsColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController,viewModel: MovieViewModel) {
    val darkThemeColor = isSystemInDarkTheme()

    ChangeSystemBarsColor(
        NavBarColor = if(darkThemeColor) Color.Black else Color.White
    )
    val context = LocalContext.current

    val authPreference = remember { AuthPreference(context) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Play.json")
    )


    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(Unit) {
        delay(4000)
//        viewModel.insertTheaterFromFirebase()
        if (authPreference.isUserLoggedIn()) {
            navController.navigate("Main") {
                popUpTo("Splash") { inclusive = true }
            }
        } else {
            navController.navigate("SignUp") {
                popUpTo("Splash") { inclusive = true }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.run { size(200.dp) }
        )
    }
}