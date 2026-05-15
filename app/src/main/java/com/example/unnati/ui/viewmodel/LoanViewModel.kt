package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.entity.Loan
import com.example.unnati.data.entity.Member
import com.example.unnati.data.entity.Repayment
import com.example.unnati.data.repository.UnnatiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── LoanViewModel (Loan List screen) ─────────────────────────────────────────

class LoanViewModel(private val repository: UnnatiRepository) : ViewModel() {

    val activeLoans: StateFlow<List<Loan>> = repository.activeLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val members: StateFlow<List<Member>> = repository.allActiveMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun memberFor(loan: Loan): Member? = members.value.firstOrNull { it.id == loan.memberId }
}

// ── NewLoanViewModel ──────────────────────────────────────────────────────────

class NewLoanViewModel(private val repository: UnnatiRepository) : ViewModel() {

    val members: StateFlow<List<Member>> = repository.allActiveMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMemberId = MutableStateFlow<Int?>(null)
    val principal = MutableStateFlow("")
    val rate = MutableStateFlow("2")
    val duration = MutableStateFlow("6")

    val existingLoan: StateFlow<Loan?> = selectedMemberId
        .filterNotNull()
        .flatMapLatest { id -> repository.getLoansByMember(id).map { loans -> loans.firstOrNull { it.status == "ACTIVE" } } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isBlocked: StateFlow<Boolean> = existingLoan.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val groupCapital: StateFlow<Double> = repository.totalGroupCapital
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val memberSavings: StateFlow<Double> = selectedMemberId
        .filterNotNull()
        .flatMapLatest { id -> repository.getTotalSavingsForMember(id).map { it ?: 0.0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val eligibility: StateFlow<Double> = combine(memberSavings, groupCapital) { ms, gc ->
        if (gc == 0.0) 0.0 else (ms / gc) * gc * 3.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val estimatedInterest: StateFlow<Double> = combine(principal, rate, duration) { p, r, d ->
        val pv = p.toDoubleOrNull() ?: 0.0
        val rv = r.toDoubleOrNull() ?: 0.0
        val dv = d.toDoubleOrNull() ?: 0.0
        (pv * rv * dv) / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _issued = MutableStateFlow(false)
    val issued: StateFlow<Boolean> = _issued

    fun issueLoan() {
        if (isBlocked.value) return
        val memberId = selectedMemberId.value ?: return
        val p = principal.value.toDoubleOrNull() ?: return
        val r = rate.value.toDoubleOrNull() ?: return
        val d = duration.value.toIntOrNull() ?: return
        viewModelScope.launch {
            // Double-check BR-01 at ViewModel level
            val active = repository.getLoansByMember(memberId).first()
                .any { it.status == "ACTIVE" }
            if (active) return@launch
            repository.insertLoan(
                Loan(memberId = memberId, principal = p, interestRate = r,
                    startDate = System.currentTimeMillis(), durationMonths = d)
            )
            _issued.value = true
        }
    }
}

// ── LoanDetailViewModel ───────────────────────────────────────────────────────

class LoanDetailViewModel(
    private val repository: UnnatiRepository,
    private val loanId: Int,
) : ViewModel() {

    private val _loan = MutableStateFlow<Loan?>(null)
    val loan: StateFlow<Loan?> = _loan

    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    val repayments = repository.getRepaymentsForLoan(loanId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRepaid: StateFlow<Double> = repository.getTotalRepaidForLoan(loanId)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val accruedInterest: StateFlow<Double> = _loan.filterNotNull().map { l ->
        val months = ((System.currentTimeMillis() - l.startDate) / (1000.0 * 60 * 60 * 24 * 30.44))
        (l.principal * l.interestRate * months) / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val outstanding: StateFlow<Double> = combine(_loan, totalRepaid, accruedInterest) { l, repaid, interest ->
        ((l?.principal ?: 0.0) + interest - repaid).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _repaymentSaved = MutableStateFlow(false)
    val repaymentSaved: StateFlow<Boolean> = _repaymentSaved

    init {
        viewModelScope.launch {
            _loan.value = repository.getLoanById(loanId)
            _loan.value?.let { _member.value = repository.getMemberById(it.memberId) }
        }
    }

    fun addRepayment(amount: Double, note: String?) = viewModelScope.launch {
        repository.insertRepayment(Repayment(loanId = loanId, amount = amount,
            paidDate = System.currentTimeMillis(), note = note))
        // Auto-close if outstanding reaches zero
        val currentOutstanding = outstanding.value - amount
        if (currentOutstanding <= 0.0) {
            _loan.value?.let { repository.updateLoan(it.copy(status = "CLOSED")) }
        }
        _repaymentSaved.value = true
    }

    fun closeLoan() = viewModelScope.launch {
        _loan.value?.let { repository.updateLoan(it.copy(status = "CLOSED")) }
    }
}
