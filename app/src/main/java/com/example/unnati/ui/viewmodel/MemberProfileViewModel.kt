package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.entity.Loan
import com.example.unnati.data.entity.Member
import com.example.unnati.data.entity.SavingsEntry
import com.example.unnati.data.repository.UnnatiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MemberProfileViewModel(
    private val repository: UnnatiRepository,
    private val memberId: Int,
) : ViewModel() {

    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    val savingsEntries: StateFlow<List<SavingsEntry>> = repository.getSavingsForMember(memberId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSavings: StateFlow<Double> = repository.getTotalSavingsForMember(memberId)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val activeLoan: StateFlow<Loan?> = repository.getLoansByMember(memberId)
        .map { loans -> loans.firstOrNull { it.status == "ACTIVE" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val creditScore: StateFlow<Int> = savingsEntries.map { entries ->
        if (entries.isEmpty()) return@map 0
        val paid = entries.count { it.status == "PAID" }
        ((paid.toFloat() / entries.size) * 100).toInt().coerceIn(0, 100)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentWeekEntry: StateFlow<SavingsEntry?> = savingsEntries.map { entries ->
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysSince = if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysSince)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        entries.firstOrNull { it.weekStartDate == weekStart }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            _member.value = repository.getMemberById(memberId)
        }
    }
}

class ContributionHistoryViewModel(
    private val repository: UnnatiRepository,
    private val memberId: Int,
) : ViewModel() {

    val entries: StateFlow<List<SavingsEntry>> = repository.getSavingsForMember(memberId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPaid: StateFlow<Double> = repository.getTotalSavingsForMember(memberId)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPending: StateFlow<Double> = entries
        .map { list -> list.filter { it.status == "PENDING" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
