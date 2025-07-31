package com.example.movieapp.MovieRepository

import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.room.Transaction
import com.example.movieapp.RoomDatabase.Dao.AuthDao
//import com.example.movieapp.RoomDatabase.Dao.AuthDao
import com.example.movieapp.RoomDatabase.Dao.BookingDao
import com.example.movieapp.RoomDatabase.Dao.MovieDao
import com.example.movieapp.RoomDatabase.Dao.SeatDao
import com.example.movieapp.RoomDatabase.Dao.ShowTimeDao
import com.example.movieapp.RoomDatabase.Dao.TheaterDao
import com.example.movieapp.RoomDatabase.Dao.movieTheaterDao
import com.example.movieapp.RoomDatabase.dbModels.Authentication
//import com.example.movieapp.RoomDatabase.dbModels.Authentication
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.BookingWithDetails
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Theater
import com.example.movieapp.Screens.DataStore.AuthPreference
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

    class MovieTheaterRepository(
        val movieDao: MovieDao,
        val theaterDao: TheaterDao,
        val showTimeDao: ShowTimeDao,
        val seatDao: SeatDao,
        val bookingDao: BookingDao,
        val movieTheaterDao: movieTheaterDao,
        val AuthDao: AuthDao,
        val authPrefs : AuthPreference
    ) {
    fun getAllMovies() = movieDao.getAllMovies()

    private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val timeSlots = listOf("10AM", "12PM", "2PM", "4PM", "6PM", "8PM", "10PM")
//    private val month = listOf("10AM", "12PM", "2PM", "4PM", "6PM", "8PM", "10PM")


    suspend fun getBookingsWithDetailsForUser(userId: String): List<BookingWithDetails> {
        return movieTheaterDao.getBookingsWithDetailsForUser(userId)
    }

        suspend fun getBookingById(BookingId : Long): List<BookingWithDetails>{
            return  movieTheaterDao.getBookingById(BookingId)
        }

        //deletePastBookings
        suspend fun deletePastBookings(date : String , time :String){
            bookingDao.deletePastBookings(date,time)
        }

    //SignIn
    suspend fun registerUser(email: String, password: String): Boolean {
        val existUser = AuthDao.getUserByEmail(email)
        return if (existUser == null) {
            AuthDao.insertUser(Authentication(email = email, password = password))
            authPrefs.setUserLoggedIn(true)
            authPrefs.savedLoggedInEmail(email)
            true
        } else {
            false
        }
    }
    //LogIn
    suspend fun loginUser(email: String, password: String): Boolean {
        val user = AuthDao.login(email, password)
        if(user != null){
            authPrefs.setUserLoggedIn(true)
            authPrefs.savedLoggedInEmail(email)
            return true
        }else{
            return  false
        }
    }

    //Logout
    fun logoutUser() {
        authPrefs.setUserLoggedIn(false)
    }

    fun isLoggedIn(): Boolean {
        return authPrefs.isUserLoggedIn()
    }

        fun getSavedEmail(): String? {
            return authPrefs.getLoggedInEmail()
        }

    // Theaters & Seats
    suspend fun createNewTheater(): Long {

        val theaterId = theaterDao.insert(Theater())
        val seats = (1..49).map { seatNum ->
            Seat(
                theaterId = theaterId,
                seatNumber = seatNum,
                section = if (seatNum <= 20) "VIP" else "Regular"
            )
        }
        seatDao.insertAll(seats)
        return theaterId
    }

//
//    suspend fun scheduleMovieShows(movie: Movie) {
//        try {
//            val requiredShows = 3
//            val theaters = theaterDao.getAllTheaters()
//            val scheduleDays = mutableSetOf<String>()
//            val currentDate = LocalDate.now() // Get current date, e.g., 2025-07-21
//
//            repeat(requiredShows) {
//                // Find an available day
//                val availableDay = daysOfWeek.firstOrNull { day ->
//                    val showDate = getNextDateForDay(currentDate, day)
//                    !scheduleDays.contains(day) && theaters.any { theater ->
//                        timeSlots.any { slot ->
//                            !isTimeSlotBooked(theater.theaterId, showDate.toString(), slot)
//                        }
//                    }
//                } ?: return // Exit if no day is available
//                scheduleDays.add(availableDay)
//
//                // Calculate the show date, month, and year
//                val showDate = getNextDateForDay(currentDate, availableDay) // e.g., 2025-07-22
//                val month = showDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) // e.g., "July"
//                val year = showDate.year.toString() // e.g., "2025"
//
//                // Find available theater and slot
//                var isScheduled = false
//                for (theater in theaters) {
//                    for (slot in timeSlots) {
//                        if (!isTimeSlotBooked(theater.theaterId, showDate.toString(), slot)) {
//                            showTimeDao.insert(
//                                ShowTime(
//                                    movieId = movie.movieId,
//                                    theaterId = theater.theaterId,
//                                    dayOfWeek = availableDay, // e.g., "Tue"
//                                    date = showDate.toString(), // e.g., "2025-07-22"
//                                    startTime = slot,
//                                    endTime = calculateEndTime(slot, movie.duration),
//                                    month = month, // e.g., "July"
//                                    year = year // e.g., "2025"
//                                )
//                            )
//                            isScheduled = true
//                            break
//                        }
//                    }
//                    if (isScheduled) break
//                }
//
//                // Create new theater if no slot is found
//                if (!isScheduled) {
//                    val newTheaterId = createNewTheater()
//                    showTimeDao.insert(
//                        ShowTime(
//                            movieId = movie.movieId,
//                            theaterId = newTheaterId,
//                            dayOfWeek = availableDay,
//                            date = showDate.toString(),
//                            startTime = timeSlots.first(),
//                            endTime = calculateEndTime(timeSlots.first(), movie.duration),
//                            month = month,
//                            year = year
//                        )
//                    )
//                }
//            }
//        } catch (e: Exception) {
//            Log.d("scheduleMovieShows", "Error scheduling shows", e)
//            // Fallback schedule with proper date
//            val fallbackDate = LocalDate.now()
//            showTimeDao.insert(
//                ShowTime(
//                    movieId = movie.movieId,
//                    theaterId = 1,
//                    dayOfWeek = fallbackDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), // e.g., "Mon"
//                    date = fallbackDate.toString(), // e.g., "2025-07-21"
//                    startTime = "18:00",
//                    endTime = "20:00",
//                    month = fallbackDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH), // e.g., "July"
//                    year = fallbackDate.year.toString() // e.g., "2025"
//                )
//            )
//        }
//    }

    suspend fun scheduleMovieShows(movie: Movie) {
        try {
            val requiredShows = 3
            val theaters = theaterDao.getAllTheaters()
            val scheduleDays = mutableSetOf<String>()
            val currentDate = LocalDate.now() // e.g., 2025-07-21
            val timeSlots = listOf(
                "10:00",
                "12:00",
                "14:00",
                "16:00",
                "18:00",
                "20:00",
                "22:00"
            ) // 10 AM to 11 PM range

            repeat(requiredShows) { showIndex ->
                // Find available days
                val availableDays = daysOfWeek.filter { day ->
                    // Spread shows across weeks: showIndex * 7 days apart
                    val showDate = getNextDateForDay(currentDate.plusWeeks(showIndex.toLong()), day)
                    !scheduleDays.contains(day) && theaters.any { theater ->
                        timeSlots.any { slot ->
                            !isTimeSlotBooked(theater.theaterId, showDate.toString(), slot)
                        }
                    }
                }
                if (availableDays.isEmpty()) return // Exit if no day is available
                val availableDay = availableDays.random() // Pick a random day
                scheduleDays.add(availableDay)

                // Calculate show date, month, and year with week offset
                val showDate =
                    getNextDateForDay(currentDate.plusWeeks(showIndex.toLong()), availableDay)
                val month = showDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                val year = showDate.year.toString()

                // Find available theater and slot
                val availableOptions = mutableListOf<Pair<Theater, String>>()
                for (theater in theaters) {
                    for (slot in timeSlots.shuffled()) { // Shuffle slots for variety
                        if (!isTimeSlotBooked(theater.theaterId, showDate.toString(), slot)) {
                            availableOptions.add(theater to slot)
                        }
                    }
                }
                if (availableOptions.isNotEmpty()) {
                    val (theater, slot) = availableOptions.random() // Pick random theater and slot
                    showTimeDao.insert(
                        ShowTime(
                            movieId = movie.movieId,
                            theaterId = theater.theaterId,
                            dayOfWeek = availableDay,
                            date = showDate.toString(),
                            startTime = slot,
                            endTime = calculateEndTime(slot, movie.duration),
                            month = month,
                            year = year
                        )
                    )
                } else {
                    // Create new theater with a random slot
                    val newTheaterId = createNewTheater()
                    val slot = timeSlots.random()
                    showTimeDao.insert(
                        ShowTime(
                            movieId = movie.movieId,
                            theaterId = newTheaterId,
                            dayOfWeek = availableDay,
                            date = showDate.toString(),
                            startTime = slot,
                            endTime = calculateEndTime(slot, movie.duration),
                            month = month,
                            year = year
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("scheduleMovieShows", "Error scheduling shows", e)
            // Fallback schedule with random slot
            val fallbackDate = LocalDate.now().plusDays(1) // Next day
            val fallbackDay = fallbackDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val fallbackMonth = fallbackDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            val fallbackYear = fallbackDate.year.toString()
            val timeSlots = listOf("10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00")
            val fallbackSlot = timeSlots.random()
            showTimeDao.insert(
                ShowTime(
                    movieId = movie.movieId,
                    theaterId = 1,
                    dayOfWeek = fallbackDay,
                    date = fallbackDate.toString(),
                    startTime = fallbackSlot,
                    endTime = calculateEndTime(fallbackSlot, movie.duration),
                    month = fallbackMonth,
                    year = fallbackYear
                )
            )
        }
    }
//
//    private fun calculateEndTime(startTime: String, duration: Int): String {
//        val (hours, minutes) = startTime.split(":").map { it.toInt() }
//        val startMinutes = hours * 60 + minutes
//        val endMinutes = startMinutes + duration
//        val endHours = endMinutes / 60
//        val endMins = endMinutes % 60
//        return String.format("%02d:%02d", endHours, endMins)
//    }

//    fun calculateEndTime(start: String, duration: Int): String {
//        // Simple format like "2PM"
//        val format = SimpleDateFormat("ha", Locale.US)
//
//        // Start time ko convert karo Date mein
//        val time = format.parse(start.uppercase(Locale.getDefault())) ?: return ""
//
//        // Time mein minutes add karo
//        val calendar = Calendar.getInstance()
//        calendar.time = time
//        calendar.add(Calendar.MINUTE, duration)
//
//        // Final time wapis "2PM" format mein do
//        return format.format(calendar.time)
//    }

    fun calculateEndTime(start: String, duration: Int): String {
        // Convert input from "HH:mm" to "ha"
        val inputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val outputFormat = SimpleDateFormat("ha", Locale.US)

        val parsed = inputFormat.parse(start) ?: return ""

        val calendar = Calendar.getInstance()
        calendar.time = parsed
        calendar.add(Calendar.MINUTE, duration)

        return outputFormat.format(calendar.time)
    }


    //is Time Slot book
    private suspend fun isTimeSlotBooked(theaterId: Long, day: String, slot: String): Boolean {
        return showTimeDao.getShowsForTheaterAndDay(theaterId, day)
            .any { it.startTime == slot }
    }


    // Seat booking with concurrency handling
    @Transaction
    suspend fun bookSeat(userId: String, showTimeId: Long, seatId: Long) {

        //check if show is full
        val bookedCount = bookingDao.getBookedSeatsCount(showTimeId)
        if (bookedCount >= 49) {
            throw Exception("Theater full! All 49 seats are booked")
        }


        // Check if seat is already booked
//        if (bookingDao.isSeatBooked(showTimeId, seatId) > 0) {
//            throw Exception("Seat already booked")
//        }
        // Get seat details for pricing
        val seat = seatDao.getSeat(seatId) ?: throw Exception("Invalid Seat")
        val price = if (seat.section == "VIP") 30.0 else 20.0

        // Create booking
        val inserted = bookingDao.insert(
            Booking(
                userId = userId,
                showTimeId = showTimeId,
                seatId = seatId,
                price = price
            )
        )
        if (inserted == -1L) {
            throw Exception("Seat already booked")
        }
        //update seat status
        seatDao.updateSeat(seat.copy(isBooked = true))
    }


    // Helper function to get the next date for a specific day of the week
    private fun getNextDateForDay(currentDate: LocalDate, dayOfWeek: String): LocalDate {
        val day = when (dayOfWeek) {
            "Mon" -> DayOfWeek.MONDAY
            "Tue" -> DayOfWeek.TUESDAY
            "Wed" -> DayOfWeek.WEDNESDAY
            "Thu" -> DayOfWeek.THURSDAY
            "Fri" -> DayOfWeek.FRIDAY
            "Sat" -> DayOfWeek.SATURDAY
            "Sun" -> DayOfWeek.SUNDAY
            else -> throw IllegalArgumentException("Invalid day: $dayOfWeek")
        }
        return currentDate.with(TemporalAdjusters.nextOrSame(day))
    }




}


