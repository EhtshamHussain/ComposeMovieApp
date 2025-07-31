package com.example.movieapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.log
import com.example.movieapp.MovieRepository.MovieTheaterRepository
import com.example.movieapp.Pagination.MoviePagingSource
import com.example.movieapp.RoomDatabase.dbModels.Authentication
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.BookingWithDetails
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.QrTicket
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Ticket
import com.example.movieapp.RoomDatabase.dbModels.UserTicket
import com.example.movieapp.Screens.DataStore.AuthPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MovieViewModel(
    val repository: MovieTheaterRepository,


) : ViewModel() {

    var hasInitialized by mutableStateOf(false)
        private set

    private val apiKey = "db785eee03bd40a03a7f2a4843263d46"

    // Popular Movies
    val popularMovies = Pager(PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = false)) {
        MoviePagingSource(apiKey, "now_playing")
    }.flow.cachedIn(viewModelScope)
//    popular
    //top_rated
    // Top Rated Movies
    val topRatedMovies = Pager(PagingConfig(pageSize = 20,prefetchDistance = 5,
        enablePlaceholders = false)) {
        MoviePagingSource(apiKey, "upcoming")
    }.flow.cachedIn(viewModelScope)

    //     Trending Movies
    val trendingMovies = Pager(PagingConfig(pageSize = 20,prefetchDistance = 5,
        enablePlaceholders = false)) {
        MoviePagingSource(apiKey, "top_rated")
    }.flow.cachedIn(viewModelScope)
//    trending


    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    // Search Results Flow
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

    private  val _currentTicket = mutableStateOf<Ticket?>(null)
    val currentTicket : State<Ticket?>  = _currentTicket

    private val _TicketDetails = mutableStateOf<QrTicket?>(null)
    val ticketDetails : State<QrTicket?> = _TicketDetails


    // New states for current show and selected seats
    private val _currentShowTime = mutableStateOf<ShowTime?>(null)
    val currentShowTime: State<ShowTime?> = _currentShowTime

    private val _UserDetail = mutableStateOf<Authentication?>(null)
    val userDetail : State<Authentication?>  = _UserDetail

    // For Booking complete
    private val _bookingCompleted = MutableStateFlow(false)
    val bookingCompleted: StateFlow<Boolean> = _bookingCompleted



    private val _scannedTicketDetails = mutableStateOf<List<BookingWithDetails>>(emptyList())
    val scannedTicketDetails: State<List<BookingWithDetails>> = _scannedTicketDetails

//    private val _selectedSeats = mutableStateListOf<Long>()
//    val selectedSeats: List<Long> get() = _selectedSeats



    fun deletePastBookings(date : String , time :String){
        viewModelScope.launch {
            repository.deletePastBookings(date , time)
        }
    }

    fun logout() {
        repository.logoutUser()
        _UserDetail.value = null
    }

    private val _userTickets = mutableStateOf<List<UserTicket>>(emptyList())
    val userTickets: State<List<UserTicket>> = _userTickets

    var selectShowId by mutableStateOf<Long>(0)

    fun loadUserOnAppState(){
        viewModelScope.launch {
        val email = repository.authPrefs.getLoggedInEmail()
            if(email!=null){
            _UserDetail.value = repository.AuthDao.getUserByEmail(email.toString())
            }
        }
    }

    fun getUser():String {
         val user = repository.authPrefs.getLoggedInEmail()
        return  user.toString()
    }
    //SignIn
    fun register(email:String, password:String , onResult: (Boolean)->Unit){
        viewModelScope.launch {
            val success = repository.registerUser(email,password)
//            _UserDetail.value = repository.AuthDao.getUserByEmail(email)
            onResult(success)
        }
    }




    //LogIn
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginUser(email, password)
//            _UserDetail.value = repository.AuthDao.getUserByEmail(email)
            onResult(success)
        }
    }

    //SignOut
//    fun logout():Boolean{
//
//    }


    fun loadUserTickets(userId: String) {
        viewModelScope.launch {
            val bookings = repository.getBookingsWithDetailsForUser(userId)
            val grouped = bookings.groupBy { it.showTimeId }

            val tickets = grouped.map { (showTimeId, bookingList) ->
                val firstBooking = bookingList.first()
                UserTicket(
                    ticketId = firstBooking.bookingId,
                    movieTitle = firstBooking.movieTitle,
                    posterUrl = firstBooking.posterUrl,
                    date = firstBooking.showDate,
                    time = firstBooking.showTime,
                    section = firstBooking.section, // Adjust if seats can have different sections
                    seats = bookingList.map { it.seatNumber.toString() },
                    showTimeId = showTimeId,
                    price = bookingList.sumOf { it.price }
                )

            }
            _userTickets.value = tickets.reversed()
        }
    }
     fun getTicketDetailsById(BookngId : Long) {
         viewModelScope.launch {
             val result = repository.getBookingById(BookngId)
             _scannedTicketDetails.value = result

         }
     }

    fun setTicket(ticket: Ticket){
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
            _showTimes.value = repository.showTimeDao.getShowsForMovie(movie.movieId)
        }
    }

    fun loadSeatsForShow(showTimeId: Long) {
        viewModelScope.launch {
            try {
                _seats.value = emptyList()
                val show = repository.showTimeDao.getShowsForMovie(_selectedMovie.value?.movieId ?: 0)
                    .firstOrNull { it.showId == showTimeId }
                if (show == null) {
                    Log.d("loadSeatsForShow", "Show not found for showId: $showTimeId")
                    // Set mock data as a fallback
                    _seats.value = (1..49).map { seatNum ->
                        Seat(
                            seatId = seatNum.toLong(),
                            theaterId = 1,
                            seatNumber = seatNum,
                            section = if (seatNum <= 15) "VIP" else "Regular",
                            isBooked = false
                        )
                    }
                } else {
                    _currentShowTime.value = show
                    var allSeats = repository.seatDao.getSeatsForTheater(show.theaterId)
                    if (allSeats.size != 49) {
                        val newSeats = (1..49).map { seatNum ->
                            Seat(
                                theaterId = show.theaterId,
                                seatNumber = seatNum,
                                section = if (seatNum <= 15) "VIP" else "Regular"
                            )
                        }
                        repository.seatDao.insertAll(newSeats)
                        allSeats = newSeats
                    }
                    val bookedSeats = try {
                        repository.bookingDao.getBookedSeatsForShow(showTimeId)
                    } catch (e: Exception) {
                        emptyList<Long>()
                    }
                    _seats.value = allSeats.map { seat ->
                        seat.copy(isBooked = bookedSeats.contains(seat.seatId))
                    }
                }
            } catch (e: Exception) {
                Log.e("loadSeats", "Error loading seats", e)
                _seats.value = (1..49).map { seatNum ->
                    Seat(
                        seatId = seatNum.toLong(),
                        theaterId = 1,
                        seatNumber = seatNum,
                        section = if (seatNum <= 15) "VIP" else "Regular",
                        isBooked = false
                    )
                }
            }
        }
    }




    suspend fun initializeSelectedMovie(movie: Movie) {

        val existingMovieId = repository.movieDao.getMovieById(movie.movieId.toLong())
        if (existingMovieId == null) {
            repository.movieDao.insertMovie(movie)
            repository.scheduleMovieShows(movie)
            selectMovie(movie)
//                loadSeatsForShow(showTimes.value.firstOrNull()?.showId ?: 0)
        } else {
            selectMovie(movie)
        }
    }




    fun bookSelectedSeats(userId: String) {
        viewModelScope.launch {
            try {
                val showId = _currentShowTime.value?.showId ?: return@launch

                _selectedSeats.value.forEach { seatId ->
                    // Get seat details
                    val seat = _seats.value.firstOrNull { it.seatId == seatId } ?: return@forEach

                    // Calculate price
                    val price = if (seat.section == "VIP") 15.0 else 10.0

                    // Create booking
                    val booking = Booking(
                        userId = userId,
                        showTimeId = showId,
                        seatId = seatId,
                        price = price
                    )

                    // Save to database
                    repository.bookingDao.insertBooking(booking)

                    // Update local state
                    _seats.value = _seats.value.map {
                        if (it.seatId == seatId) it.copy(isBooked = true) else it
                    }
                }

                // ✅ Clear selected seats after booking
                _selectedSeats.value = emptyList()
                _bookingCompleted.value = true

                loadUserTickets(userId)

            } catch (e: Exception) {
                Log.e("BookingError", e.message ?: "Booking failed")
            }
        }
    }
    // State reset of Booking
    fun resetBookingCompleted() {
        _bookingCompleted.value = false
    }



    fun toggleSeatSelection(seatId: Long) {
        val seat = _seats.value.firstOrNull { it.seatId == seatId }
        if (seat?.isBooked == true) return

        _selectedSeats.value = if (_selectedSeats.value.contains(seatId)) {
            _selectedSeats.value - seatId
        } else {
            _selectedSeats.value + seatId
        }
    }


    fun getCurrentTicketDetails(ticket: UserTicket){
        val ticket =  QrTicket(
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
                    if (it.seatId == seatId) it.copy(isBooked = true)
                    else it
                }
            } catch (e: Exception) {
                Log.d("BookSeatError", e.message.toString())
            }
        }
    }


}




