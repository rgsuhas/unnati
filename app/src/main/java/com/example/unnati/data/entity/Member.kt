package com.example.unnati.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val photoUri: String? = null,
    val joinDate: Long, // epoch ms
    val role: String = "MEMBER", // ADMIN, MEMBER
    val isActive: Boolean = true
)
