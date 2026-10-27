package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platforms")
data class PlatformItem(
    @PrimaryKey
    val platform: String,
    val series: String,
    val signupUrl: String = "",
    val rewardPoint: Int = 300,
    val isMismatchedOrDuplicate: Boolean = false,
    val reviewNote: String = ""
)
