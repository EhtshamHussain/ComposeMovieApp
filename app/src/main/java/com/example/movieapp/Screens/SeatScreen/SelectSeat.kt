package com.example.movieapp.Screens.SeatScreen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.MovieViewModel
import com.example.movieapp.R
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.Ticket
import com.example.movieapp.ui.theme.poppins
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSeaat(
    navController: NavController,
    viewModel: MovieViewModel,
    showId: Long,
    userId: String,
) {
    val bookingCompleted by viewModel.bookingCompleted.collectAsState()

    val context = LocalContext.current
    val seats by viewModel.seats

    val selectedSeats = viewModel.selectedSeats
    val currentShow by viewModel.currentShowTime
    Log.d("showid", "showId: ${showId}")

    LaunchedEffect(Unit) {
        if (showId != 0L) {
            Log.d("showid", "showId: ${showId}")
            viewModel.loadSeatsForShow(showId)
        }
    }





    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF2E1371), Color(0xFF130B2B)
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 10.dp, end = 5.dp),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Choose Seats",
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontFamily = poppins
                        )
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, actionIconContentColor = Color.White
                    ), navigationIcon = {

                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60FFCA), Color(0x0060FFCA), Color(0x0060FFCA)
                                        ), start = Offset(0f, 20f), end = Offset(0f, 100f)
                                    ), CircleShape
                                ),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xC88631B6), contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }, actions = {
                        IconButton(
                            onClick = {}, modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF60FFCA), Color(0x0060FFCA), Color(0x0060FFCA)
                                        ), start = Offset(0f, 20f), end = Offset(0f, 100f)
                                    ), CircleShape
                                ), colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xC88631B6), contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Action",
                                tint = Color.White
                            )
                        }
                    })
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.curveline),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(79.dp)
                )
                Spacer(modifier = Modifier.padding(bottom = 5.dp))

                SeatLayout(seats = seats, selectedSeats = selectedSeats, onSeatClick = { seatId ->
                    viewModel.toggleSeatSelection(seatId)
                }, showToast = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                })

                Spacer(modifier = Modifier.padding(top = 20.dp))




                SelectionGuidence()


                Spacer(modifier = Modifier.padding(top = 20.dp))

                // Calculate summary data
                val selectedSeatDetails = remember(selectedSeats, seats) {
                    seats.filter { it.seatId in selectedSeats }
                }

                val totalPrice = remember(selectedSeatDetails) {
                    selectedSeatDetails.sumOf { seat ->
                        if (seat.section == "VIP") 15.0 else 10.0
                    }
                }

                val seatNumbers = remember(selectedSeatDetails) {
                    selectedSeatDetails.joinToString(", ") { it.seatNumber.toString() }
                }


                val sectionType = remember(selectedSeatDetails) {
                    when {
                        selectedSeatDetails.isEmpty() -> ""
                        selectedSeatDetails.all { it.section == "VIP" } -> "VIP"
                        else -> "Regular"
                    }
                }


                val (date, time) = remember(currentShow) {
                    if (currentShow == null) {
                        Pair("", "")
                    } else {
                        try {
                            // Parse date (e.g., "2025-07-13" -> "Jul 13, 2025")
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateObj = dateFormat.parse(currentShow!!.date)
                            val dateStr = SimpleDateFormat(
                                "MMM dd, yyyy", Locale.getDefault()
                            ).format(dateObj!!)

                            // Parse time with multiple format attempts
                            val timeStr = try {
                                // Try "HH:mm" (e.g., "22:00")
                                val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val timeObj = timeFormat24.parse(currentShow!!.startTime)
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timeObj!!)
                            } catch (e1: ParseException) {
                                try {
                                    // Try "hh:mm a" (e.g., "4:00 PM")
                                    val timeFormatWithMinutes =
                                        SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    val timeObj =
                                        timeFormatWithMinutes.parse(currentShow!!.startTime)
                                    SimpleDateFormat(
                                        "hh:mm a", Locale.getDefault()
                                    ).format(timeObj!!)
                                } catch (e2: ParseException) {
                                    try {
                                        // Try "ha" (e.g., "4PM")
                                        val timeFormatWithoutMinutes =
                                            SimpleDateFormat("ha", Locale.getDefault())
                                        val timeObj = timeFormatWithoutMinutes.parse(
                                            currentShow!!.startTime.uppercase(Locale.getDefault())
                                        )
                                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                                            timeObj!!
                                        )
                                    } catch (e3: ParseException) {
                                        Log.e(
                                            "TimeParse",
                                            "Error parsing time: ${currentShow!!.startTime}",
                                            e3
                                        )
                                        ""
                                    }
                                }
                            }

                            Pair(dateStr, timeStr)
                        } catch (e: Exception) {
                            Log.e("DateParse", "Error parsing date: ${currentShow!!.date}", e)
                            Pair("", "")
                        }
                    }
                }


                Log.d("date", "SelectSeaat: $date")
                Log.d("Time", "SelectSeaat: $time")

                val ticket = Ticket(
                    date = date,
                    time = time,
                    section = sectionType,
                    seats = seatNumbers
                )
                Log.d("Ticket", "SelectSeaat: ${ticket.seats}")
                Log.d("SeatNumber", seatNumbers)
                Log.d("Price", totalPrice.toString())

//                Button(onClick = {
//                    val userId = viewModel.getUserId() // User ID yahan se lo
//                    viewModel.bookSelectedSeats(userId) // Booking start karo
//                }) {
//                    Text("Buy")
//                }
//            }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Bottom

                ) {
                    key(selectedSeats, seats) {
                        BuySummaryCard(
                            date = date,
                            time = time,
                            section = sectionType,
                            seats = seatNumbers,
                            total = "$${"%.2f".format(totalPrice)}",
                            onBuyClick = {
                                val userId = viewModel.getUser() // Yahan userId lo
                                viewModel.bookSelectedSeats(userId) // Booking yahin se hogi
                                currentShow?.movieId?.let { movieId ->
                                    viewModel.selectedTab.value = 1
                                    viewModel.MovieId.value = movieId
                                    viewModel.setTicket(ticket)


                                }
                            }

                        )
                    }
                }
                LaunchedEffect(bookingCompleted) {
                    if (bookingCompleted) {
                        navController.navigate("Main") {
                            popUpTo("Main") { inclusive = true }
                        }
                        viewModel.resetBookingCompleted()
                    }
                }

            }

        }
    }
}

@Composable
fun SeatLayout(
    seats: List<Seat>,
    selectedSeats: List<Long>,
    onSeatClick: (Long) -> Unit,
    showToast: (String) -> Unit,
) {
    // Show loading indicator if seats are empty
    if (seats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    // Handle case where we have unexpected number of seats
    if (seats.size != 49) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Unexpected seat count: ${seats.size} (expected 49)",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Retry loading */ }) {
                Text("Retry Loading Seats")
            }
        }
        return
    }

    // Sort seats by seatNumber
    val sortedSeats = seats.sortedBy { it.seatNumber }

    // Define row configurations (size and space positions)
    val rowConfigs = listOf(
        Pair(6, listOf(3)),   // Row 1: 6 seats, space after 3rd seat
        Pair(8, listOf(4)),   // Row 2: 8 seats, space after 4th seat
        Pair(8, listOf()),   // Row 3: 8 seats, space after 4th seat
        Pair(9, emptyList()), // Row 4: 9 seats, no spaces
        Pair(9, emptyList()), // Row 5: 9 seats, no spaces
        Pair(9, emptyList())  // Row 6: 9 seats, no spaces
    )

    // Create rows with boundary checks
    val seatRows = mutableListOf<List<Seat>>()
    var currentIndex = 0

    for ((rowSize, _) in rowConfigs) {
        // Ensure we don't go beyond the list size
        if (currentIndex >= sortedSeats.size) break

        val endIndex = minOf(currentIndex + rowSize, sortedSeats.size)
        val row = sortedSeats.subList(currentIndex, endIndex)
        seatRows.add(row)
        currentIndex = endIndex
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        seatRows.forEachIndexed { rowIndex, rowSeats ->
            val spaceIndexes = rowConfigs.getOrNull(rowIndex)?.second ?: emptyList()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                rowSeats.forEachIndexed { index, seat ->
                    // Add space if needed
                    if (spaceIndexes.contains(index)) {
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                    if (rowIndex.equals(1)) {
                        Spacer(modifier = Modifier.height(45.dp))
                    }
                    val isSelected = selectedSeats.contains(seat.seatId)
                    val color = when {
                        seat.isBooked -> Color.White
                        isSelected -> Color(0xFF60FFCA) // Green
                        else -> Color(0xFFB6116B)      // Red
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.seat),
                            contentDescription = "Seat ${seat.seatNumber}",
                            colorFilter = ColorFilter.tint(color),
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    if (seat.isBooked) {
                                        showToast("Seat already booked!")
                                    } else {
                                        onSeatClick(seat.seatId)
                                    }
                                })

//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "${seat.seatNumber}",
//                            color = Color.White,
//                            fontSize = 12.sp
//                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SelectionGuidence() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            Modifier.size(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = CircleShape,
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Text("Reserved", color = Color.Gray)

        Spacer(modifier = Modifier.width(20.dp))
        Card(
            Modifier.size(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFB6116B)),
            shape = CircleShape
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Text("Available", color = Color.Gray)

        Spacer(modifier = Modifier.width(20.dp))
        Card(
            Modifier.size(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF60FFCA)),
            shape = CircleShape
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Text("Selected", color = Color.Gray)
    }
}

//fun SelectionGuidence() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, end = 20.dp),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Card(
//            Modifier.size(10.dp)
//                .fillMaxWidth(),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            shape = CircleShape,
//        ) {
//
//        }
//        Spacer(modifier = Modifier.width(10.dp))
//        Text("Reserved", color = Color.Gray)
//
//        Spacer(modifier = Modifier.width(20.dp))
//        Card(
//            Modifier.size(10.dp),
//            colors = CardDefaults.cardColors(containerColor = Color(0xFFB6116B)),
//            shape = CircleShape
//        ) {}
//        Spacer(modifier = Modifier.width(10.dp))
//        Text("Available", color = Color.Gray)
//
//        Spacer(modifier = Modifier.width(20.dp))
//        Card(
//            Modifier.size(10.dp),
//            colors = CardDefaults.cardColors(containerColor = Color(0xFF60FFCA)),
//            shape = CircleShape
//        ) {}
//        Spacer(modifier = Modifier.width(10.dp))
//        Text("Selected", color = Color.Gray)
//    }
//
//}


@Composable
fun BuySummaryCard(
    date: String = "April 23, 2022",
    time: String = "6 p.m.",
    section: String = "VIP Section",
    seats: String = "Seat 9,10",
    total: String = "$30",
    onBuyClick: () -> Unit = {},
) {
    Log.d("Seats", "BuySummaryCard: $seats")

    var seatList = seats.split(",").mapNotNull { it.trim().toIntOrNull() }
    val vipSeats = seatList.filter { it in 1..20 }.sorted()
    val regularSeats = seatList.filter { it > 20 }.sorted()

    val vip = vipSeats
    val regular = regularSeats
//    Log.d("Seats", "BuySummaryCard: $vipSeats")
//    Log.d("Seats", "BuySummaryCard: $regularSeats")
    Box(
        modifier = Modifier.fillMaxWidth()
//            .height(216.dp)
    ) {
        // Main card surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .height(216.dp)
//                .clip(RoundedCornerShape(topStart = 40.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF421D90), Color(0xFF634BB3), Color(0xFFAC4EB5)
                        )
                    )
                )
                .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
//                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = "Date", tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$date • $time", color = Color.White)
                }
                Spacer(modifier = Modifier.height(20.dp))

                // VIP Section
                if (vipSeats.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = "Seat",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIP Section • Seats:",
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.padding(start = 32.dp, top = 8.dp)) {
                        vipSeats.chunked(6).forEach { seatChunk ->
                            Row {
                                seatChunk.forEach { seat ->
                                    val allVip = seat == vip.last()
                                    Text(
                                        text = if(allVip) "$seat" else "$seat,",
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 5.dp , end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Space between sections
                Spacer(modifier = Modifier.height(30.dp))

                // Regular Section
                if (regularSeats.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = "Seat",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Regular Section • Seats:",
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.padding(start = 32.dp, top = 8.dp)) {
                        regularSeats.chunked(6).forEach { seatChunk ->
                            Row {
                                seatChunk.forEach { seat ->
                                    val allRegular = seat == regular.last()
                                    Text(
                                        text = if(allRegular) "$seat" else "$seat,",
                                        color = Color.White,
                                        modifier = Modifier.padding(start=5.dp,end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ShoppingCart, contentDescription = "Total", tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Total: $total", color = Color.White)
                }
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp)
                .size(110.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 90.dp, topEnd = 2.dp, bottomEnd = 2.dp, bottomStart = 90.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3C096C), Color(0xFF5A189A)
                        )
                    )
                ), contentAlignment = Alignment.Center
        ) {
//                onClick = { onBuyClick() },
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable(onClick = onBuyClick)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF60FFCA), Color(0x0060FFCA)
                            ), start = Offset(0f, 0f), end = Offset(0f, 80f)
                        ), shape = CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xC88631B6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Buy", color = Color.White)
                }
            }
        }
    }
}


//                if (vipSeats.isNotEmpty()) {
//
//                    Row() {
//                        Icon(
//                            Icons.Default.ConfirmationNumber,
//                            contentDescription = "Seat",
//                            tint = Color.White
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Text(
//                            "VIP Section   • \t\t\t Seat ", color = Color.White,
//                        )
//
//
//                    }
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        vipSeats.chunked(3).forEach { seatChunk ->
//                            Row(
//                                modifier = Modifier.width(45.dp),
//                                horizontalArrangement = Arrangement.Start
//                            ) {
//                                Text(
//                                    "\n ${seatChunk.joinToString(", ")}", color = Color.White
//                                )
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(30.dp))
//
//                if (regularSeats.isNotEmpty()) {
//                    Row() {
//                        Icon(
//                            Icons.Default.ConfirmationNumber,
//                            contentDescription = "Seat",
//                            tint = Color.White
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Text(
//                            "REGULAR Section   • \t\t\t Seat ", color = Color.White
//                        )
//                    }
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        regularSeats.chunked(7).forEach { seatChunk ->
//                            Row(
//                                modifier = Modifier.width(45.dp),
//                                horizontalArrangement = Arrangement.Start
//                            ) {
//                                Text(
//                                    "\n ${regularSeats.joinToString(", ")}", color = Color.White
//                                )
//                            }
//                        }
//
//
//                    }
//                }
