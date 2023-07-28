package com.example.neurosmg.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.neurosmg.db.entity.RatResultDto

@Dao
interface RatTestResultDao {
    @Query("SELECT * FROM rat_result ORDER BY id")
    fun getRatResults(): List<RatResultDto>

    @Query("SELECT * FROM rat_result WHERE id = :id LIMIT 1")
    fun getResultAboutClientId(id: Int): RatResultDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(ratResults: RatResultDto)
}