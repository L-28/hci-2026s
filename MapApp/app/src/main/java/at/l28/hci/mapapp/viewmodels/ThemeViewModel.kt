package at.l28.hci.mapapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeViewModel : ViewModel() {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    val isDarkMode: Boolean
        get() = themeMode == ThemeMode.DARK

    fun setTheme(mode: ThemeMode) {
        themeMode = mode
    }

    // Keep for backward compatibility if needed, but we'll use setTheme
    fun toggleDarkMode() {
        themeMode = if (themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
    }
}