package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM daily_logs ORDER BY date ASC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getLogByDate(date: String): DailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog)

    @Delete
    suspend fun deleteLog(log: DailyLog)

    @Query("SELECT * FROM cycle_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<CycleSettings?>

    @Query("SELECT * FROM cycle_settings WHERE id = 1")
    suspend fun getSettings(): CycleSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: CycleSettings)
}
