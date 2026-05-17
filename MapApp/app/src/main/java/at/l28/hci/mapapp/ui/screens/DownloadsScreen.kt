package at.l28.hci.mapapp.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import at.l28.hci.mapapp.viewmodels.BookmarksViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import at.l28.hci.mapapp.data.DataProvider
import at.l28.hci.mapapp.models.Dataset
import at.l28.hci.mapapp.models.DownloadState
import kotlin.math.roundToInt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    bookmarksViewModel: BookmarksViewModel = viewModel(),
    onNavigateToPin: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val allDatasets = remember { DataProvider.allDatasets }

    val downloadStates = remember {
        mutableStateMapOf<String, DownloadState>().apply {
            allDatasets.forEach { put(it.id, it.initialState) }
        }
    }
    val downloadProgress = remember { mutableStateMapOf<String, Float>() }

    val filteredDatasets = allDatasets.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }.filter {
        when (selectedTabIndex) {
            1 -> downloadStates[it.id] == DownloadState.DOWNLOADED
            else -> true
        }
    }

    var expandedDatasetId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFFBF8FF))
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

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredDatasets) { dataset ->
                val state = downloadStates[dataset.id] ?: DownloadState.NOT_DOWNLOADED
                val progress = downloadProgress[dataset.id] ?: 0f

                SwipeableDatasetItem(
                    dataset = dataset,
                    state = state,
                    progress = progress,
                    isBookmarked = bookmarksViewModel.isBookmarked(dataset.id),
                    isExpanded = expandedDatasetId == dataset.id,
                    onToggleExpand = {
                        expandedDatasetId = if (expandedDatasetId == dataset.id) null else dataset.id
                    },
                    onBookmark = { bookmarksViewModel.toggle(dataset) },
                    onDownload = {
                        downloadStates[dataset.id] = DownloadState.DOWNLOADING
                    },
                    onDelete = {
                        downloadStates[dataset.id] = DownloadState.NOT_DOWNLOADED
                        downloadProgress.remove(dataset.id)
                    },
                    onNavigateToPin = onNavigateToPin
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
    state: DownloadState,
    progress: Float,
    isBookmarked: Boolean = false,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToPin: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    val menuWidth = 200.dp
    val menuWidthPx = with(LocalDensity.current) { menuWidth.toPx() }

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        label = "offset"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFBF8FF))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { onBookmark(); offsetX = 0f },
                    shape = CircleShape,
                    color = if (isBookmarked) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Lesezeichen",
                            modifier = Modifier.size(24.dp),
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, dataset.name)
                            putExtra(Intent.EXTRA_TEXT, "${dataset.name}\n${dataset.description}\nhttps://data.wien.gv.at/")
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                        offsetX = 0f
                    },
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

            Surface(
                modifier = Modifier
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
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
                    .clickable { onToggleExpand() }
                    .fillMaxWidth(),
                color = Color(0xFFFBF8FF)
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(dataset.name, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
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
                        dataset.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } ?: Spacer(modifier = Modifier.size(28.dp))
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
                                    IconButton(onClick = { onDownload(); onToggleExpand() }) {
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
                                    Icon(
                                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            val associatedPins = remember(dataset.id) {
                DataProvider.allPins.filter { it.datasetId == dataset.id }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    "Enthaltene Orte:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                associatedPins.forEach { pin ->
                    ListItem(
                        headlineContent = { Text(pin.name) },
                        supportingContent = { Text(pin.description) },
                        leadingContent = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingContent = { Icon(Icons.Default.Map, contentDescription = "Auf Karte zeigen") },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { onNavigateToPin(pin.id) }
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadsScreenPreview() {
    DownloadsScreen()
}
