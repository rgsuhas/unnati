package com.example.unnati.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// Single DataStore instance for the whole app — two instances on the same file crash at runtime.
val Context.appDataStore by preferencesDataStore("unnati_settings")

object PrefsKeys {
    val ADMIN_PIN      = stringPreferencesKey("admin_pin")
    val INTEREST_RATE  = doublePreferencesKey("interest_rate")
    val WEEKLY_AMOUNT  = doublePreferencesKey("weekly_amount")
    val PALETTE_NAME   = stringPreferencesKey("palette_name")
    val IS_DARK_MODE   = booleanPreferencesKey("is_dark_mode")
    val GROUP_NAME     = stringPreferencesKey("group_name")
}
