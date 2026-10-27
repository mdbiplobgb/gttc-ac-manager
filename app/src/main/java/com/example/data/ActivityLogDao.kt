package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.ActivityLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 50): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog)

    @Query("DELETE FROM activity_logs")
    suspend fun clearLogs()
}
