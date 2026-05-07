package com.example.unnati.data.dao

import androidx.room.*
import com.example.unnati.data.entity.SavingsEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {
    @Query("SELECT * FROM savings_entries WHERE memberId = :memberId ORDER BY weekStartDate DESC")
    fun getSavingsForMember(memberId: Int): Flow<List<SavingsEntry>>

    @Query("SELECT * FROM savings_entries WHERE weekStartDate = :weekStart")
    fun getSavingsForWeek(weekStart: Long): Flow<List<SavingsEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsEntry(entry: SavingsEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<SavingsEntry>)

    @Query("SELECT SUM(amount) FROM savings_entries WHERE status = 'PAID'")
    fun getTotalGroupCapital(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM savings_entries WHERE memberId = :memberId AND status = 'PAID'")
    fun getTotalSavingsForMember(memberId: Int): Flow<Double?>
}
