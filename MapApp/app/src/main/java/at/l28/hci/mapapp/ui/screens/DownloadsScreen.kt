package at.l28.hci.mapapp.ui.screens

<<<<<<< HEAD
import android.content.Intent
import android.net.Uri
=======
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
>>>>>>> 6849601 (Further refined download menu)
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stars
=======
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.*
>>>>>>> 6849601 (Further refined download menu)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.platform.LocalContext
=======
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
>>>>>>> 6849601 (Further refined download menu)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
    val icon: ImageVector = Icons.Default.Stars
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen() {
<<<<<<< HEAD
    val context = LocalContext.current

    val datasets = remember {
        mutableStateListOf(
            Dataset("Baumkataster", "Supporting line text, lorem ipsum...", "362 MB"),
            Dataset("Straßenzüge", "Supporting line text, lorem ipsum...", "341 MB"),
            Dataset("Luftmessstellen", "Supporting line text, lorem ipsum...", "42 MB"),
            Dataset("Wanderwege", "Supporting line text...", "762 MB", isDownloaded = true),
            Dataset("Solarpotenzialkataster", "Supporting line text, lorem ipsum...", "185 MB"),
            Dataset("Saubere Stadt", "Supporting line text, lorem ipsum...", "78 MB"),
            Dataset("Wiener Märkte", "Supporting line text, lorem ipsum...", "138 MB"),
            Dataset("Historische Stadtpläne", "Supporting line text, lorem ipsum...", "1,6 GB"),
            Dataset("Gebäudeinformationen", "Supporting line text, lorem ipsum...", "218 MB"),
            Dataset("Dachkataster (Innere Stadt)", "Supporting line text, lorem ipsum...", "114 MB")
        )
    }
=======
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
>>>>>>> 6849601 (Further refined download menu)

    val allDatasets = remember {
        listOf(
            Dataset("map_vienna", "Wien - Basiskarte", "Basis", "Vollständige Offline-Karte für das gesamte Stadtgebiet.", "412 MB", DownloadState.DOWNLOADED),
            Dataset("map_vienna_center", "Wien - Innere Stadt (HD)", "Basis", "Hochauflösende Details für den 1. Bezirk.", "85 MB", DownloadState.NOT_DOWNLOADED),
            Dataset("wl_network", "Wiener Linien - Netz", "Verkehr", "S-Bahn, U-Bahn, Straßenbahn und Busstationen.", "12 MB", DownloadState.DOWNLOADED, Icons.Default.DirectionsBus),
            Dataset("bike_lanes", "Radwege Wien", "Verkehr", "Umfassendes Verzeichnis aller Radwege und Abstellplätze.", "4.5 MB", DownloadState.NOT_DOWNLOADED, Icons.AutoMirrored.Filled.DirectionsBike),
            Dataset("trees", "Wiener Baumkataster", "Umwelt", "Standorte und Arten von über 200.000 Stadtbäumen.", "38 MB", DownloadState.NOT_DOWNLOADED, Icons.Default.Park),
            Dataset("fountains", "Trinkbrunnen & Kühlung", "Umwelt", "Wasserspender und Nebelduschen für heiße Tage.", "1.2 MB", DownloadState.DOWNLOADED, Icons.Default.WaterDrop),
            Dataset("museums", "Kultur & Tourismus", "Kultur", "Museen, Denkmäler und historische Sehenswürdigkeiten.", "15 MB", DownloadState.NOT_DOWNLOADED, Icons.Default.Museum),
            Dataset("history_1912", "Stadtplan 1912", "Kultur", "Historische Kartenebene für Zeitreisen durch Wien.", "1.2 GB", DownloadState.NOT_DOWNLOADED, Icons.Default.HistoryEdu),
            Dataset("wifi", "Free Wave Hotspots", "Infrastruktur", "Öffentliche WLAN-Standorte in ganz Wien.", "0.8 MB", DownloadState.DOWNLOADED, Icons.Default.Wifi)
        )
    }

<<<<<<< HEAD
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(datasets, key = { it.name }) { dataset ->
            DatasetItem(
                dataset = dataset,
                isExpanded = expandedId == dataset.name,
                onExpandClick = {
                    expandedId = if (expandedId == dataset.name) null else dataset.name
                },
                onDownloadClick = {
                    val index = datasets.indexOfFirst { it.name == dataset.name }
                    if (index >= 0) datasets[index] = datasets[index].copy(isDownloaded = true)
                },
                onDeleteClick = {
                    val index = datasets.indexOfFirst { it.name == dataset.name }
                    if (index >= 0) datasets[index] = datasets[index].copy(isDownloaded = false)
                },
                onOpenClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://data.wien.gv.at/"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
=======
    val downloadStates = remember { mutableStateMapOf<String, DownloadState>().apply { 
        allDatasets.forEach { put(it.id, it.initialState) }
    } }
    val downloadProgress = remember { mutableStateMapOf<String, Float>() }

    val filteredDatasets = allDatasets.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }.filter {
        when (selectedTabIndex) {
            1 -> downloadStates[it.id] == DownloadState.DOWNLOADED
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFFBF8FF)) // Light lavender background like the image
    ) {
        Text(
            text = "Downloads",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Karten oder Layer suchen...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
>>>>>>> 6849601 (Further refined download menu)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                Text("Entdecken", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                Text("Installiert", modifier = Modifier.padding(12.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredDatasets) { dataset ->
                val state = downloadStates[dataset.id] ?: DownloadState.NOT_DOWNLOADED
                val progress = downloadProgress[dataset.id] ?: 0f
                
                SwipeableDatasetItem(
                    dataset = dataset,
                    state = state,
                    progress = progress,
                    onDownload = {
                        downloadStates[dataset.id] = DownloadState.DOWNLOADING
                    },
                    onDelete = {
                        downloadStates[dataset.id] = DownloadState.NOT_DOWNLOADED
                        downloadProgress.remove(dataset.id)
                    }
                )
                
                if (state == DownloadState.DOWNLOADING) {
                    LaunchedEffect(dataset.id) {
                        var p = 0f
                        while (p < 1f) {
                            delay(50)
                            p += 0.05f
                            downloadProgress[dataset.id] = p
                        }
                        downloadStates[dataset.id] = DownloadState.DOWNLOADED
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableDatasetItem(
    dataset: Dataset,
<<<<<<< HEAD
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onOpenClick: () -> Unit
) {
    Column {
        ListItem(
            modifier = Modifier.clickable { onExpandClick() },
            headlineContent = { Text(dataset.name, fontWeight = FontWeight.SemiBold) },
            supportingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(dataset.description, modifier = Modifier.weight(1f))
                    Text(dataset.size)
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = if (dataset.isDownloaded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                if (isExpanded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (dataset.isDownloaded) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                    .padding(8.dp)
                                    .clickable { onOpenClick() }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Öffnen")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.StarBorder, contentDescription = "Lesezeichen")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Teilen")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (dataset.isDownloaded) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .padding(8.dp)
                                .clickable {
                                    if (dataset.isDownloaded) onDeleteClick() else onDownloadClick()
                                }
                        ) {
                            Icon(
                                if (dataset.isDownloaded) Icons.Default.Delete else Icons.Default.FileDownload,
                                contentDescription = if (dataset.isDownloaded) "Löschen" else "Herunterladen",
                                tint = if (dataset.isDownloaded) MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { if (dataset.isDownloaded) onOpenClick() else onDownloadClick() }) {
                        Icon(
                            if (dataset.isDownloaded) Icons.AutoMirrored.Filled.OpenInNew else Icons.Default.FileDownload,
                            contentDescription = if (dataset.isDownloaded) "Öffnen" else "Herunterladen"
                        )
                    }
=======
    state: DownloadState,
    progress: Float,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val menuWidth = 200.dp
    val menuWidthPx = with(LocalDensity.current) { menuWidth.toPx() }
    
    val animatedOffset by animateIntOffsetAsState(
        targetValue = IntOffset(offsetX.roundToInt(), 0),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFBF8FF)) // Match screen background
    ) {
        // Menu content (Revealed on swipe)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = {},
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.StarBorder, contentDescription = "Favorite", modifier = Modifier.size(24.dp))
>>>>>>> 6849601 (Further refined download menu)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                onClick = {},
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                onClick = { 
                    onDelete()
                    offsetX = 0f
                },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }

        // Main content
        Surface(
            modifier = Modifier
                .offset { animatedOffset }
                .draggable(
                    state = rememberDraggableState { delta ->
                        val newOffset = (offsetX + delta).coerceIn(-menuWidthPx, 0f)
                        offsetX = newOffset
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        offsetX = if (offsetX < -menuWidthPx / 2) -menuWidthPx else 0f
                    }
                )
                .fillMaxWidth(),
            color = Color(0xFFFBF8FF)
        ) {
            Column {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { 
                        Text(
                            dataset.name, 
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    supportingContent = {
                        Text(
                            dataset.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                dataset.size,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            when (state) {
                                DownloadState.NOT_DOWNLOADED -> {
                                    IconButton(onClick = onDownload) {
                                        Icon(Icons.Default.FileDownload, contentDescription = "Download")
                                    }
                                }
                                DownloadState.DOWNLOADING -> {
                                    CircularProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                                DownloadState.DOWNLOADED -> {
                                    // User requested: "Make the three dot menu into a trash bin for downloaded data sets"
                                    IconButton(onClick = onDelete) {
                                        Icon(
                                            imageVector = Icons.Default.Delete, 
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

<<<<<<< HEAD
data class Dataset(
    val name: String,
    val description: String,
    val size: String,
    val isDownloaded: Boolean = false
)

=======
>>>>>>> 6849601 (Further refined download menu)
@Preview(showBackground = true)
@Composable
fun DownloadsScreenPreview() {
    DownloadsScreen()
}
