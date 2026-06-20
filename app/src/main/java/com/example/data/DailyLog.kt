package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey val date: String, // format: "yyyy-MM-dd"
    val isPeriod: Boolean = false,
    val flow: String = "Medium", // "Light", "Medium", "Heavy"
    val symptoms: String = "", // comma-separated symptoms (e.g., "Cramps,Headache")
    val mood: String = "Neutral", // "Happy", "Energetic", "Calm", "Neutral", "Sad", "Anxious", "Sensitive"
    val notes: String = ""
)
