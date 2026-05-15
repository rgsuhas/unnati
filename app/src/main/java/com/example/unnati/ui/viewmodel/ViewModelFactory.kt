package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unnati.data.repository.UnnatiRepository

class RepositoryViewModelFactory(
    private val repository: UnnatiRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(MembersViewModel::class.java) ->
                MembersViewModel(repository) as T
            modelClass.isAssignableFrom(AddEditMemberViewModel::class.java) ->
                AddEditMemberViewModel(repository) as T
            modelClass.isAssignableFrom(SavingsEntryViewModel::class.java) ->
                SavingsEntryViewModel(repository) as T
            modelClass.isAssignableFrom(LoanViewModel::class.java) ->
                LoanViewModel(repository) as T
            modelClass.isAssignableFrom(NewLoanViewModel::class.java) ->
                NewLoanViewModel(repository) as T
            modelClass.isAssignableFrom(ExportViewModel::class.java) ->
                ExportViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

/** Factory for ViewModels that also need an ID argument. */
class IdViewModelFactory(
    private val repository: UnnatiRepository,
    private val id: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MemberProfileViewModel::class.java) ->
                MemberProfileViewModel(repository, id) as T
            modelClass.isAssignableFrom(LoanDetailViewModel::class.java) ->
                LoanDetailViewModel(repository, id) as T
            modelClass.isAssignableFrom(ContributionHistoryViewModel::class.java) ->
                ContributionHistoryViewModel(repository, id) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
