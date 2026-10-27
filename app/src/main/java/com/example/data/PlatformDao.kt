package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.PlatformItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformDao {
    @Query("SELECT * FROM platforms ORDER BY series ASC, platform ASC")
    fun getAllPlatforms(): Flow<List<PlatformItem>>

    @Query("SELECT * FROM platforms WHERE series = :series ORDER BY platform ASC")
    fun getPlatformsBySeries(series: String): Flow<List<PlatformItem>>

    @Query("SELECT * FROM platforms WHERE platform = :platform LIMIT 1")
    suspend fun getPlatformByName(platform: String): PlatformItem?

    @Query("SELECT * FROM platforms WHERE isMismatchedOrDuplicate = 1")
    fun getMismatchedPlatforms(): Flow<List<PlatformItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatform(platform: PlatformItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatforms(platforms: List<PlatformItem>)

    @Update
    suspend fun updatePlatform(platform: PlatformItem)

    @Query("UPDATE platforms SET signupUrl = :signupUrl WHERE platform = :platform")
    suspend fun updateSignupUrl(platform: String, signupUrl: String)

    @Query("DELETE FROM platforms")
    suspend fun deleteAllPlatforms()
}
