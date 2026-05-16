package at.l28.hci.mapapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class OfflineMap(val id: String, val name: String, val size: String)

class StorageViewModel : ViewModel() {
    val offlineMaps = mutableStateListOf(
        OfflineMap("1", "Wien Zentrum", "450 MB"),
        OfflineMap("2", "Niederösterreich Süd", "800 MB")
    )

    var totalUsedSpace by mutableStateOf("1.25 GB")
    var totalAvailableSpace by mutableStateOf("64 GB")

    fun deleteMap(map: OfflineMap) {
        offlineMaps.remove(map)
        updateUsedSpace()
    }

    private fun updateUsedSpace() {
        val totalMB = offlineMaps.sumOf { it.size.split(" ")[0].toInt() }
        totalUsedSpace = if (totalMB >= 1000) {
            String.format("%.2f GB", totalMB / 1024.0)
        } else {
            "$totalMB MB"
        }
    }
}
