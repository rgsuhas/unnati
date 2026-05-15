package com.example.unnati

import android.app.Application
import com.example.unnati.data.AppDatabase
import com.example.unnati.data.repository.UnnatiRepository

class UnnatiApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        UnnatiRepository(
            database.memberDao(),
            database.savingsDao(),
            database.loanDao(),
            database.repaymentDao()
        )
    }
}
