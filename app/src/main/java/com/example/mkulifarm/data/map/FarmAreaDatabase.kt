package com.example.mkulifarm.data.map
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FarmAreaEntity::class], version = 1, exportSchema = false)
abstract class FarmAreaDatabase : RoomDatabase() {
    abstract fun farmAreaDao(): FarmAreaDao

    companion object {
        @Volatile
        private var INSTANCE: FarmAreaDatabase? = null

        fun getDatabase(context: Context): FarmAreaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmAreaDatabase::class.java,
                    "farm_area_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
