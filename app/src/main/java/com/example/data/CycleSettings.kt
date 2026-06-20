package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_settings")
data class CycleSettings(
    @PrimaryKey val id: Int = 1,
    val typicalCycleLength: Int = 28,
    val typicalPeriodLength: Int = 5,
    val userName: String = "Serene",
    val lastGeneratedInsight: String? = null,
    val lastGeneratedInsightTime: Long = 0L
)
