package com.example.movieapp.Screens.TicketDetailScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.MovieViewModel
import com.example.movieapp.Screens.BarCode.BarcodeGenerator
import com.example.movieapp.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    navController: NavController,
    date: String,
    time: String,
    vipSeats: List<String>,
    regularSeats: List<String>,
    price: Float,
    viewModel: MovieViewModel,
) {
    Log.d("vip", "TicketDetailScreen: $vipSeats")
    val currentTicketDetails by viewModel.ticketDetails
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
                .fillMaxSize()
//                .padding(start = 10.dp,end=10.dp)
            ,
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
                    .clip(RoundedCornerShape(5.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

//                Row(Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly) {
//
//
//                Text("Date $date", color = Color.White)
//                Text("Time $time", color = Color.White)
//                }
//                Row(Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly) {
//
//
//                    Text("Vip seat ${vipSeats.joinToString(", ")}", color = Color.White)
//                }
//                Text("Regular seat ${regularSeats.joinToString(", ")}", color = Color.White)
//                Text("Price $price", color = Color.White)


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF9C91B4), Color(0xFFCD9AC4))
                            )
                        )
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Date: $date", color = Color.Black)
                            Text(",  $time", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (vipSeats.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
//                                    Text(
//                                        text = "Section: Vip",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
                                    Text(
                                        text = "VIP Seats:",
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                val allVipSeats = vipSeats
                                vipSeats.chunked(7).forEach { seatChunk ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        seatChunk.forEach { seat ->
                                            val isLastSeat = seat == allVipSeats.last()
                                            Text(
                                                text = if (isLastSeat) seat else "$seat,",
                                                color = Color.Black,
                                                modifier = Modifier.padding(
                                                    start = 10.dp,
                                                    end = 15.dp,
                                                    top = 10.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!regularSeats.isNullOrEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
//                                    Text(
//                                        text = "Section: Regular",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
                                    Text(
                                        text = "Regular Seats:",
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                val allRegularSeats = regularSeats
                                regularSeats.chunked(7).forEach { seatChunk ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        seatChunk.forEach { seat ->
                                            val isLastSeat = seat == allRegularSeats.last()
                                            Text(
                                                text = if (isLastSeat) seat else "$seat,",
                                                color = Color.Black,
                                                modifier = Modifier.padding(
                                                    start = 10.dp,
                                                    end = 10.dp,
                                                    top = 10.dp
                                                )
                                            )
                                        }
                                    }
                                }


                            }
                        }


                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Total Price :", color = Color.Black)
                            Text("${price}$", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))


                        val barcodeContent = """
                                        Ticket ID: ${currentTicketDetails?.ticketId}
                                        Movie: ${currentTicketDetails?.movieTitle}
                                        Date: ${currentTicketDetails?.date}
                                        Time: ${currentTicketDetails?.time}
                                        Price:${currentTicketDetails?.price}
                                    """.trimIndent()




                        BarcodeGenerator(
                            barcodeContent, modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )


                    }
                }
            }
        }
    }
}