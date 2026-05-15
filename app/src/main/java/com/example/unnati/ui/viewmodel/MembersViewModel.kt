package com.example.unnati.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.entity.Member
import com.example.unnati.data.repository.UnnatiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MembersViewModel(private val repository: UnnatiRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val members: StateFlow<List<Member>> = repository.allActiveMembers
        .combine(searchQuery) { list, query ->
            if (query.isBlank()) list
            else list.filter { it.name.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun softDelete(id: Int) = viewModelScope.launch { repository.softDeleteMember(id) }
}

class AddEditMemberViewModel(private val repository: UnnatiRepository) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _member = MutableStateFlow<Member?>(null)
    val member: StateFlow<Member?> = _member

    fun load(id: Int) = viewModelScope.launch {
        if (id > 0) _member.value = repository.getMemberById(id)
    }

    fun save(
        id: Int,
        name: String,
        phone: String,
        photoUri: String?,
        joinDate: Long,
        role: String,
    ) = viewModelScope.launch {
        if (id > 0) {
            repository.updateMember(
                Member(id, name.trim(), phone.trim(), photoUri, joinDate, role, true)
            )
        } else {
            repository.insertMember(
                Member(name = name.trim(), phone = phone.trim(), photoUri = photoUri,
                    joinDate = joinDate, role = role)
            )
        }
        _saved.value = true
    }
}
