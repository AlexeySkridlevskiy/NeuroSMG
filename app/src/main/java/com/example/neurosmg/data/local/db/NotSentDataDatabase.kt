package com.example.neurosmg.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.neurosmg.data.local.model.NotSavedTestEntity

@Database(entities = [NotSavedTestEntity::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class NotSentDataDatabase: RoomDatabase() {
    abstract fun notSavedDataDao(): NotSentDataDao

    companion object {

        private val DB_NAME = "NotSentDataDatabase"
        private var INSTANCE: NotSentDataDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): NotSentDataDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val database = Room.databaseBuilder(
                    context = context,
                    klass = NotSentDataDatabase::class.java,
                    name = DB_NAME
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}