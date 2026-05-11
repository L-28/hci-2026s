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
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlinx.serialization.json.JsonObject
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

    val pins = remember {
        listOf(
            Position(longitude = 16.35, latitude = 48.20),
            Position(longitude = 16.38, latitude = 48.22),
            Position(longitude = 16.34, latitude = 48.25),
            Position(longitude = 16.39, latitude = 48.18)
        )
    }

    val pinPainter = rememberVectorPainter(Icons.Default.Place)

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
                val pinSource = rememberGeoJsonSource(
                    data = GeoJsonData.Features(
                        FeatureCollection(
                            pins.map { Feature(geometry = Point(it), properties = JsonObject(emptyMap())) }
                        )
                    )
                )

                SymbolLayer(
                    id = "pins",
                    source = pinSource,
                    iconImage = image(
                        value = pinPainter,
                        drawAsSdf = true
                    ),
                    iconColor = const(Color.Black),
                    iconSize = const(1.5f),
                    iconAllowOverlap = const(true)
                )
            }

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
