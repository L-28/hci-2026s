package at.l28.hci.mapapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import at.l28.hci.mapapp.viewmodels.ThemeMode
import at.l28.hci.mapapp.viewmodels.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel = viewModel()) {
    var expandedItem by remember { mutableStateOf<String?>(null) }

    val settingsItems = listOf(
        SettingItem("Profil", "Max Mustermann, 28 Jahre", Icons.Default.Settings),
        SettingItem("Lesezeichen", "Gespicherte Orte und Routen", Icons.Default.BookmarkBorder),
        SettingItem("Speicherverwaltung", "Offline-Karten (1.2 GB)", Icons.Default.FolderOpen),
        SettingItem("Darstellung", "Design und Farbschema anpassen", Icons.Default.LightMode, isExpandable = true),
        SettingItem("Barrierefreiheit", "Schriftgröße und Kontrast", Icons.Default.Accessibility),
        SettingItem("Über die App", "Versionsinfo und Entwickler", Icons.Default.Info, isExpandable = true),
        SettingItem("Impressum", "Rechtliche Hinweise", Icons.Default.Description)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(settingsItems) { item ->
            val isExpanded = expandedItem == item.title

            Column {
                ListItem(
                    headlineContent = { Text(item.title, fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text(item.description) },
                    leadingContent = { Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable {
                        expandedItem = if (isExpanded) null else item.title
                    }
                )

                AnimatedVisibility(visible = isExpanded) {
                    when (item.title) {
                        "Darstellung" -> ThemeSelection(themeViewModel)
                        "Über die App" -> AboutSection()
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelection(themeViewModel: ThemeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val options = listOf("Hell", "Dunkel", "System")
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, label ->
                val mode = when (index) {
                    0 -> ThemeMode.LIGHT
                    1 -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { themeViewModel.setTheme(mode) },
                    selected = themeViewModel.themeMode == mode
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "MapApp v2.4.1 (Stable)",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Entwickelt von L28-HCI Systems in Wien. Unser Ziel ist es, die Navigation im urbanen Raum so intuitiv wie möglich zu gestalten.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "© 2026 L28-HCI Systems. Alle Rechte vorbehalten.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Check for updates */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Nach Updates suchen")
        }
    }
}

data class SettingItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isExpandable: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
