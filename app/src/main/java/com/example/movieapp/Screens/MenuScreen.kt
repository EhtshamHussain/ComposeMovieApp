package com.example.movieapp.Screens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapp.Model.TimeItem
import com.example.movieapp.MovieViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun MenuScreen(navController: NavController) {

}

@Composable
fun ZigZagScrollList(
    viewModel: MovieViewModel,
    navController: NavController,
    onClick: (TimeItem?) -> Unit,
) {
    Log.d("SelectMovie", "ZigZagScrollList:${viewModel.selectedMovie}")
    // Step 1: Observe showTimes from ViewModel
    val showTimes by viewModel.showTimes

    // Step 2: Group showTimes by date and convert into DayItem format
    val groupedByDate = showTimes.groupBy { it.date }
    val dateTimeList = remember(showTimes) {
        groupedByDate.entries.sortedBy { it.key }.map { (date, shows) ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = try {
                dateFormat.parse(date)
            } catch (e: Exception) {
                null
            }
            val monthStr = dateObj?.let {
                SimpleDateFormat("MMM", Locale.getDefault()).format(it)
            } ?: ""
            val dayNumber = dateObj?.let {
                SimpleDateFormat("dd", Locale.getDefault()).format(it)
            } ?: date.filter { it.isDigit() }.takeLast(2)

            DayItem(
                day = shows.firstOrNull()?.dayOfWeek ?: "",
                date = date,
                month = monthStr,
                dayNumber = dayNumber,
                times = shows.map { show ->
                    TimeItem(
                        showId = show.showId,
                        time = show.startTime
                    )
                }
            )
        }
    }

    // Step 3: State variables for selection
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(-1) } // Shuru mein koi select nahi
    val selectedTimeIndices = remember { mutableStateMapOf<String, Int>() }

    // Step 4: Preselect default time index for each date
    LaunchedEffect(dateTimeList) {
        dateTimeList.forEachIndexed { i, item ->
            selectedTimeIndices[item.date] = 0
        }
    }

    val centerOffset by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.size / 2 } }

    // Step 5: LazyRow for zigzag scrolling
    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        itemsIndexed(dateTimeList) { index, item ->
            val firstVisible = listState.firstVisibleItemIndex
            val pseudoCenter = firstVisible + centerOffset
            val distance = abs(index - pseudoCenter)

            val offsetY by animateDpAsState(
                targetValue = when {
                    distance == 0 -> 0.dp
                    distance == 1 -> 30.dp
                    else -> 60.dp
                },
                animationSpec = tween(200),
                label = "OffsetY"
            )

            val scale by animateFloatAsState(
                targetValue = if (distance == 0) 1.1f else 1f,
                label = "Scale"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .width(80.dp)
                        .graphicsLayer {
                            translationY = offsetY.toPx()
                            scaleX = scale
                            scaleY = scale
                        }
                        .padding(8.dp)
                ) {
                    val currentTimeIndex = selectedTimeIndices[item.date] ?: 0
                    val currentTime = item.times.getOrNull(currentTimeIndex)
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedIndex == index) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFB6116B),
                                            Color(0xFF2E1371)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(150f, 150f)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF2E1371),
                                            Color(0xFF21232F)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(150f, 150f)
                                    )
                                }
                            )
                            .clickable {
                                selectedIndex = index
                                currentTime?.let {
                                    Log.d("CheckShowId", "ZigZagScrollList: $it")
                                    onClick(it)
                                }
                            }
                            .border(
                                width = 0.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFE53BB), Color(0xFFFE53BB)),
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.day,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = item.month,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = item.dayNumber,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedIndex == index) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFB6116B),
                                            Color(0xFF2E1371)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(100f, 100f)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF2E1371),
                                            Color(0xFF21232F)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(100f, 100f)
                                    )
                                }
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.times.getOrNull(currentTimeIndex)?.time ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// Update DayItem data class to include month and dayNumber
data class DayItem(
    val day: String,
    val date: String, // Original date for internal use
    val month: String, // e.g., "Jul"
    val dayNumber: String, // e.g., "10"
    val times: List<TimeItem>,
)