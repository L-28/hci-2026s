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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.ScaleBar
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

data class PinInfo(
    val id: String,
    val name: String,
    val description: String,
    val position: Position
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val density = LocalDensity.current
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    
    val ornamentPaddingBottom by remember {
        derivedStateOf {
            val sheetTopOffset = try { sheetState.requireOffset() } catch (_: Exception) { 0f }
            val sheetTopDp = with(density) { sheetTopOffset.toDp() }
            val containerHeightDp = with(density) { containerSize.height.toDp() }
            
            // Padding from bottom of the Map container = ContainerHeight - SheetTop
            val padding = if (containerHeightDp > 0.dp) {
                (containerHeightDp - sheetTopDp).coerceAtLeast(0.dp)
            } else 140.dp // Fallback to peek height

            padding + 16.dp
        }
    }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = 16.3738, latitude = 48.2082),
            zoom = 12.0
        )
    )

    val pins = remember {
        listOf(
            PinInfo("1", "Rathaus", "Wiener Rathaus, 1. Bezirk", Position(longitude = 16.35, latitude = 48.20)),
            PinInfo("2", "Stephansdom", "Wahrzeichen Wiens, 1. Bezirk", Position(longitude = 16.38, latitude = 48.22)),
            PinInfo("3", "Schönbrunn", "Schloss Schönbrunn, 13. Bezirk", Position(longitude = 16.34, latitude = 48.25)),
            PinInfo("4", "Prater", "Wiener Prater & Riesenrad, 2. Bezirk", Position(longitude = 16.39, latitude = 48.18))
        )
    }

    var selectedPin by remember { mutableStateOf<PinInfo?>(null) }
    val recentSearches = remember { mutableStateListOf<String>() }

    val pinPainter = rememberVectorPainter(Icons.Default.Place)

    if (selectedPin != null) {
        ModalBottomSheet(onDismissRequest = { selectedPin = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    selectedPin!!.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    selectedPin!!.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchSheetContent(
                recentSearches = recentSearches,
                onSearch = { query ->
                    if (query.isNotBlank() && !recentSearches.contains(query)) {
                        recentSearches.add(0, query)
                        if (recentSearches.size > 5) recentSearches.removeLast()
                    }
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }
                }
            )
        },
        sheetPeekHeight = 125.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) { _ -> // Use _ to ignore innerPadding which was pushing map content up
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { containerSize = it.size }
        ) {
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                options = MapOptions(
                    ornamentOptions = OrnamentOptions(
                        isScaleBarEnabled = false, // Disable native scale bar to use custom one
                        logoAlignment = Alignment.BottomStart,
                        attributionAlignment = Alignment.BottomEnd,
                        padding = PaddingValues(bottom = ornamentPaddingBottom, start = 16.dp, end = 16.dp)
                    )
                ),
                onMapClick = { clickedPosition, _ ->
                    val nearest = pins.minByOrNull { pin ->
                        val dx = pin.position.longitude - clickedPosition.longitude
                        val dy = pin.position.latitude - clickedPosition.latitude
                        dx * dx + dy * dy
                    }
                    if (nearest != null) {
                        val dx = nearest.position.longitude - clickedPosition.longitude
                        val dy = nearest.position.latitude - clickedPosition.latitude
                        if (dx * dx + dy * dy < 0.0001) {
                            selectedPin = nearest
                            ClickResult.Consume
                        } else ClickResult.Pass
                    } else ClickResult.Pass
                }
            ) {
                val pinSource = rememberGeoJsonSource(
                    data = GeoJsonData.Features(
                        FeatureCollection(
                            pins.map { Feature(geometry = Point(it.position), properties = JsonObject(emptyMap())) }
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

            // Custom Compose ScaleBar overlay
            ScaleBar(
                metersPerDp = cameraState.metersPerDpAtTarget,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = ornamentPaddingBottom + 24.dp, end = 16.dp)
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheetContent(
    recentSearches: List<String> = emptyList(),
    onSearch: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        onSearch(it)
                        query = ""
                    },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Suchen") },
                    leadingIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    trailingIcon = { Icon(Icons.Default.Mic, contentDescription = null) },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets(0)
        ) { }

        Spacer(modifier = Modifier.height(16.dp))

        if (recentSearches.isNotEmpty()) {
            Text(
                "Frühere Suchen",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(recentSearches) { search ->
                    ListItem(
                        headlineContent = { Text(search) },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                }
            }
        } else {
            val suggestions = listOf(
                SearchResult("Voranschlag Wien 2026 (Gemeinde)", "Voranschlag der Gemeinde - Einnahmen und..."),
                SearchResult("Verkehrsnetz der Wiener Linien Wien", "Länge des Verkehrsnetzes der Wiener Linien..."),
                SearchResult("Bericht - Laichhilfen Wienfluss 2023 Wien", "Ergebnisse des Laichhilfeprojekts im...")
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(suggestions) { result ->
                    ListItem(
                        headlineContent = { Text(result.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(result.description) },
                        leadingContent = { Icon(Icons.Default.AccountTree, contentDescription = null) }
                    )
                }
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
