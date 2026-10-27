package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val profileImage: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val series: String,
    val platform: String,
    val maintainSeries: String = "",
    val successfulGrow: Boolean = false,
    val earnPoint: Int = if (successfulGrow) 300 else 0,
    val registerDate: Long = System.currentTimeMillis(),
    val status: String = if (successfulGrow) "Grow" else "Pending",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
