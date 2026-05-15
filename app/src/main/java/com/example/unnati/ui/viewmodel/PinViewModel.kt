package com.example.unnati.ui.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unnati.data.PrefsKeys
import com.example.unnati.data.appDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class PinState {
    object Idle       : PinState()
    object Checking   : PinState()
    object Success    : PinState()
    object Error      : PinState()
    object FirstLaunch: PinState()
    object PinSet     : PinState()
}

class PinViewModel(private val context: Context) : ViewModel() {

    private val DEFAULT_PIN = "1234"

    private val _state = MutableStateFlow<PinState>(PinState.Idle)
    val state: StateFlow<PinState> = _state

    private val _digits = MutableStateFlow("")
    val digits: StateFlow<String> = _digits

    init {
        viewModelScope.launch {
            val stored = context.appDataStore.data.first()[PrefsKeys.ADMIN_PIN]
            if (stored == null) _state.value = PinState.FirstLaunch
        }
    }

    fun addDigit(d: Char) {
        if (_digits.value.length >= 4) return
        val next = _digits.value + d
        _digits.value = next
        if (next.length == 4) verifyPin(next)
    }

    fun backspace() {
        _digits.value = _digits.value.dropLast(1)
        _state.value = PinState.Idle
    }

    fun setNewPin(pin: String) {
        viewModelScope.launch {
            context.appDataStore.edit { it[PrefsKeys.ADMIN_PIN] = pin }
            _state.value = PinState.PinSet
        }
    }

    private fun verifyPin(entered: String) {
        viewModelScope.launch {
            _state.value = PinState.Checking
            val stored = context.appDataStore.data.first()[PrefsKeys.ADMIN_PIN] ?: DEFAULT_PIN
            if (entered == stored) {
                _state.value = PinState.Success
            } else {
                _state.value = PinState.Error
                _digits.value = ""
            }
        }
    }

    fun resetState() {
        _state.value = PinState.Idle
        _digits.value = ""
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            PinViewModel(context.applicationContext) as T
    }
}
