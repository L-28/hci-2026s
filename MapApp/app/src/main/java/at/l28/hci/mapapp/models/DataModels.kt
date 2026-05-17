package at.l28.hci.mapapp.models

import androidx.compose.ui.graphics.vector.ImageVector
import org.maplibre.spatialk.geojson.Position

enum class DownloadState {
    NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED
}

data class Dataset(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val size: String,
    val initialState: DownloadState = DownloadState.NOT_DOWNLOADED,
    val icon: ImageVector? = null
)

data class PinInfo(
    val id: String,
    val name: String,
    val description: String,
    val position: Position,
    val datasetId: String
)
