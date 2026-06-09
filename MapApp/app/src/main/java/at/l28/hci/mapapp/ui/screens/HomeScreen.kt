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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import at.l28.hci.mapapp.viewmodels.DatasetsViewModel
import at.l28.hci.mapapp.viewmodels.MapPin

private val categoryColors = mapOf(
    "Verkehr" to Color(0xFF1565C0),
    "Umwelt" to Color(0xFF2E7D32),
    "Kultur" to Color(0xFFE65100),
    "Infrastruktur" to Color(0xFF6A1B9A),
)

private val categorySizes = mapOf(
    "Verkehr" to 1.8f,
    "Umwelt" to 1.2f,
    "Kultur" to 1.6f,
    "Infrastruktur" to 1.3f,
)

private fun MapPin.toFeature() = Feature(
    geometry = Point(Position(longitude, latitude)),
    properties = JsonObject(emptyMap())
)

private fun allPinFeatures(pins: List<MapPin>) = GeoJsonData.Features(
    FeatureCollection(pins.map { it.toFeature() })
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(datasetsViewModel: DatasetsViewModel = viewModel()) {
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
            val padding = if (containerHeightDp > 0.dp) {
                (containerHeightDp - sheetTopDp).coerceAtLeast(0.dp)
            } else 140.dp
            padding + 16.dp
        }
    }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = 16.3738, latitude = 48.2082),
            zoom = 12.0
        )
    )

    val activePins = datasetsViewModel.getActivePins()
    var selectedPin by remember { mutableStateOf<MapPin?>(null) }
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    selectedPin!!.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    selectedPin!!.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColors[selectedPin!!.category] ?: MaterialTheme.colorScheme.primary
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
                onDismiss = { scope.launch { sheetState.hide() } }
            )
        },
        sheetPeekHeight = 125.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) { _ ->
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
                        isScaleBarEnabled = false,
                        logoAlignment = Alignment.BottomStart,
                        attributionAlignment = Alignment.BottomEnd,
                        padding = PaddingValues(bottom = ornamentPaddingBottom, start = 16.dp, end = 16.dp)
                    )
                ),
                onMapClick = { clickedPosition, _ ->
                    val nearest = activePins.minByOrNull { pin ->
                        val dx = pin.longitude - clickedPosition.longitude
                        val dy = pin.latitude - clickedPosition.latitude
                        dx * dx + dy * dy
                    }
                    if (nearest != null) {
                        val dx = nearest.longitude - clickedPosition.longitude
                        val dy = nearest.latitude - clickedPosition.latitude
                        if (dx * dx + dy * dy < 0.0001) {
                            selectedPin = nearest
                            ClickResult.Consume
                        } else ClickResult.Pass
                    } else ClickResult.Pass
                }
            ) {
                val pinSource = rememberGeoJsonSource(data = allPinFeatures(activePins))

                SymbolLayer(
                    id = "pins",
                    source = pinSource,
                    iconImage = image(value = pinPainter, drawAsSdf = true),
                    iconColor = const(MaterialTheme.colorScheme.primary),
                    iconSize = const(1.5f),
                    iconAllowOverlap = const(true)
                )
            }

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
