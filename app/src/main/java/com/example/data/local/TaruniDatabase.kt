package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedProject::class], version = 1, exportSchema = false)
abstract class TaruniDatabase : RoomDatabase() {
    abstract fun taruniDao(): TaruniDao

    companion object {
        @Volatile
        private var INSTANCE: TaruniDatabase? = null

        fun getInstance(context: Context): TaruniDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaruniDatabase::class.java,
                    "taruni_studio.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
