package com.example.movieapp.Screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movieapp.MovieViewModel
import com.example.movieapp.RoomDatabase.dbModels.UserTicket
import com.example.movieapp.Screens.BarCode.BarcodeGenerator
import com.example.movieapp.ui.theme.poppins
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: MovieViewModel,
) {
//    val ticket1 by viewModel.currentTicket

    // Load tickets when the screen is composed
    val tickets by viewModel.userTickets
    LaunchedEffect(Unit) {
        val email = viewModel.getUser()
        viewModel.loadUserTickets(email) // Ensures latest data on screen entry
    }
//    if (tickets.isEmpty()) {
//        Text("No tickets booked yet.")
//    } else {
//        // Display tickets (e.g., HorizontalPager with TicketCard)
//        tickets.forEach { ticket ->
//            Text("Ticket: ${ticket.seats}")
//        }
//    }


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
                .padding(end = 5.dp),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Mobile Ticket",
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
                                            Color(0xFF60FFCA), Color(0x0060FFCA), Color(0x0060FFCA)
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
                            onClick = { },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60FFCA), Color(0x0060FFCA), Color(0x0060FFCA)
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
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val screenHeight = configuration.screenHeightDp.dp
            Log.d("height", "TicketCard: $screenHeight")
            Log.d("width", "TicketCard: $screenWidth")
            val scrollState = if (screenHeight <= 880.dp) rememberScrollState() else null
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .then(
                        if (scrollState != null) Modifier.verticalScroll(scrollState) else Modifier
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Once you buy a movie ticket\n simply scan the barcode to\n access your movie.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = poppins,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                val tickets by viewModel.userTickets
                Log.d("userTickets", "TicketScreen: ${tickets}")
                if (tickets.isEmpty()) {
                    Text("No tickets booked yet.", color = Color.White, fontFamily = poppins)
                } else {
                    val pagerState = rememberPagerState(pageCount = {
                        tickets.size
                    })

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
//                            .height(500.dp)
                    ) { page ->
                        val ticket = tickets[page]
                        TicketCard(ticket, navController,viewModel)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Page Indicator below pager
                    DotsIndicator(
                        totalDots = tickets.size,
                        selectedIndex = pagerState.currentPage
                    )

                    Spacer(modifier = Modifier.padding(bottom = 156.dp))
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: UserTicket, navController: NavController,viewModel: MovieViewModel) {


    // Format date (e.g., "2025-07-21" to "Jul 21, yyyy")
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val dateObj = inputFormat.parse(ticket.date)
        outputFormat.format(dateObj!!)
    } catch (e: Exception) {
        ticket.date
    }

    // Format time (e.g., "10:00" to "10 a.m.")
    val formattedTime = try {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val outputFormat = SimpleDateFormat("h a", Locale.US)
        val timeObj = inputFormat.parse(ticket.time)
        outputFormat.format(timeObj!!).lowercase(Locale.US)
            .replace("am", "a.m.").replace("pm", "p.m.")
    } catch (e: Exception) {
        ticket.time
    }



    // Filter and sort seats numerically
    val vipSeats =
        ticket.seats.filter { (it.toIntOrNull() ?: 0) <= 20 }.sortedBy { it.toIntOrNull() ?: 0 }
    val regularSeats =
        ticket.seats.filter { (it.toIntOrNull() ?: 0) > 20 }.sortedBy { it.toIntOrNull() ?: 0 }
    val totalPrice = ticket.price
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${ticket.posterUrl}",
            contentDescription = ticket.movieTitle,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .width(250.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .width(250.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {
                        val vipStr = vipSeats.joinToString(",")
                        val regStr = regularSeats.joinToString(",")

                        val encodedVip = Uri.encode(vipStr)
                        val encodedReg = Uri.encode(regStr)

                        viewModel.getCurrentTicketDetails(ticket)
                        navController.navigate("TicketDetailScreen/${formattedDate}/${formattedTime}/${encodedVip}/${encodedReg}/${totalPrice}")

                    })
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
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 5.dp),
                        ) {
                            Text("Date: $formattedDate", color = Color.Black)
                            Text(",  $formattedTime", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (vipSeats.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
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
                                                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (regularSeats.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
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
                                                modifier = Modifier.padding(start = 5.dp, end = 2.dp)

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
                            Text("${ticket.price}$", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        val barcodeContent = """
                                        Ticket ID: ${ticket.ticketId}
                                        Movie: ${ticket.movieTitle}
                                        Date: ${ticket.date}
                                        Time: ${ticket.time}
                                        Price:${ticket.price}
                                    """.trimIndent()

                        BarcodeGenerator(barcodeContent)



                    }
                }
            }
        }
    }
}


@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0 until totalDots) {
            val color = if (i == selectedIndex) Color.White else Color.Gray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

//                        Image(
//                            painter = painterResource(id = R.drawable.barcode),
//                            contentDescription = "Barcode",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(80.dp)
//                                .padding(8.dp)
//                        )

//                        val barcodeContent  =    ticket.ticketId.toString()
//                        val barcodeContent = "${ticket.ticketId}-${ticket.movieTitle}-${ticket.date}-${ticket.time}"


//
//@Composable
//fun TicketCard(ticket: UserTicket,viewModel: MovieViewModel) {
//    // Format date (e.g., "2025-07-21" to "Jul 21")
////    val formattedDate = ticket.date
//    val formattedDate = try {
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
//        val dateObj = inputFormat.parse(ticket.date)
//        outputFormat.format(dateObj!!)
//    } catch (e: Exception) {
//        ticket.date
//    }
//
//
//
//
//    // Format time (e.g., "10:00" to "10 a.m.")
//    val formattedTime = try {
//        val inputFormat = SimpleDateFormat("HH:mm", Locale.US)
//        val outputFormat = SimpleDateFormat("h a", Locale.US)
//        val timeObj = inputFormat.parse(ticket.time)
//        outputFormat.format(timeObj!!).lowercase(Locale.US)
//            .replace("am", "a.m.").replace("pm", "p.m.")
//    } catch (e: Exception) {
//        ticket.time
//    }
//
//    val vipSeats = ticket.seats.filter { it <= 20.toString() }.sorted()
//    val regularSeats = ticket.seats.filter { it > 20.toString() }.sorted()
//
//
////    val vipSeats = ticket1?.seats?.filter { it <= 20.toChar() }.sorted()
////    val regularSeats = ticket1?.seats?.filter { it > 20.toChar() }?.sorted()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        AsyncImage(
//            model = "https://image.tmdb.org/t/p/w500${ticket.posterUrl}",
//            contentDescription = ticket.movieTitle,
//            modifier = Modifier
//                .clip(RoundedCornerShape(10.dp))
////                .size(300.dp)
//                .width(250.dp)
////                .height(300.dp)
//            ,
//            contentScale = ContentScale.Crop
//        )
//
//        Box(
//            modifier = Modifier
////                .size(300.dp)
//                .width(250.dp)
////                .wrapContentHeight()
////                .height(195.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .background(Color.White)
//        ) {
//            Column(
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.linearGradient(
//                                listOf(Color(0xFF9C91B4), Color(0xFFCD9AC4))
//                            )
//                        )
//                        .padding(vertical = 12.dp, horizontal = 16.dp)
//                ) {
//                    Column{
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
////                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Date: $formattedDate", color = Color.Black)
//                            Text(",  $formattedTime", color = Color.Black)
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//
//
//                        if(vipSeats.isNotEmpty()) {
//                            Column(modifier = Modifier.fillMaxWidth(),) {
//
//                                // Section Text
//                                Row(
//                                    Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = "Section: Vip",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
//
//                                    Text(
//                                        text = "Seats:",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
//
//                                }
//
//
//                                // Seats shown in lines of 5 seats per row
//                                vipSeats.chunked(7).forEach { seatChunk ->
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(bottom = 4.dp),
//                                        horizontalArrangement = Arrangement.Start
//                                    ) {
//                                        seatChunk.forEach { seat ->
//                                            Text(
//                                                text = "$seat, ",
//                                                color = Color.Black,
//                                                modifier = Modifier.padding(end = 4.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if(regularSeats.isNotEmpty() ) {
//                            Column(modifier = Modifier.fillMaxWidth(),) {
//                                // Section Text
//                                Row(
//                                    Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = "Section:Regular",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
//
//                                    Text(
//                                        text = "Seats:",
//
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(bottom = 4.dp)
//                                    )
//
//                                }
//
//                                // Seats shown in lines of 5 seats per row
//                                regularSeats.chunked(7).forEach { seatChunk ->
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(bottom = 4.dp),
//                                        horizontalArrangement = Arrangement.Start
//                                    ) {
//                                        seatChunk.forEach { seat ->
//                                            Text(
//                                                text = "$seat, ",
//                                                color = Color.Black,
//                                                modifier = Modifier.padding(end = 4.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        Image(
//                            painter = painterResource(id = R.drawable.barcode),
//                            contentDescription = "Barcode",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(80.dp)
//                                .padding(8.dp,)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}




