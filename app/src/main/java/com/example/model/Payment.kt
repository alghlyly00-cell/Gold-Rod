package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Association::class,
            parentColumns = ["id"],
            childColumns = ["associationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memberId"), Index("associationId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val associationId: Int,
    val amount: Double,
    val date: Long,
    val transactionNumber: String?,
    val status: String,
    val notes: String?
)
