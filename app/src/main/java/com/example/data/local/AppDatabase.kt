package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SensitivityProfile

@Database(entities = [SensitivityProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensitivityDao(): SensitivityDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val created = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mx_sense_ai.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                 .build()
                instance = created
                created
            }
        }
    }
}
