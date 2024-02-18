package com.example.neurosmg.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.neurosmg.data.local.model.NotSavedTestEntity

@Dao
interface NotSentDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTest(notSavedTestEntity: NotSavedTestEntity)

    @Query("SELECT * FROM not_saved_test")
    fun getAllTests(): List<NotSavedTestEntity>

    @Query("DELETE FROM not_saved_test WHERE fileName=:fileName")
    suspend fun removeFromNotSentData(fileName: String)
}