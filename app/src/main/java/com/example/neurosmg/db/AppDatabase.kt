package com.example.neurosmg.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class AppDatabase: RoomDatabase() {

    companion object {
        private var db: AppDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }

    // todo:  Ты создашь тут под каждый тест свой DAO интерфейс. Будет создаваться своя таблица
    //  через эти интерфейсы будешь сохранять и доставать результаты из таблицы
    abstract fun productPriceInfoDao(): RatTestResultDao
}