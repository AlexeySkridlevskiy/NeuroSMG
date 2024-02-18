package com.example.neurosmg.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "not_saved_test")
data class NotSavedTestEntity(
    @PrimaryKey val fileName: String,
    val idPatient: Int,
    val data: List<List<String>>,
)
