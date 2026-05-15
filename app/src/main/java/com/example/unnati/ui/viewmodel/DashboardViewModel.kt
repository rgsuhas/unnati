package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.repository.UnnatiRepository
import kotlinx.coroutines.flow.*

class DashboardViewModel(private val repository: UnnatiRepository) : ViewModel() {

    val groupCapital: StateFlow<Double> = repository.totalGroupCapital
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val activeLoansCount: StateFlow<Int> = repository.activeLoansCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeLoans = repository.activeLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalLoanOutstanding: StateFlow<Double> = activeLoans
        .map { loans -> loans.sumOf { it.principal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val memberCount: StateFlow<Int> = repository.allActiveMembers
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
