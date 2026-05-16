package at.l28.hci.mapapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("Max Mustermann")
    var birthDate by mutableStateOf("12.05.1995")
    var email by mutableStateOf("max.mustermann@example.com")
}
