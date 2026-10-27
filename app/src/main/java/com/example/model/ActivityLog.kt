package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: String = "",
    val accountName: String = "",
    val actionType: String, // "CREATED", "UPDATED", "STATUS_CHANGED", "DELETED", "URL_EDITED", "CSV_IMPORTED"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
