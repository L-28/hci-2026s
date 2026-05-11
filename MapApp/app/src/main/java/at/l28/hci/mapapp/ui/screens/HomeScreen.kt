package at.l28.hci.mapapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = 16.3738, latitude = 48.2082),
            zoom = 12.0
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchSheetContent()
        },
        sheetPeekHeight = 300.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
            ) {
                // Pins will be added as custom overlays or using the library's features
                // For now, let's keep the UI pins as overlays to ensure they are visible
            }
            
            // Map Pins (Overlaid on top of MaplibreMap for now)
            MapPin(Modifier.offset(x = 100.dp, y = 200.dp))
            MapPin(Modifier.offset(x = 250.dp, y = 350.dp))
            MapPin(Modifier.offset(x = 50.dp, y = 500.dp))
            MapPin(Modifier.offset(x = 300.dp, y = 150.dp), isSelected = true)

            // Scale/Zoom UI elements
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .width(4.dp)
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

@Composable
fun MapPin(modifier: Modifier = Modifier, isSelected: Boolean = false) {
    Box(modifier = modifier) {
        Icon(
            imageVector = if (isSelected) Icons.Default.LocationOn else Icons.Default.Place,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
            modifier = Modifier.size(32.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = "",
                    onQueryChange = {},
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Suchen") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.Mic, contentDescription = null) },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(16.dp))

        val searchResults = listOf(
            SearchResult("Voranschlag Wien 2026 (Gemeinde)", "Voranschlag der Gemeinde - Einnahmen und..."),
            SearchResult("Verkehrsnetz der Wiener Linien Wien", "Länge des Verkehrsnetzes der Wiener Linien..."),
            SearchResult("Bericht - Laichhilfen Wienfluss 2023 Wien", "Ergebnisse des Laichhilfeprojekts im...")
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(searchResults) { result ->
                ListItem(
                    headlineContent = { Text(result.title, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(result.description) },
                    leadingContent = { Icon(Icons.Default.AccountTree, contentDescription = null) }
                )
            }
        }
    }
}

data class SearchResult(val title: String, val description: String)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
