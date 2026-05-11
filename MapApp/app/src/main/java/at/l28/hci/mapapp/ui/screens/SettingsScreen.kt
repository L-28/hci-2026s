package at.l28.hci.mapapp.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen() {
    val settingsItems = listOf(
        SettingItem("Profil", "Profilname, Alter, Geschlecht", Icons.Default.Settings),
        SettingItem("Lesezeichen", "Lorem ipsum dolor sit amet.", Icons.Default.BookmarkBorder),
        SettingItem("Speicherverwaltung", "Lorem ipsum dolor sit amet.", Icons.Default.FolderOpen),
        SettingItem("Darstellung", "Dunkelmodus, Textgröße", Icons.Default.LightMode),
        SettingItem("Barrierefreiheit", "Lorem ipsum dolor sit amet.", Icons.Default.Accessibility),
        SettingItem("Über die App", "Lorem ipsum dolor sit amet.", Icons.Default.Info),
        SettingItem("Impressum", "Lorem ipsum dolor sit amet.", Icons.Default.Description)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(settingsItems) { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = { Text(item.description) },
                leadingContent = { Icon(item.icon, contentDescription = null) }
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

data class SettingItem(val title: String, val description: String, val icon: ImageVector)

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
