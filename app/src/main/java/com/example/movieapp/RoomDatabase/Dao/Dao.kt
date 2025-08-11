package com.example.movieapp.RoomDatabase.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.movieapp.RoomDatabase.dbModels.Authentication
//import com.example.movieapp.RoomDatabase.dbModels.Authentication
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.BookingWithDetails
import com.example.movieapp.RoomDatabase.dbModels.LocalUserTickets

import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Theater

import kotlinx.coroutines.flow.Flow
import java.nio.charset.CodingErrorAction.IGNORE


//Movie Dao
@Dao
interface MovieDao  {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)

    @Query("Select * from movies")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE movieId = :id")
    suspend fun getMovieById(id: Long): Movie?
}


@Dao
interface TheaterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(theater: Theater):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(theater: List<Theater>) : List<Long>

    @Query("SELECT * FROM theaters")
    suspend fun getAllTheaters(): List<Theater>

    @Query("SELECT * FROM theaters where isAvailable=1 Limit 1")
    suspend fun getAvailableTheater(): Theater?

    @Query(" Update theaters set isAvailable=:isAvailable where theaterId=:theaterId")
    suspend fun updateTheaterAvailability(theaterId: Int, isAvailable: Boolean)

    @Query("Select Count(*) from theaters")
    suspend fun getTotalCount():Int
}

//Show Dao
@Dao
interface ShowTimeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(showTime: ShowTime):Long

    @Query("SELECT * from `show_times ` where movieId=:movieId")
    suspend fun getShowsForMovie(movieId: Int): List<ShowTime>

   @Query("Select * from `show_times ` where theaterId=:theaterId And date=:date And startTime=:startTime")
    suspend fun isSlotAvailable(theaterId: Int, date: String, startTime: String): ShowTime?

    @Query("Select * from `show_times ` where theaterId=:theaterId and date=:day")
    suspend fun getShowsForTheaterAndDay(theaterId: Long , day : String ): List<ShowTime>
}

@Dao
interface SeatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(seat: Seat)

    @Insert
    suspend fun insertAll(seats: List<Seat>):List<Long>


    @Transaction
    @Query("SELECT * FROM seats WHERE seatId = :seatId AND booked = 0 ")
    suspend fun lockSeatForBooking(seatId: Long): Seat?

    @Query("SELECT * FROM seats WHERE theaterId = :theaterId")
    suspend fun getSeatsForTheater(theaterId: Long): List<Seat>

    @Query("SELECT * FROM seats WHERE seatId= :seatId")
    suspend fun getSeat(seatId: Long)  : Seat?

    @Update
    suspend fun updateSeat(seat: Seat)
}

@Dao
interface  BookingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(booking: Booking): Long

    @Query("SELECT seatId FROM bookings WHERE showTimeId = :showId")
    suspend fun getBookedSeatsForShow(showId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking):Long

    @Query("SELECT COUNT(*) FROM bookings WHERE showTimeId = :showId")
    suspend fun getBookedSeatsCount(showId: Long): Long


    @Query("Select count(*) from bookings where showTimeId=:showTimeId and seatId=:seatId")
    suspend fun isSeatBooked(showTimeId: Long, seatId: Long): Int

    @Query("Select * from bookings")
    suspend fun getAllBooking(): List<Booking>

    @Query("Delete from bookings where userId =:userId")
    suspend fun deleteBooking(userId: String)

    @Query("DELETE FROM bookings WHERE showTimeId IN (SELECT showTimeId FROM `show_times ` WHERE date < :currentDate OR (date = :currentDate AND startTime < :currentTime))")
    suspend fun deletePastBookings(currentDate: String, currentTime: String)

}

@Dao
interface movieTheaterDao{

    @Query("""
    SELECT b.bookingId, b.userId, b.showTimeId, b.seatId, b.price,
           m.title AS movieTitle, m.posterUrl,
           st.date AS showDate, st.startTime AS showTime,
           s.seatNumber, s.section
    FROM bookings b
    JOIN `show_times ` st ON b.showTimeId = st.showId
    JOIN movies m ON st.movieId = m.movieId
    JOIN seats s ON b.seatId = s.seatId
    WHERE b.userId = :userId
""")
    suspend fun getBookingsWithDetailsForUser(userId: String): List<BookingWithDetails>



    @Query("""
    SELECT b.bookingId, b.userId, b.showTimeId, b.seatId, b.price,
           m.title AS movieTitle, m.posterUrl,
           st.date AS showDate, st.startTime AS showTime,
           s.seatNumber, s.section
    FROM bookings b
    JOIN `show_times ` st ON b.showTimeId = st.showId
    JOIN movies m ON st.movieId = m.movieId
    JOIN seats s ON b.seatId = s.seatId
    WHERE b.bookingId = :BookingId
""")
    suspend fun getBookingById(BookingId: Long): List<BookingWithDetails>
}

@Dao
interface AuthDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: Authentication)

    @Query("SELECT * FROM Authentication WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): Authentication?

    @Query("SELECT * FROM Authentication WHERE email = :email")
    suspend fun getUserByEmail(email: String): Authentication?

    @Query("DELETE FROM Authentication WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)
}


@Dao
interface LocalUserTicketDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserTicket(localUserTickets: LocalUserTickets)


    @Query("SELECT * FROM LocalUserTickets ORDER BY ticketId DESC")
    fun getAllLocalTickets(): Flow<List<LocalUserTickets>>
}










