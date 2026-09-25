package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FavoriteItem

@Database(entities = [FavoriteItem::class], version = 1, exportSchema = false)
abstract class HelpyDatabase : RoomDatabase() {
    abstract fun helpyDao(): HelpyDao

    companion object {
        @Volatile
        private var INSTANCE: HelpyDatabase? = null

        fun getDatabase(context: Context): HelpyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HelpyDatabase::class.java,
                    "helpy_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
