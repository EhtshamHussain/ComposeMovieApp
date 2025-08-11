package com.example.movieapp.RoomDatabase.dbModels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val movieId: Int = 0,
    val title: String="",
    val posterUrl : String?="" ,
    val duration : Int = 120
)
//    val showDays : List<String>
//{
//    fun getShowDaysFormatted(): String = showDays.joinToString(", ")
//}

@Entity(tableName = "theaters")
data class Theater(
    @PrimaryKey(autoGenerate = false)
    val theaterId: Long = 0,
    val isAvailable: Boolean=true,

)

@Entity(
    tableName = "show_times ",
    foreignKeys = [
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["movieId"],
            childColumns = ["movieId"]
        ),
        ForeignKey(
            entity = Theater::class,
            parentColumns = ["theaterId"],
            childColumns = ["theaterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)

data class ShowTime (
    @PrimaryKey(autoGenerate = true)
    val showId: Long = 0,
    val movieId: Int =0,
    val theaterId: Long=0,
    val dayOfWeek: String="",
    val date : String="" ,
    val startTime: String="",
    val endTime: String="",
    val year: String="" ,
    val month: String="",
)


@Entity(
    tableName = "seats",
    foreignKeys = [
        ForeignKey(
            entity = Theater::class,
            parentColumns = ["theaterId"],
            childColumns = ["theaterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

)
data class Seat(
    @PrimaryKey(autoGenerate = true) val seatId: Long = 0,
    val theaterId: Long=0,
    val seatNumber: Int=0,// 1 to 49
    val section: String="",// "VIP" ya "Regular"
    val booked: Boolean = false
)


@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = ShowTime::class,
            parentColumns = ["showId"],
            childColumns = ["showTimeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Seat::class,
            parentColumns = ["seatId"],
            childColumns = ["seatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["showTimeId", "seatId"], unique = true)]

)
data class Booking(
    @PrimaryKey(autoGenerate = false) val bookingId: Long = 0,
    val userId: String="",
    val showTimeId: Long=0,
    val seatId: Long=0,
    val price: Double=0.0,
    )

data class BookingWithDetails(
    val bookingId: Long=0,
    val userId: String="",
    val showTimeId: Long=0,
    val seatId: Long=0,
    val price: Double=0.0,
    val movieTitle: String="",
    val posterUrl: String="",
    val showDate: String="",
    val showTime: String="",
    val seatNumber: Int=0,
    val section: String=""
)

data class Ticket(
    val date: String,
    val time: String,
    val section: String,
    val seats: String,
)
//@Serializable
data class UserTicket(
    val ticketId : Long,
    val movieTitle: String,
    val posterUrl: String,
    val date: String,
    val time: String,
    val section: String, // Assuming all seats for one show are in the same section
    val seats: List<String>, // List of seat numbers
    val showTimeId: Long,
    val price: Double
)





@Entity(
    tableName = "Authentication"
)
data class Authentication(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val email: String="",
    val password: String=""
)

data class QrTicket(
    val ticketId: Long,
    val movieTitle: String,
    val date: String,
    val time: String,
    val price: Double
)


@Entity(tableName = "LocalUserTickets")
data class LocalUserTickets(
    @PrimaryKey val ticketId : Long,
    val movieTitle: String,
    val posterUrl: String,
    val date: String,
    val time: String,
    val section: String, // Assuming all seats for one show are in the same section
    val seats: List<String>, // List of seat numbers
    val showTimeId: Long,
    val price: Double
)


//For Locking Seats
data class SeatUI(
    val seatId: Long,
    val seatNumber: Int,
    val section: String,
    val isBooked: Boolean,
    val isLockedByOther: Boolean,
    val isSelected: Boolean
)