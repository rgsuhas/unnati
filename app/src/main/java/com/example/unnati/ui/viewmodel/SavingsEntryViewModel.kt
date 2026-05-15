package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.entity.Member
import com.example.unnati.data.entity.SavingsEntry
import com.example.unnati.data.repository.UnnatiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class MemberSavingsRow(
    val member: Member,
    val status: String,   // "PAID" | "PENDING"
    val amount: Double,
)

class SavingsEntryViewModel(private val repository: UnnatiRepository) : ViewModel() {

    private fun mondayOf(epochMs: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMs }
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysSinceMonday = if (dow == Calendar.SUNDAY) 6 else dow - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val selectedWeek = MutableStateFlow(mondayOf(System.currentTimeMillis()))
    val weeklyAmount = MutableStateFlow(100.0)

    private val _rows = MutableStateFlow<List<MemberSavingsRow>>(emptyList())
    val rows: StateFlow<List<MemberSavingsRow>> = _rows

    val paidCount = _rows.map { r -> r.count { it.status == "PAID" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCollected = _rows.map { r -> r.filter { it.status == "PAID" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init {
        viewModelScope.launch {
            combine(selectedWeek, repository.allActiveMembers) { week, members -> week to members }
                .collect { (week, members) ->
                    val existing = repository.getSavingsForWeek(week).first()
                    val existingMap = existing.associateBy { it.memberId }
                    _rows.value = members.map { m ->
                        val entry = existingMap[m.id]
                        MemberSavingsRow(m, entry?.status ?: "PENDING", entry?.amount ?: weeklyAmount.value)
                    }
                }
        }
    }

    fun toggleStatus(memberId: Int) {
        _rows.value = _rows.value.map { row ->
            if (row.member.id == memberId)
                row.copy(status = if (row.status == "PAID") "PENDING" else "PAID")
            else row
        }
    }

    fun updateAmount(memberId: Int, amount: Double) {
        _rows.value = _rows.value.map { row ->
            if (row.member.id == memberId) row.copy(amount = amount) else row
        }
    }

    fun saveAll() = viewModelScope.launch {
        val week = selectedWeek.value
        val entries = _rows.value.map { row ->
            SavingsEntry(
                memberId = row.member.id,
                weekStartDate = week,
                amount = row.amount,
                status = row.status,
            )
        }
        repository.insertAllSavings(entries)
        _saved.value = true
    }
}
