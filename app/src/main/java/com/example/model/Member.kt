package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = Association::class,
            parentColumns = ["id"],
            childColumns = ["associationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("associationId")]
)
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val associationId: Int,
    val name: String,
    val phoneNumber: String?,
    val memberNumber: Int,
    val subscriptionAmount: Double,
    val expectedTotalAmount: Double,
    val status: String 
)
