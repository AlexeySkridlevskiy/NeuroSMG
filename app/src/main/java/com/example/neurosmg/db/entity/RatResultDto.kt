package com.example.neurosmg.db.entity

import androidx.room.Entity

@Entity(tableName = "rat_result")
data class RatResultDto(
    val id: Int,
    val time: Long
)