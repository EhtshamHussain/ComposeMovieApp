package com.example.movieapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.RoomDatabase.dbModels.UserTicket
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

@Preview()
@Composable
fun LocationScreen(navController: NavController= rememberNavController()) {
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


    }
}

@Preview(showBackground = true)
@Composable
fun TicketCard123() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${R.drawable.img1}",
            contentDescription = "ticket.movieTitle",
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .width(250.dp)
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .size(300.dp)
//                .width(250.dp)
//                .height(195.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            Column(
            ) {
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Date: April 23", color = Color.Black)
                            Text("Time: 6.p.m.", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Section: Vip", color = Color.Black,
                                modifier = Modifier.padding(end = 15.dp)

                            )
//                            Text("Seats: ${ticket.seats.joinToString(", ")}", color = Color.Black,
                            Text("Seats: 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20", color = Color.Black,)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Section: Regular", color = Color.Black,
                                modifier = Modifier.padding(end = 15.dp)

                            )
//                            Text("Seats: ${ticket.seats.joinToString(", ")}", color = Color.Black,
                            Text("Seats: 21,22,23,24,25,26,27,28,29,30,31,32,33" +
                                    ",34,35,36,37,38,39,40," +
                                    "41,42,43,44,45,46,47,48,49",

                                color = Color.Black)
                        }

                        Image(
                            painter = painterResource(id = R.drawable.barcode),
                            contentDescription = "Barcode",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(8.dp,)
                        )
                    }
                }
            }
        }
    }
}