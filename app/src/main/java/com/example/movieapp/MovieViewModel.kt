package com.example.movieapp

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.TypeConverter
import com.example.movieapp.MovieRepository.MovieTheaterRepository
import com.example.movieapp.Pagination.MoviePagingSource
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.BookingWithDetails
import com.example.movieapp.RoomDatabase.dbModels.LocalUserTickets
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.QrTicket
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.SeatUI
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Theater
import com.example.movieapp.RoomDatabase.dbModels.Ticket
import com.example.movieapp.RoomDatabase.dbModels.UserTicket
import com.example.movieapp.Screens.Authentication.PersonProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MovieViewModel(
    val repository: MovieTheaterRepository,


    ) : ViewModel() {


    //FIREBASE INSTANCES
    //firebase instance
    private val firebaseAuth = FirebaseAuth.getInstance()

    //firebase Firestore Database
    private val firestore = FirebaseFirestore.getInstance()


    var hasInitialized by mutableStateOf(false)
        private set

    private val apiKey = "db785eee03bd40a03a7f2a4843263d46"

    // Popular Movies
    val notPlaying = Pager(
        PagingConfig(
            pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
        )
    ) {
        MoviePagingSource(apiKey, "now_playing")
    }.flow.cachedIn(viewModelScope)

    //    popular
    //top_rated
    // Top Rated Movies
    val upComing = Pager(
        PagingConfig(
            pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
        )
    ) {
        MoviePagingSource(apiKey, "upcoming")
    }.flow.cachedIn(viewModelScope)

    //     Trending Movies
    val trendingMovies = Pager(
        PagingConfig(
            pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
        )
    ) {
        MoviePagingSource(apiKey, "top_rated")
    }.flow.cachedIn(viewModelScope)
//    trending


    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    // Search Results Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = searchQuery.flatMapLatest { query ->
        if (query.isNotEmpty()) {
            Pager(PagingConfig(pageSize = 20)) {
                MoviePagingSource(apiKey, "search", query)
            }.flow
        } else {
            flowOf(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


    // movie Repository working

    val movies = repository.getAllMovies()

    val selectedTab = mutableStateOf(0)
    val MovieId = mutableStateOf(0)


    private val _selectedMovie = mutableStateOf<Movie?>(null)
    val selectedMovie: State<Movie?> = _selectedMovie

    private val _showTimes = mutableStateOf<List<ShowTime>>(emptyList())
    val showTimes: State<List<ShowTime>> = _showTimes

    private val _seats = mutableStateOf<List<Seat>>(emptyList())
    val seats: State<List<Seat>> = _seats

    private val _selectedSeats = mutableStateOf<List<Long>>(emptyList())
    val selectedSeats: List<Long> get() = _selectedSeats.value

    private val _isSelected = mutableStateOf<List<Long>>(emptyList())
    val isSelected: List<Long> get() = _isSelected.value

    private val _currentTicket = mutableStateOf<Ticket?>(null)
    val currentTicket: State<Ticket?> = _currentTicket

    private val _TicketDetails = mutableStateOf<QrTicket?>(null)
    val ticketDetails: State<QrTicket?> = _TicketDetails


    // New states for current show and selected seats
    private val _currentShowTime = mutableStateOf<ShowTime?>(null)
    val currentShowTime: State<ShowTime?> = _currentShowTime

    private val _UserDetail = mutableStateOf<PersonProfile?>(null)
    val userDetail: State<PersonProfile?> = _UserDetail

    // For Booking complete
    private val _bookingCompleted = MutableStateFlow(false)
    val bookingCompleted: StateFlow<Boolean> = _bookingCompleted


    private val _scannedTicketDetails = mutableStateOf<List<BookingWithDetails>>(emptyList())
    val scannedTicketDetails: State<List<BookingWithDetails>> = _scannedTicketDetails


    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading


    private val _seatsUI = mutableStateOf<List<SeatUI>>(emptyList())
    val seatsUI: State<List<SeatUI>> = _seatsUI


    init {
        viewModelScope.launch {
            repository.localUserTickets.getAllLocalTickets().collect { tickets ->
                _LocalUserTickets.value = tickets

            }
        }
    }


//    private val _selectedSeats = mutableStateListOf<Long>()
//    val selectedSeats: List<Long> get() = _selectedSeats


    fun deletePastBookings(date: String, time: String) {
        viewModelScope.launch {
            repository.deletePastBookings(date, time)
        }
    }

    fun logout() {
        repository.logoutUser()
        _UserDetail.value = null
    }

    private val _userTickets = mutableStateOf<List<UserTicket>>(emptyList())
    val userTickets: State<List<UserTicket>> = _userTickets


    private val _LocalUserTickets = mutableStateOf<List<LocalUserTickets>>(emptyList())
    val LocalUserTickets: State<List<LocalUserTickets>> = _LocalUserTickets


    var selectShowId by mutableStateOf<Long>(0)

    fun loadUserOnAppState() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val email = firebaseUser?.email

            if (email != null) {
                val snapshot =
                    FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", email)
                        .get().await()

                val user = snapshot.documents.firstOrNull()?.toObject(PersonProfile::class.java)
                Log.d("FIREBASE_USER", "Email from Firestore: ${user?.email}")
                _UserDetail.value = user
            } else {
                Log.d("FIREBASE_USER", "Firebase user or email is null")
            }
        }
    }


    fun getUser(): String {
        val user = repository.authPrefs.getLoggedInEmail()
        return user.toString()
    }

    //SignIn
    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    val profile = PersonProfile(id = userId, email = email)

                    // Save to Firestore
                    Firebase.firestore.collection("Users").document(userId).set(profile)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                val success = repository.registerUser(email, password)
                                onResult(success)
                            }
                        }.addOnFailureListener {
                            onResult(false)
                        }
                } else {
                    onResult(false)
                }
            }
    }


    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModelScope.launch {
                    val success = repository.loginUser(email, password)

                    // Optionally: Agar Room mein user nahi mila, to insert karo
                    if (!success) {
                        repository.registerUser(email, password)  // optional
                        onResult(true)
                    } else {
                        onResult(true)
                    }
                }
            } else {
                onResult(false)
            }
        }
    }


    fun loadUserTickets(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val bookingsSnapshot =
                firestore.collection("bookings").whereEqualTo("userId", userId).get().await()

            val bookings = bookingsSnapshot.toObjects(Booking::class.java)


            val localTickets = mutableListOf<LocalUserTickets>()
            val grouped = bookings.groupBy { it.showTimeId }


            val tickets = grouped.mapNotNull { (showTimeId, bookingList) ->
                val firstBooking = bookingList.firstOrNull()
                if (firstBooking == null) {
                    Log.e("LoadTickets", "No booking found in group")
                    null
                } else {
                    val showTime = repository.getShowTimeFromFirestore(showTimeId)
                    if (showTime == null) {
                        Log.e("LoadTickets", "ShowTime not found for ID: $showTimeId")
                        null
                    } else {
                        val movie = repository.getMovieFromFirestore(showTime.movieId.toLong())
                        if (movie == null) {
                            Log.e("LoadTickets", "Movie not found for ID: ${showTime.movieId}")
                            null
                        } else {
                            val seats =
                                repository.getSeatsFromFirebase(showTime.theaterId) ?: emptyList()

                            val bookedSeatIds = bookingList.map { it.seatId }
                            val bookedSeats = seats.filter { it.seatId in bookedSeatIds }

                            val seatNumbers = bookedSeats.map { it.seatNumber.toString() }
                            val sections = bookedSeats.map { it.section }

                            val section = if (sections.all { it == sections.firstOrNull() }) {
                                sections.firstOrNull() ?: "Unknown"
                            } else {
                                "Mixed"
                            }

                            val ticket = UserTicket(
                                ticketId = firstBooking.bookingId,
                                movieTitle = movie.title,
                                posterUrl = movie.posterUrl.toString(),
                                date = showTime.date,
                                time = showTime.startTime,
                                section = section,
                                seats = seatNumbers,
                                showTimeId = showTimeId,
                                price = bookingList.sumOf { it.price })


                            val localTicket = LocalUserTickets(
                                ticketId = firstBooking.bookingId,
                                movieTitle = movie.title,
                                posterUrl = movie.posterUrl.toString(),
                                date = showTime.date,
                                time = showTime.startTime,
                                section = section,
                                seats = seatNumbers,
                                showTimeId = showTimeId,
                                price = bookingList.sumOf { it.price })


                            repository.localUserTickets.inserTicket(localTicket)
                            _isLoading.value = false
//                            localTickets.add(localTicket)
//                            ticket

                        }
//
                    }
                }
            }


//            _LocalUserTickets.value = localTickets.reversed()
//            _userTickets.value = tickets.reversed()


        }
    }


    fun getTicketDetailsById(BookngId: Long) {
        viewModelScope.launch {
            val result = repository.getBookingById(BookngId)
            _scannedTicketDetails.value = result

        }
    }

    fun setTicket(ticket: Ticket) {
        _currentTicket.value = ticket
    }

    fun loadShowTimesForMovie(movieId: Long) {
        viewModelScope.launch {
            _showTimes.value = repository.showTimeDao.getShowsForMovie(movieId.toInt())
        }
    }


    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
        viewModelScope.launch {

//            _showTimes.value = repository.showTimeDao.getShowsForMovie(movie.movieId)

            firestore.collection("show_times").whereEqualTo("movieId", movie.movieId).get()
                .addOnSuccessListener { snapShot ->
                    val showTimes = snapShot.toObjects(ShowTime::class.java)
                    _showTimes.value = showTimes

                }.addOnFailureListener {
                    Log.d("Firestore", "Failed to fetch show_times: ", it)
                }

        }
    }

//    fun createTheaters(){
//        viewModelScope.launch {
//            repository.createNewTheater()
//        }
//    }


    fun loadSeatsForShow(showTimeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _seats.value = emptyList()
//                val show = repository.showTimeDao.getShowsForMovie(_selectedMovie.value?.movieId ?: 0)
//                    .firstOrNull { it.showId == showTimeId }
                val showList =
                    repository.getShowsForMovieFromFirestore(_selectedMovie.value?.movieId ?: 0)
                val show = showList.firstOrNull { it.showId == showTimeId }
                Log.d("ShowTimeId", "loadSeatsForShow: ${show}")
                if (show == null) {
                    Log.d("loadSeatsForShow123", "Show not found for showId: $showTimeId")
                    // Set mock data as a fallback
                    _seats.value = (1..49).map { seatNum ->
                        Seat(
                            seatId = seatNum.toLong(),
                            theaterId = 1,
                            seatNumber = seatNum,
                            section = if (seatNum <= 15) "VIP" else "Regular",
                            booked = false
                        )
                    }
                    return@launch
                } else {
                    _currentShowTime.value = show
//                    var allSeats = repository.seatDao.getSeatsForTheater(show.theaterId)

                    var allSeats = repository.getSeatsForTheaterFromFirestore(show.theaterId)

                    Log.d("SeatsLoaded", "Loaded seats: ${allSeats.size}")

//                    if (allSeats.size != 49) {
//
//                        val newSeats = (1..49).map { seatNum ->
//                            Seat(
////                                seatId = seatNum.toLong(),
//                                theaterId = show.theaterId,
//                                seatNumber = seatNum,
//                                section = if (seatNum <= 15) "VIP" else "Regular"
//                            )
//                        }
//                        repository.addSeatsToFirestore(newSeats)
//                        repository.seatDao.insertAll(newSeats)
//                        allSeats = newSeats
//                    }
//                    val bookedSeats = try {

//                        repository.bookingDao.getBookedSeatsForShow(showTimeId)
//                        repository.getBookedSeatsFromFirestore(showTimeId)
//                    }
//                    catch (e: Exception) {
//                        emptyList<Long>()
//                    }
                    val bookedSeats = repository.getBookedSeatsFromFirestore(showTimeId)
                    val lockedSeatsSnapshot = firestore.collection("lockedSeats")
                        .whereEqualTo("showTimeId", showTimeId)
                        .get()
                        .await()
                    val lockedSeats = lockedSeatsSnapshot.documents.mapNotNull { doc ->
                        val seatId = doc.getLong("seatId")
                        val userId = doc.getString("userId")
                        val lockedAt = doc.getTimestamp("lockedAt")?.toDate()
                        val isExpired = lockedAt != null && (System.currentTimeMillis() - lockedAt.time) > 5 * 60 * 1000 // 5 minutes
                        if (isExpired) {
                            firestore.collection("lockedSeats").document("${showTimeId}_$seatId").delete()
                            null
                        } else {
                            seatId to userId
                        }
                    }.toMap()

                    val currentUserId = getUser()

                    _seatsUI.value = allSeats.map { seat ->
                        val isBooked = bookedSeats.contains(seat.seatId)
                        val lockedBy = lockedSeats[seat.seatId]
                        val isLockedByOther = lockedBy != null && lockedBy != currentUserId
                        val isSelected = _selectedSeats.value.contains(seat.seatId)
                        SeatUI(
                            seatId = seat.seatId,
                            seatNumber = seat.seatNumber,
                            section = seat.section,
                            isBooked = isBooked,
                            isLockedByOther = isLockedByOther,
                            isSelected = isSelected
                        )
                    }

//                    _seatsUI.value = allSeats.map { seat ->
//                        seat.copy(isBooked = bookedSeats.contains(seat.seatId))
//                    }
                }
            } catch (e: Exception) {
                Log.e("loadSeats", "Error loading seats", e)
                _seats.value = (1..49).map { seatNum ->
                    Seat(
                        seatId = seatNum.toLong(),
                        theaterId = 1,
                        seatNumber = seatNum,
                        section = if (seatNum <= 15) "VIP" else "Regular",
                        booked = false
                    )
                }
            }
        }
    }


    suspend fun initializeSelectedMovie(movie: Movie) {
        try {

            val movieDoc =
                firestore.collection("movies").document(movie.movieId.toString()).get().await()
            if (movieDoc.exists()) {

                selectMovie(movie)
            } else {
                firestore.collection("movies").document(movie.movieId.toString()).set(movie).await()
                repository.movieDao.insertMovie(movie)
                repository.scheduleMovieShows(movie)
                selectMovie(movie)
            }
        } catch (e: Exception) {
            Log.e("InitializeMovie", "Error initializing movie", e)
        }
    }
    fun bookSelectedSeats(userId: String) {
        viewModelScope.launch {
            try {
                val showId = _currentShowTime.value?.showId ?: return@launch
                Log.d("Booking", "bookSelectedSeats: ${showId}")
                _selectedSeats.value.forEach { seatId ->
                    Log.d("Booking", "bookSelectedSeats: ${seatId}")
                    val seat = _seatsUI.value.firstOrNull { it.seatId == seatId } ?: return@forEach
                    val price = if (seat.section == "VIP") 15.0 else 10.0
                    val bookingId = System.currentTimeMillis()
                    val booking = Booking(
                        bookingId = bookingId,
                        userId = userId,
                        showTimeId = showId,
                        seatId = seatId,
                        price = price
                    )


                    firestore.collection("bookings")
                        .document(bookingId.toString())
                        .set(booking)
                        .await()
                    releaseSeatLock(seatId, showId) // Lock release karo
                }
                _selectedSeats.value = emptyList()
                _bookingCompleted.value = true
                loadUserTickets(userId)
            } catch (e: Exception) {
                Log.e("BookingError", e.message ?: "Booking failed")
            }
        }
    }

//
//    fun bookSelectedSeats(userId: String) {
//        viewModelScope.launch {
//            try {
//                val showId = _currentShowTime.value?.showId ?: return@launch
//
//                _selectedSeats.value.forEach { seatId ->
//                    // Get seat details
//                    val seat = _seats.value.firstOrNull { it.seatId == seatId } ?: return@forEach
//
//                    // Calculate price
//                    val price = if (seat.section == "VIP") 15.0 else 10.0
//
//
//                    // Create booking
//                    val bookingId = System.currentTimeMillis()
//
//                    val booking = Booking(
//                        bookingId = bookingId,
//                        userId = userId,
//                        showTimeId = showId,
//                        seatId = seatId,
//                        price = price
//                    )
//
//                    // Save to database
////                    val bookingId = repository.bookingDao.insertBooking(booking)
//
//                    firestore.collection("bookings")
//                        .document(bookingId.toString())
//                        .set(booking)
//                        .await()
//                    releaseSeatLock(seatId, showId)
//
//                    // Update local state
////                    _seats.value = _seats.value.map {
////                        if (it.seatId == seatId) it.copy(booked = true) else it
////                    }
//                }
//
//                // ✅ Clear selected seats after booking
//                _selectedSeats.value = emptyList()
//                _bookingCompleted.value = true
//                loadUserTickets(userId)
//
//            } catch (e: Exception) {
//                Log.e("BookingError", e.message ?: "Booking failed")
//            }
//        }
//    }


    // State reset of Booking
    fun resetBookingCompleted() {
        _bookingCompleted.value = false
    }


    fun toggleSeatSelection(seatId: Long , toast: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {

            val seat = _seats.value.firstOrNull { it.seatId == seatId }
            if (seat?.booked == true) return@launch


            val showId = _currentShowTime.value?.showId ?: return@launch
            val userId = getUser()

             if (_selectedSeats.value.contains(seatId)) {

                 releaseSeatLock(seatId, showId)
                 _selectedSeats.value = _selectedSeats.value - seatId
            } else {
                 val locked = lockSeat(userId, seatId, showId) // Lock try karo
                 if (locked) {
                     _selectedSeats.value = _selectedSeats.value + seatId // Agar lock hua toh add karo
                 } else {
                     toast("Seat Already Booked by another user right now ")
                 }
            }
        }
    }

    suspend fun lockSeat(userId: String, seatId: Long, showId: Long): Boolean {
        val lockDocId = "${showId}_$seatId"
        val lockData = mapOf(
            "seatId" to seatId,
            "showTimeId" to showId,
            "userId" to userId,
            "lockedAt" to FieldValue.serverTimestamp()
        )

        return try {
            val docRef = firestore.collection("lockedSeats").document(lockDocId)
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                false // Already locked
            } else {
                docRef.set(lockData).await()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun releaseSeatLock(seatId: Long, showId: Long) {
        val lockDocId = "${showId}_$seatId"
        firestore.collection("lockedSeats").document(lockDocId).delete()
    }


    fun getCurrentTicketDetails(ticket: LocalUserTickets) {
        val ticket = QrTicket(
            ticketId = ticket.ticketId,
            movieTitle = ticket.movieTitle,
            date = ticket.date,
            time = ticket.time,
            price = ticket.price
        )
        _TicketDetails.value = ticket

    }

    fun bookSeat(userId: String, showTimeId: Long, seatId: Long) {
        viewModelScope.launch {
            try {
                repository.bookSeat(userId, showTimeId, seatId)
                //update UI state
                _seats.value = _seats.value.map {
                    if (it.seatId == seatId) it.copy(booked = true)
                    else it
                }
            } catch (e: Exception) {
                Log.d("BookSeatError", e.message.toString())
            }
        }
    }


    fun insertTheaterFromFirebase() {
        viewModelScope.launch {
            val snapShot = firestore.collection("theaters").get().await()
            snapShot.documents.forEach { document ->
                val theater = document.toObject(Theater::class.java)
                if (theater != null) {
                    repository.insertTheaterFromFirebase(theater)
                }
            }
        }
    }

}


class Converters {
    @TypeConverter
    fun fromList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<String> = value.split(",")
}




//
//fun bookSelectedSeats(userId: String) {
//    viewModelScope.launch {
//        try {
//            val showId = _currentShowTime.value?.showId ?: return@launch
//
//            _selectedSeats.value.forEach { seatId ->
//                val seat = _seats.value.firstOrNull { it.seatId == seatId } ?: return@forEach
//                val price = if (seat.section == "VIP") 15.0 else 10.0
//                val bookingId = System.currentTimeMillis()
//
//                val booking = Booking(
//                    bookingId = bookingId,
//                    userId = userId,
//                    showTimeId = showId,
//                    seatId = seatId,
//                    price = price
//                )
//
//                val seatLockedRef = firestore.collection("lockedSeats")
//                    .document("${showId}_$seatId")
//                val bookingRef = firestore.collection("bookings")
//                    .document(bookingId.toString())
//
//                // 🔹 Transaction to ensure atomic lock + booking
//                firestore.runTransaction { transaction ->
//                    val lockSnapshot = transaction.get(seatLockedRef)
//                    if (lockSnapshot.exists()) {
//                        throw Exception("Seat $seatId is already locked/booked.")
//                    }
//
//                    // Lock the seat
//                    transaction.set(
//                        seatLockedRef,
//                        mapOf(
//                            "lockedBy" to userId,
//                            "timestamp" to System.currentTimeMillis()
//                        )
//                    )
//
//                    // Save booking
//                    transaction.set(bookingRef, booking)
//                }
//                    .addOnSuccessListener {
//                        Log.d("Booking", "Seat $seatId booked successfully")
//                        // Update UI state immediately
//                        _seats.value = _seats.value.map {
//                            if (it.seatId == seatId) it.copy(booked = true) else it
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("Booking", "Failed to book seat $seatId: ${e.message}")
//                    }
//            }
//
//            // ✅ Clear selection and reload tickets
//            _selectedSeats.value = emptyList()
//            _bookingCompleted.value = true
//            loadUserTickets(userId)
//
//        } catch (e: Exception) {
//            Log.e("BookingError", e.message ?: "Booking failed")
//        }
//    }
//}
//





//    fun toggleSeatSelection(seatId: Long, userId: String , toast : (String) -> Unit) {
//        viewModelScope.launch {
//            val showId = _currentShowTime.value?.showId ?: return@launch
//            Log.d("SeatSelection", "Toggling seat: $seatId")
//            val seat = _seats.value.firstOrNull { it.seatId == seatId }
//            if (seat?.booked == true) return@launch
//            Log.d("SeatSelection", "Before update: ${_selectedSeats.value}")
//
//
////            _selectedSeats.value = if (_selectedSeats.value.contains(seatId)) {
////                _selectedSeats.value - seatId
////            } else {
////                _selectedSeats.value + seatId
////            }
//
//            if (_selectedSeats.value.contains(seatId)) {
//                releaseSeatLock(seatId, showId)
//                _selectedSeats.value = _selectedSeats.value - seatId
//                return@launch
//            }
//            val locked = lockSeat(userId, seatId, showId)
//            if(locked){
//                _selectedSeats.value = _selectedSeats.value + seatId
//            } else {
//                Log.d("SeatSelection", "Seat is already locked by another user.")
//                toast("Seat is already locked by another user.")
//
//            }
//
//
//
//            Log.d("SeatSelection", "After update: ${_selectedSeats.value}")
//        }
//
//    }
