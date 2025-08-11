package com.example.movieapp.MovieRepository

//import com.example.movieapp.RoomDatabase.Dao.AuthDao
//import com.example.movieapp.RoomDatabase.dbModels.Authentication
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Transaction
import com.example.movieapp.RoomDatabase.Dao.AuthDao
import com.example.movieapp.RoomDatabase.Dao.BookingDao
import com.example.movieapp.RoomDatabase.Dao.LocalUserTicketDao
import com.example.movieapp.RoomDatabase.Dao.MovieDao
import com.example.movieapp.RoomDatabase.Dao.SeatDao
import com.example.movieapp.RoomDatabase.Dao.ShowTimeDao
import com.example.movieapp.RoomDatabase.Dao.TheaterDao
import com.example.movieapp.RoomDatabase.Dao.movieTheaterDao
import com.example.movieapp.RoomDatabase.dbModels.Authentication
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.BookingWithDetails
import com.example.movieapp.RoomDatabase.dbModels.LocalUserTickets
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Theater
import com.example.movieapp.Screens.Authentication.PersonProfile
import com.example.movieapp.Screens.DataStore.AuthPreference
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
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
    val authPrefs: AuthPreference,
    val localUserTickets : LocalUserTicketDao

) {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllMovies() = movieDao.getAllMovies()

    private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val timeSlots = listOf("10AM", "12PM", "2PM", "4PM", "6PM", "8PM", "10PM")
//    private val month = listOf("10AM", "12PM", "2PM", "4PM", "6PM", "8PM", "10PM")



    suspend fun getBookingsWithDetailsForUser(userId: String): List<BookingWithDetails> {
        return movieTheaterDao.getBookingsWithDetailsForUser(userId)
    }

    suspend fun getBookingById(BookingId: Long): List<BookingWithDetails> {
        return movieTheaterDao.getBookingById(BookingId)
    }

    //deletePastBookings
    suspend fun deletePastBookings(date: String, time: String) {
        bookingDao.deletePastBookings(date, time)
    }

    //SignIn
    suspend fun registerUser(email: String, password: String): Boolean {

            AuthDao.insertUser(Authentication(email = email, password = password))
            authPrefs.setUserLoggedIn(true)
            authPrefs.savedLoggedInEmail(email)
            return true

    }

    //LogIn
    suspend fun loginUser(email: String, password: String): Boolean {
        val user = AuthDao.login(email, password)
        if (user != null) {
            authPrefs.setUserLoggedIn(true)
            authPrefs.savedLoggedInEmail(email)
            return true
        } else {
            return false
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
        val firebaseTheaters = firestore
            .collection("theaters")
            .get().await()

        val totalTheaters= theaterDao.getTotalCount()
        if (firebaseTheaters.size() < 20 ) {
            val theater = Theater()
            val theaterId = theaterDao.insert(theater) // Room insert → ID milega

            val firebaseTheater = theater.copy(theaterId = theaterId)

            // Firebase insert
            firestore.collection("theaters")
                .document(theaterId.toString())
                .set(firebaseTheater)

            val seats = (1..49).map { seatNum ->
                Seat(
                    theaterId = theaterId,
                    seatNumber = seatNum,
                    section = if (seatNum <= 20) "VIP" else "Regular"
                )
            }

            val seatIds = seatDao.insertAll(seats)

            val seatsWithIds = seatIds.mapIndexed { index, id ->
                seats[index].copy(seatId = id)
            }

            // Firebase seats insert
            seatsWithIds.forEach { seat ->
                firestore.collection("seats")
                    .document(seat.seatId.toString())
                    .set(seat)
            }

            return theaterId
        }
        if(totalTheaters < 20){

            val theaters = getAllTheatersFromFirebase()
             theaterDao.insertAll(theaters)


            val seats = mutableListOf<Seat>()
            theaters.forEach { theater ->
                val theaterId = theater.theaterId // Firebase id used as Room id as well

                (1..49).forEach { seatNum ->
                    val section = if (seatNum <= 20) "VIP" else "Regular"
                    val seat = Seat(
                        theaterId = theaterId,
                        seatNumber = seatNum,
                        section = section
                    )
                    seats.add(seat)
                }
            }
            seatDao.insertAll(seats)
        }

        return -1
    }



    suspend fun insertTheaterFromFirebase(theater: Theater) {
        theaterDao.insert(theater)
        val seatList = (1..49).map { seatNum ->
            Seat(
                theaterId = theater.theaterId, // Firebase ID use ho rahi hai
                seatNumber = seatNum,
                section = if (seatNum <= 20) "VIP" else "Regular"
            )
        }

        // Insert all seats into Room
        val generatedIds = seatDao.insertAll(seatList)
        val seatsWithIds = seatList.zip(generatedIds).map { (seat, id) ->
            seat.copy(seatId = id)
        }

        val batch = Firebase.firestore.batch()
        val collectionRef = Firebase.firestore.collection("seats")

        seatsWithIds.forEach { seat ->
            val docRef = collectionRef.document(seat.seatId.toString())
            batch.set(docRef, seat)

        }
        batch.commit().await()
    }


    suspend fun scheduleMovieShows(movie: Movie) {
        try {
            val requiredShows = 3
            val theaters = theaterDao.getAllTheaters()
//            val theaters = getAllTheatersFromFirebase()
            Log.d("theaters", "scheduleMovieShows: ${theaters.size}")


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
                    val showTimeObj = ShowTime(
                        movieId = movie.movieId,
                        theaterId = theater.theaterId,
                        dayOfWeek = availableDay,
                        date = showDate.toString(),
                        startTime = slot,
                        endTime = calculateEndTime(slot, movie.duration),
                        month = month,
                        year = year
                    )

                    val insertedId = showTimeDao.insert(showTimeObj)
                    val showTimeWithId = showTimeObj.copy(showId = insertedId)
                    firestore.collection("show_times")
                        .add(showTimeWithId)
                        .addOnSuccessListener {
                            Log.d("scheduleMovieShows", "ShowTime added with ID: ${it.id}")
                        }
                        .addOnFailureListener {
                            Log.e("scheduleMovieShows", "Error adding ShowTime", it)
                        }
                } else {
                    // Create new theater with a random slot
                    val newTheaterId = createNewTheater()
                    val slot = timeSlots.random()
                    val showTimeObj = ShowTime(
                        movieId = movie.movieId,
                        theaterId = newTheaterId,
                        dayOfWeek = availableDay,
                        date = showDate.toString(),
                        startTime = slot,
                        endTime = calculateEndTime(slot, movie.duration),
                        month = month,
                        year = year
                    )

                    val insertedId = showTimeDao.insert(showTimeObj)
                    val showTimeWithId = showTimeObj.copy(showId = insertedId)
                    firestore.collection("show_times")
                        .add(showTimeWithId)
                        .addOnSuccessListener {
                            Log.d("scheduleMovieShows", "ShowTime added with ID: ${it.id}")
                        }
                        .addOnFailureListener {
                            Log.e("scheduleMovieShows", "Error adding ShowTime", it)
                        }
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
            val showTimeObj = ShowTime(
                movieId = movie.movieId,
                theaterId = 1,
                dayOfWeek = fallbackDay,
                date = fallbackDate.toString(),
                startTime = fallbackSlot,
                endTime = calculateEndTime(fallbackSlot, movie.duration),
                month = fallbackMonth,
                year = fallbackYear
            )

            val insertedId = showTimeDao.insert(showTimeObj)
            val showTimeWithId = showTimeObj.copy(showId = insertedId)
            firestore.collection("show_times")
                .add(showTimeWithId)
                .addOnSuccessListener {
                    Log.d("scheduleMovieShows", "ShowTime added with ID: ${it.id}")
                }
                .addOnFailureListener {
                    Log.e("scheduleMovieShows", "Error adding ShowTime", it)
                }

        }
    }


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


    suspend fun getShowsForMovieFromFirestore(movieId: Int): List<ShowTime> {
        return try {
            val snapshot = Firebase.firestore
                .collection("show_times")
                .whereEqualTo("movieId", movieId)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(ShowTime::class.java) }

        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSeatsForTheaterFromFirestore(theaterId: Long): List<Seat> {
        return try {
            val snapshot = firestore.collection("seats")
                .whereEqualTo("theaterId", theaterId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Seat::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBookedSeatsFromFirestore(showTimeId: Long): List<Long> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("showTimeId", showTimeId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.getLong("seatId") }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun addSeatsToFirestore(seats: List<Seat>) {
        seats.forEach { seat ->
            firestore.collection("seats").document(seat.seatId.toString()).set(seat).await()
        }
    }


    suspend fun getAllTheatersFromFirebase(): List<Theater> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("theaters")
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Theater::class.java)
            }
//            snapshot.documents.mapNotNull { doc ->
//                val theater = doc.toObject(Theater::class.java)
//                theater?.copy(theaterId = doc.id.toLong()) // overwrite id with Firebase doc id
//            }

        } catch (e: Exception) {
            emptyList()
        }
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
        seatDao.updateSeat(seat.copy(booked = true))
    }








    suspend fun getShowTimeFromFirestore(showTimeId: Long): ShowTime? {
        return try {
            val snapshot = firestore.collection("show_times")
                .whereEqualTo("showId", showTimeId)
                .get()
                .await()
            val showTimes = snapshot.documents.mapNotNull { it.toObject(ShowTime::class.java) }
            showTimes.firstOrNull() // agar mila to return, warna null
        } catch (e: Exception) {
            Log.d("Firestore", "Error fetching ShowTime for ID: $showTimeId", e)
            null
        }
    }

    suspend fun getMovieFromFirestore(movieId: Long): Movie? {
        return try {
            val snapshot = firestore.collection("movies")
                .whereEqualTo("movieId", movieId)
                .get()
                .await()
            val showTimes = snapshot.documents.mapNotNull { it.toObject(Movie::class.java) }
            showTimes.firstOrNull()
        }catch (e: Exception){
            Log.d("Firestore", "Error fetching MovieId for ID: $movieId", e)
            null
        }
    }

    suspend fun getSeatsFromFirebase(theaterId:Long):List<Seat>{
        val snapshot = firestore.collection("seats")
            .whereEqualTo("theaterId", theaterId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Seat::class.java) }
    }

}
