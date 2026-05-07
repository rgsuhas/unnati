package com.example.unnati.data.dao

import androidx.room.*
import com.example.unnati.data.entity.Repayment
import kotlinx.coroutines.flow.Flow

@Dao
interface RepaymentDao {
    @Query("SELECT * FROM repayments WHERE loanId = :loanId ORDER BY paidDate DESC")
    fun getRepaymentsForLoan(loanId: Int): Flow<List<Repayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: Repayment)

    @Query("SELECT SUM(amount) FROM repayments WHERE loanId = :loanId")
    fun getTotalRepaidForLoan(loanId: Int): Flow<Double?>
}
