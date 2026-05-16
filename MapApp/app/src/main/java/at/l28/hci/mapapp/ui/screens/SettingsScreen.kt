package at.l28.hci.mapapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import at.l28.hci.mapapp.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = viewModel(),
    bookmarksViewModel: BookmarksViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    storageViewModel: StorageViewModel = viewModel()
) {
    var expandedItem by remember { mutableStateOf<String?>(null) }

    val settingsItems = listOf(
        SettingItem("Profil", "${profileViewModel.name}", Icons.Default.AccountCircle, isExpandable = true),
        SettingItem("Lesezeichen", "Gespicherte Orte und Routen", Icons.Default.BookmarkBorder, isExpandable = true),
        SettingItem("Speicherverwaltung", "Offline-Karten (${storageViewModel.totalUsedSpace})", Icons.Default.FolderOpen, isExpandable = true),
        SettingItem("Darstellung", "Design und Farbschema anpassen", Icons.Default.LightMode, isExpandable = true),
        SettingItem("Barrierefreiheit", "Schriftgröße und Kontrast", Icons.Default.Accessibility, isExpandable = true),
        SettingItem("Über die App", "Versionsinfo und Entwickler", Icons.Default.Info, isExpandable = true),
        SettingItem("Impressum", "Rechtliche Hinweise", Icons.Default.Description, isExpandable = true)
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
                        "Profil" -> ProfileSection(profileViewModel)
                        "Darstellung" -> ThemeSelection(themeViewModel)
                        "Lesezeichen" -> BookmarksSection(bookmarksViewModel)
                        "Speicherverwaltung" -> StorageSection(storageViewModel)
                        "Barrierefreiheit" -> AccessibilitySection(themeViewModel)
                        "Über die App" -> AboutSection()
                        "Impressum" -> ImpressumSection()
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

@Composable
fun ProfileSection(profileViewModel: ProfileViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = profileViewModel.name,
            onValueChange = { profileViewModel.name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = profileViewModel.birthDate,
            onValueChange = { profileViewModel.birthDate = it },
            label = { Text("Geburtsdatum") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = profileViewModel.email,
            onValueChange = { profileViewModel.email = it },
            label = { Text("E-Mail") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StorageSection(storageViewModel: StorageViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Speicherplatz", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { 0.15f },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "${storageViewModel.totalUsedSpace} von ${storageViewModel.totalAvailableSpace} verwendet",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Offline-Karten", style = MaterialTheme.typography.titleSmall)
        storageViewModel.offlineMaps.forEach { map ->
            ListItem(
                headlineContent = { Text(map.name) },
                supportingContent = { Text(map.size) },
                trailingContent = {
                    IconButton(onClick = { storageViewModel.deleteMap(map) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Löschen")
                    }
                }
            )
        }
        Button(
            onClick = { /* Add more maps */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Karte hinzufügen")
        }
    }
}

@Composable
fun AccessibilitySection(themeViewModel: ThemeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Große Schrift")
            Switch(
                checked = themeViewModel.useLargeFont,
                onCheckedChange = { themeViewModel.setLargeFont(it) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Hoher Kontrast")
            Switch(
                checked = themeViewModel.useHighContrast,
                onCheckedChange = { themeViewModel.setHighContrast(it) }
            )
        }
    }
}

@Composable
fun ImpressumSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Impressum", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n" +
            "Herausgeber: L28-HCI Systems GmbH\n" +
            "Adresse: Wiedner Hauptstraße 8-10, 1040 Wien\n" +
            "E-Mail: contact@l28-hci.at",
            style = MaterialTheme.typography.bodyMedium
        )
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
fun BookmarksSection(bookmarksViewModel: BookmarksViewModel) {
    val bookmarks = bookmarksViewModel.bookmarks
    Column(modifier = Modifier.fillMaxWidth()) {
        if (bookmarks.isEmpty()) {
            Text(
                "Keine Lesezeichen vorhanden",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            bookmarks.forEach { dataset ->
                ListItem(
                    headlineContent = { Text(dataset.name) },
                    supportingContent = { Text(dataset.category) },
                    leadingContent = {
                        Icon(dataset.icon, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
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
