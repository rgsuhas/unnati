package com.example.unnati.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memberId")]
)
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val principal: Double,
    val interestRate: Double, // annual % or monthly %? PRD says 2%/month
    val startDate: Long, // epoch ms
    val durationMonths: Int,
    val status: String = "ACTIVE" // ACTIVE, CLOSED
)
