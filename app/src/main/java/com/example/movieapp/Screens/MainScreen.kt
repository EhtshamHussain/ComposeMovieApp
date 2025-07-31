package com.example.movieapp.Screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.Model.NavItem
import com.example.movieapp.MovieViewModel
import com.example.movieapp.ui.theme.ChangeSystemBarsColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScreenContent(
    modifier: Modifier,
    navController: NavController,
    selectedItem: Int,
    viewModel: MovieViewModel,
) {
    ChangeSystemBarsColor(
        NavBarColor = Color(0xC88631B6)

    )
    AnimatedContent(
        targetState = selectedItem,
        transitionSpec = {
            slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = Up
            ).with(
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = Down
                )
            )
        }
    ) {
        when (it) {
            0 -> HomeScreen(navController, viewModel)
//            1 -> LocationScreen(navController)
            1 -> TicketScreen(navController,viewModel)
//            3 -> MenuScreen(navController)
            2 -> PersonScreen(navController,viewModel)
        }
    }
}

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val navItems = listOf(
        Icons.Default.Home,
        Icons.Default.Dashboard,
        Icons.Default.Person,
//        Icons.Default.Place,
//        Icons.Default.GridView
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4A148C), Color(0xFFCE93D8))
                ),
//                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEachIndexed { index, icon ->
            Box(
                contentAlignment = Alignment.TopCenter,
            ) {
                // Indicator Circle only for selected item
                if (selectedIndex == index) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xC88631B6))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF09FBD3), Color(0xFF09FBD3))
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                onItemSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Empty space below indicator to avoid duplicate icon
                    Spacer(modifier = Modifier.height(32.dp))
                } else {
                    // Normal IconButton for unselected items
                    IconButton(onClick = {
                        onItemSelected(index)
                    }) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp).clickable(onClick = {onItemSelected(index)})
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen(navController: NavController, viewModel: MovieViewModel) {
    var selectedIndex by viewModel.selectedTab

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            selectedItem = selectedIndex,
            viewModel = viewModel
        )
    }
}