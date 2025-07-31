package com.example.movieapp.Model

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class DayItem(
    val day: String,   // "Sat"
    val date: String,  // "23"
    val times: List<TimeItem> // ["19:00", "20:00"]
)

data class TimeItem(
    val showId: Long,
    val time: String
)


val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//
//
//@Composable
//fun DateTimePicker() {
//
//    val days = (1..30).map { date ->
//        val dayName = dayNames[(date - 1) % dayNames.size] // repeat days
//        val times = (1..24).map { hour -> "${"%02d".format(hour)}:00" } // 01:00 to 24:00
//        DayItem(dayName, date.toString(), times)
//    }
//
//    var selectedDay by remember { mutableStateOf<DayItem?>(null) }
//    var selectedTime by remember { mutableStateOf<String?>(null) }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        // Dinon ka Horizontal Scroll
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
////            contentPadding = PaddingValues(horizontal = 32.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            items(days) { day ->
//                DayBox(
//                    day = day,
//                    isSelected = day == selectedDay,
//                    onSelect = { selectedDay = it }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Selected Day ke Time Slots
//        selectedDay?.times?.takeIf { it.isNotEmpty() }?.let { times ->
//            Text("Select Time:", modifier = Modifier.padding(bottom = 8.dp), color = Color.White)
//            LazyRow(
//                horizontalArrangement = Arrangement.spacedBy(22.dp),
//            contentPadding = PaddingValues(horizontal = 32.dp),
//                modifier = Modifier.fillMaxWidth()
//                    .scale(scaleX = 2f , scaleY = 2f )
//            ) {
//                items(times) { time ->
//                    TimeBox(
//                        time = time,
//                        isSelected = time == selectedTime,
//                        onSelect = { selectedTime = it }
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun DayBox(day: DayItem, isSelected: Boolean, onSelect: (DayItem) -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clickable { onSelect(day) }
            .scale(if(isSelected)1.5f else 1f)
            .background(
                color = if(isSelected)    Color(0xFF6200EA) else Color(0xFF2C3E50),

                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = CenterHorizontally) {
            Text(day.day, color = Color.White, fontWeight = FontWeight.Bold)
            Text(day.date, color = Color.White, fontSize = 24.sp)
        }
    }
}

@Composable
fun TimeBox(time: String, isSelected: Boolean, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(40.dp)
            .clickable { onSelect(time) }
            .background(
                color = if (isSelected) Color(0xFF6C3ADB) else
                    Color(0xFF2C3E50),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(time, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

//swipeIn,
//swipeOut,
//brush = Brush.horizontalGradient(
//0.0f to Color.Red,
//0.3f to Color.Green,
//1.0f to Color.Blue,
//startX = 0.0f,
//endX = 100.0f
//)