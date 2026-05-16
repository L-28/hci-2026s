package at.l28.hci.mapapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DownloadsScreen() {
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

    var expandedId by remember { mutableStateOf<String?>(datasets[3].name) }

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
            )
        }
    }
}

@Composable
fun DatasetItem(
    dataset: Dataset,
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
                }
            }
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

data class Dataset(
    val name: String,
    val description: String,
    val size: String,
    val isDownloaded: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun DownloadsScreenPreview() {
    DownloadsScreen()
}
