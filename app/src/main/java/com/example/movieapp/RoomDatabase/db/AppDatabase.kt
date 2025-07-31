package com.example.movieapp.RoomDatabase.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.movieapp.RoomDatabase.Dao.AuthDao
import com.example.movieapp.RoomDatabase.Dao.BookingDao
import com.example.movieapp.RoomDatabase.Dao.MovieDao
import com.example.movieapp.RoomDatabase.Dao.SeatDao
import com.example.movieapp.RoomDatabase.Dao.ShowTimeDao
import com.example.movieapp.RoomDatabase.Dao.TheaterDao
import com.example.movieapp.RoomDatabase.Dao.movieTheaterDao
import com.example.movieapp.RoomDatabase.dbModels.Authentication
import com.example.movieapp.RoomDatabase.dbModels.Booking
import com.example.movieapp.RoomDatabase.dbModels.Movie
import com.example.movieapp.RoomDatabase.dbModels.Seat
import com.example.movieapp.RoomDatabase.dbModels.ShowTime
import com.example.movieapp.RoomDatabase.dbModels.Theater
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [Movie::class, Theater::class, ShowTime::class, Seat::class, Booking::class, Authentication::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun theaterDao(): TheaterDao
    abstract fun showTimeDao(): ShowTimeDao
    abstract fun seatDao(): SeatDao
    abstract fun bookingDao(): BookingDao
    abstract fun movieTheaterDao():movieTheaterDao
    abstract fun authDao(): AuthDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_db"
                )

                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
//
//val MIGRATION_5_6 = object : Migration(5, 6) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        // Add columns
//        database.execSQL("ALTER TABLE `show_times ` ADD COLUMN year TEXT NOT NULL DEFAULT ''")
//        database.execSQL("ALTER TABLE `show_times ` ADD COLUMN month TEXT NOT NULL DEFAULT ''")
//
//        // Optional: Update year and month based on date column
//        database.execSQL("""
//            UPDATE `show_times `
//            SET year = SUBSTR(date, -4),
//                month = CASE
//                    WHEN SUBSTR(date, 1, 3) = 'Mon' THEN 'January'
//                    WHEN SUBSTR(date, 1, 3) = 'Tue' THEN 'February'
//                    WHEN SUBSTR(date, 1, 3) = 'Wed' THEN 'March'
//                    WHEN SUBSTR(date, 1, 3) = 'Thu' THEN 'April'
//                    WHEN SUBSTR(date, 1, 3) = 'Fri' THEN 'May'
//                    WHEN SUBSTR(date, 1, 3) = 'Sat' THEN 'June'
//                    WHEN SUBSTR(date, 1, 3) = 'Sun' THEN 'July'
//                    ELSE 'Unknown'
//                END
//        """)
//    }
//}