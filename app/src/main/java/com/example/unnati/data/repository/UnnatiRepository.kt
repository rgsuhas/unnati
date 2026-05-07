package com.example.unnati.data.repository

import com.example.unnati.data.dao.LoanDao
import com.example.unnati.data.dao.MemberDao
import com.example.unnati.data.dao.RepaymentDao
import com.example.unnati.data.dao.SavingsDao
import com.example.unnati.data.entity.Loan
import com.example.unnati.data.entity.Member
import com.example.unnati.data.entity.Repayment
import com.example.unnati.data.entity.SavingsEntry
import kotlinx.coroutines.flow.Flow

class UnnatiRepository(
    private val memberDao: MemberDao,
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao,
    private val repaymentDao: RepaymentDao
) {
    // Member operations
    val allActiveMembers: Flow<List<Member>> = memberDao.getAllActiveMembers()
    suspend fun getMemberById(id: Int) = memberDao.getMemberById(id)
    suspend fun insertMember(member: Member) = memberDao.insertMember(member)
    suspend fun updateMember(member: Member) = memberDao.updateMember(member)
    suspend fun softDeleteMember(id: Int) = memberDao.softDeleteMember(id)

    // Savings operations
    fun getSavingsForMember(memberId: Int) = savingsDao.getSavingsForMember(memberId)
    fun getSavingsForWeek(weekStart: Long) = savingsDao.getSavingsForWeek(weekStart)
    suspend fun insertSavingsEntry(entry: SavingsEntry) = savingsDao.insertSavingsEntry(entry)
    suspend fun insertAllSavings(entries: List<SavingsEntry>) = savingsDao.insertAll(entries)
    val totalGroupCapital: Flow<Double?> = savingsDao.getTotalGroupCapital()
    fun getTotalSavingsForMember(memberId: Int) = savingsDao.getTotalSavingsForMember(memberId)

    // Loan operations
    val activeLoans: Flow<List<Loan>> = loanDao.getActiveLoans()
    fun getLoansByMember(memberId: Int) = loanDao.getLoansByMember(memberId)
    suspend fun getLoanById(id: Int) = loanDao.getLoanById(id)
    suspend fun insertLoan(loan: Loan) = loanDao.insertLoan(loan)
    suspend fun updateLoan(loan: Loan) = loanDao.updateLoan(loan)
    val activeLoansCount: Flow<Int> = loanDao.getActiveLoansCount()

    // Repayment operations
    fun getRepaymentsForLoan(loanId: Int) = repaymentDao.getRepaymentsForLoan(loanId)
    suspend fun insertRepayment(repayment: Repayment) = repaymentDao.insertRepayment(repayment)
    fun getTotalRepaidForLoan(loanId: Int) = repaymentDao.getTotalRepaidForLoan(loanId)
}
