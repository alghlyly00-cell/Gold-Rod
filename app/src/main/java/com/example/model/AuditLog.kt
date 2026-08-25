package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "audit_logs",
    indices = [Index("associationId"), Index("memberId")]
)
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val associationId: Int?,
    val memberId: Int?,
    val actionType: String,
    val memberName: String?,
    val oldAmount: Double?,
    val newAmount: Double?,
    val timestamp: Long,
    val reason: String?
)
