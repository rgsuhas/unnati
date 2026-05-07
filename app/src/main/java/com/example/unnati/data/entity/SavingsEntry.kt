package com.example.unnati.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings_entries",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["memberId", "weekStartDate"], unique = true)]
)
data class SavingsEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val weekStartDate: Long, // epoch ms (Monday of the week)
    val amount: Double,
    val status: String, // PAID, PENDING
    val recordedAt: Long = System.currentTimeMillis()
)
