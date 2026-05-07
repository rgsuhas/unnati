package com.example.unnati.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.unnati.data.dao.LoanDao
import com.example.unnati.data.dao.MemberDao
import com.example.unnati.data.dao.RepaymentDao
import com.example.unnati.data.dao.SavingsDao
import com.example.unnati.data.entity.Loan
import com.example.unnati.data.entity.Member
import com.example.unnati.data.entity.Repayment
import com.example.unnati.data.entity.SavingsEntry

@Database(
    entities = [Member::class, SavingsEntry::class, Loan::class, Repayment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun savingsDao(): SavingsDao
    abstract fun loanDao(): LoanDao
    abstract fun repaymentDao(): RepaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unnati_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
