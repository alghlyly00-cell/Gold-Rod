package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "associations")
data class Association(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String?,
    val expectedMembersCount: Int,
    val subscriptionAmount: Double,
    val durationMonths: Int,
    val startDate: Long,
    val endDate: Long,
    val paymentMethod: String?,
    val notes: String?
)
