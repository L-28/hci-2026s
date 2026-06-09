package at.l28.hci.mapapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
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
import kotlinx.serialization.json.JsonPrimitive
import at.l28.hci.mapapp.data.DataProvider
import at.l28.hci.mapapp.models.PinInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    initialPinId: String? = null,
    onNavigateToDownloads: () -> Unit = {},
    onPinNavigated: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
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

    val mapStyle by produceState<BaseStyle>(
        initialValue = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
    ) {
        withContext(Dispatchers.IO) {
            try {
                val styleJson = URL("https://tiles.openfreemap.org/styles/liberty").readText()
                val localizedJson = styleJson
                    .replace("\"name:en\"", "\"name:de\"")
                    .replace("\"name_en\"", "\"name_de\"")
                    .replace("{name}", "{name:de}")
                value = BaseStyle.Json(localizedJson)
            } catch (e: Exception) {
                // Fallback to default
            }
        }
    }

    val pins = remember { DataProvider.allPins }

    var selectedPin by remember { mutableStateOf<PinInfo?>(null) }

    LaunchedEffect(initialPinId) {
        if (initialPinId != null) {
            val target = pins.find { it.id == initialPinId }
            if (target != null) {
                selectedPin = target
                cameraState.animateTo(
                    CameraPosition(
                        target = target.position,
                        zoom = 15.0
                    )
                )
                onPinNavigated()
            }
        }
    }
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
                Spacer(modifier = Modifier.height(24.dp))
                
                val associatedDataset = remember(selectedPin) {
                    DataProvider.allDatasets.find { it.id == selectedPin!!.datasetId }
                }
                
                associatedDataset?.let { dataset ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = dataset.icon ?: Icons.Default.Stars,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Datensatz",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    dataset.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            IconButton(onClick = {
                                selectedPin = null
                                onNavigateToDownloads()
                            }) {
                                Icon(  Icons.AutoMirrored.Default.ArrowForward, contentDescription = "Datensatz anzeigen")
                            }
                        }
                    }
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchSheetContent(
                recentSearches = recentSearches,
                onSearch = { query ->
                    if (query.isNotBlank()) {
                        if (!recentSearches.contains(query)) {
                            recentSearches.add(0, query)
                            if (recentSearches.size > 5) recentSearches.removeLast()
                        }
                        
                        // Search for pin
                        val pin = pins.find {
                            it.name.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                        }
                        if (pin != null) {
                            selectedPin = pin
                            scope.launch {
                                cameraState.animateTo(
                                    CameraPosition(target = pin.position, zoom = 15.0)
                                )
                                sheetState.partialExpand()
                            }
                        }
                    }
                },
                onExpand = {
                    scope.launch { sheetState.expand() }
                },
                onDismiss = {
                    scope.launch { sheetState.partialExpand() }
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
                baseStyle = mapStyle,
                options = MapOptions(
                    ornamentOptions = OrnamentOptions(
                        isScaleBarEnabled = false, // Disable native scale bar to use custom one
                        isCompassEnabled = false,
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
                            scope.launch {
                                cameraState.animateTo(
                                    CameraPosition(
                                        target = nearest.position,
                                        zoom = 15.0
                                    )
                                )
                            }
                            ClickResult.Consume
                        } else ClickResult.Pass
                    } else ClickResult.Pass
                }
            ) {
                val pinSource = rememberGeoJsonSource(
                    data = remember(selectedPin) {
                        GeoJsonData.Features(
                            FeatureCollection(
                                pins.map { pin ->
                                    val dataset = DataProvider.allDatasets.find { it.id == pin.datasetId }
                                    val color = DataProvider.getColorStringForCategory(dataset?.category ?: "")
                                    Feature(
                                        geometry = Point(pin.position),
                                        properties = JsonObject(
                                            mapOf(
                                                "color" to JsonPrimitive(color),
                                                "selected" to JsonPrimitive(pin.id == selectedPin?.id)
                                            )
                                        )
                                    )
                                }
                            )
                        )
                    }
                )

                SymbolLayer(
                    id = "pins",
                    source = pinSource,
                    iconImage = image(
                        value = pinPainter,
                        drawAsSdf = true
                    ),
                    iconColor = feature["color"].convertToColor(),
                    iconSize = switch(
                        condition(feature["selected"].convertToBoolean(), const(2.5f)),
                        fallback = const(1.5f)
                    ),
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheetContent(
    recentSearches: List<String> = emptyList(),
    onSearch: (String) -> Unit = {},
    onExpand: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Update expanded based on callback from SearchBar
    val handleExpandedChange: (Boolean) -> Unit = {
        expanded = it
        if (it) onExpand()
    }

    // Automatically expand while typing
    LaunchedEffect(query) {
        if (query.isNotEmpty() && !expanded) {
            handleExpandedChange(true)
        }
    }

    val pins = remember { DataProvider.allPins }
    val suggestions = remember(query) {
        if (query.isBlank()) {
            recentSearches.map { SearchResult(it, "Frühere Suche") }
        } else {
            pins.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }.map { SearchResult(it.name, it.description) }
        }
    }

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
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = handleExpandedChange,
                    placeholder = { Text("Suchen") },
                    leadingIcon = {
                        IconButton(onClick = {
                            if (expanded) {
                                expanded = false
                                query = ""
                            }
                            onDismiss()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        } else {
                            Icon(Icons.Default.Mic, contentDescription = null)
                        }
                    },
                    modifier = Modifier.onFocusChanged { 
                        if (it.isFocused && !expanded) {
                            handleExpandedChange(true)
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = handleExpandedChange,
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets(0)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(suggestions) { result ->
                    ListItem(
                        headlineContent = { Text(result.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(result.description) },
                        leadingContent = {
                            Icon(
                                if (query.isBlank()) Icons.Default.History else Icons.Default.Place,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            onSearch(result.title)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (!expanded) {
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
                            leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                            modifier = Modifier.clickable { onSearch(search) }
                        )
                    }
                }
            } else {
                val staticSuggestions = listOf(
                    SearchResult("Stephansdom", "Wahrzeichen Wiens, 1. Bezirk"),
                    SearchResult("Schloss Schönbrunn", "Kaiserliche Sommerresidenz"),
                    SearchResult("Prater Riesenrad", "Wiener Riesenrad, 2. Bezirk")
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(staticSuggestions) { result ->
                        ListItem(
                            headlineContent = { Text(result.title, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(result.description) },
                            leadingContent = { Icon(Icons.Default.AccountTree, contentDescription = null) },
                            modifier = Modifier.clickable { onSearch(result.title) }
                        )
                    }
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
