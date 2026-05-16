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

    val pins = remember {
        listOf(
            PinInfo("1", "Stephansdom", "Wahrzeichen Wiens, 1. Bezirk", Position(16.3731, 48.2085)),
            PinInfo("2", "Rathaus", "Wiener Rathaus, 1. Bezirk", Position(16.3589, 48.2109)),
            PinInfo("3", "Schönbrunn", "Schloss Schönbrunn, 13. Bezirk", Position(16.3122, 48.1848)),
            PinInfo("4", "Prater Riesenrad", "Wiener Riesenrad, 2. Bezirk", Position(16.3959, 48.2166)),
            PinInfo("5", "Hofburg", "Kaiserliche Residenz, 1. Bezirk", Position(16.3648, 48.2065)),
            PinInfo("6", "Belvedere", "Schloss Belvedere, 3. Bezirk", Position(16.3808, 48.1915)),
            PinInfo("7", "Staatsoper", "Wiener Staatsoper, 1. Bezirk", Position(16.3691, 48.2030)),
            PinInfo("8", "Kunsthistorisches Museum", "Museumsquartier, 1. Bezirk", Position(16.3617, 48.2038)),
            PinInfo("9", "Naturhistorisches Museum", "Museumsquartier, 1. Bezirk", Position(16.3591, 48.2052)),
            PinInfo("10", "Karlskirche", "Barockkirche am Karlsplatz, 4. Bezirk", Position(16.3719, 48.1982)),
            PinInfo("11", "Parlament", "Österreichisches Parlament, 1. Bezirk", Position(16.3592, 48.2081)),
            PinInfo("12", "Burgtheater", "Nationaltheater, 1. Bezirk", Position(16.3614, 48.2103)),
            PinInfo("13", "Albertina", "Kunstmuseum, 1. Bezirk", Position(16.3681, 48.2047)),
            PinInfo("14", "Donauturm", "Aussichtsturm, 22. Bezirk", Position(16.4128, 48.2403)),
            PinInfo("15", "Naschmarkt", "Berühmter Markt, 6. Bezirk", Position(16.3600, 48.1985)),
            PinInfo("16", "Secession", "Ausstellungshaus, 1. Bezirk", Position(16.3657, 48.2003)),
            PinInfo("17", "Hundertwasserhaus", "Architektur-Highlight, 3. Bezirk", Position(16.3931, 48.2076)),
            PinInfo("18", "Universität Wien", "Hauptgebäude, 1. Bezirk", Position(16.3608, 48.2130)),
            PinInfo("19", "Votivkirche", "Neugotische Kirche, 9. Bezirk", Position(16.3597, 48.2152)),
            PinInfo("20", "Zentralfriedhof", "Ehrengräber, 11. Bezirk", Position(16.4397, 48.1508)),
            PinInfo("21", "Tiergarten Schönbrunn", "Ältester Zoo, 13. Bezirk", Position(16.3027, 48.1820)),
            PinInfo("22", "Gloriette", "Aussichtspunkt Schönbrunn, 13. Bezirk", Position(16.3084, 48.1782)),
            PinInfo("23", "Stadtpark", "Johann-Strauß-Denkmal, 1. Bezirk", Position(16.3793, 48.2045)),
            PinInfo("24", "Volksgarten", "Sisi-Denkmal, 1. Bezirk", Position(16.3622, 48.2085)),
            PinInfo("25", "Minoritenkirche", "1. Bezirk", Position(16.3641, 48.2096)),
            PinInfo("26", "Peterskirche", "1. Bezirk", Position(16.3701, 48.2094)),
            PinInfo("27", "Haus des Meeres", "Aquarium im Flakturm, 6. Bezirk", Position(16.3529, 48.1977)),
            PinInfo("28", "Museumsquartier (MQ)", "7. Bezirk", Position(16.3585, 48.2030)),
            PinInfo("29", "Austrian National Library", "Prunksaal, 1. Bezirk", Position(16.3661, 48.2062)),
            PinInfo("30", "Maria-Theresien-Platz", "1. Bezirk", Position(16.3604, 48.2045)),
            PinInfo("31", "Graben", "Einkaufsstraße, 1. Bezirk", Position(16.3698, 48.2088)),
            PinInfo("32", "Kohlmarkt", "Luxusmeile, 1. Bezirk", Position(16.3675, 48.2088)),
            PinInfo("33", "Michaelerplatz", "Antike Ausgrabungen, 1. Bezirk", Position(16.3666, 48.2082)),
            PinInfo("34", "Am Hof", "Historischer Platz, 1. Bezirk", Position(16.3679, 48.2111)),
            PinInfo("35", "Freyung", "Palais-Viertel, 1. Bezirk", Position(16.3653, 48.2116)),
            PinInfo("36", "Hoher Markt", "Ankeruhr, 1. Bezirk", Position(16.3735, 48.2108)),
            PinInfo("37", "Judenplatz", "Holocaust-Mahnmal, 1. Bezirk", Position(16.3696, 48.2116)),
            PinInfo("38", "Stock-im-Eisen-Platz", "1. Bezirk", Position(16.3719, 48.2083)),
            PinInfo("39", "Kursalon Wien", "Stadtpark, 1. Bezirk", Position(16.3779, 48.2023)),
            PinInfo("40", "Schwarzenbergplatz", "Hochstrahlbrunnen, 4. Bezirk", Position(16.3762, 48.1983)),
            PinInfo("41", "Beethoven Museum", "19. Bezirk", Position(16.3557, 48.2447)),
            PinInfo("42", "Sigmund Freud Museum", "9. Bezirk", Position(16.3631, 48.2187)),
            PinInfo("43", "Mozarthaus Vienna", "1. Bezirk", Position(16.3752, 48.2085)),
            PinInfo("44", "Haydnhaus", "6. Bezirk", Position(16.3497, 48.1969)),
            PinInfo("45", "Schubert Geburtshaus", "9. Bezirk", Position(16.3562, 48.2268)),
            PinInfo("46", "MAK", "Museum für angewandte Kunst, 1. Bezirk", Position(16.3815, 48.2076)),
            PinInfo("47", "Leopold Museum", "7. Bezirk", Position(16.3592, 48.2023)),
            PinInfo("48", "MUMOK", "Museum moderner Kunst, 7. Bezirk", Position(16.3578, 48.2033)),
            PinInfo("49", "Technisches Museum", "14. Bezirk", Position(16.3182, 48.1908)),
            PinInfo("50", "Heeresgeschichtliches Museum", "3. Bezirk", Position(16.3879, 48.1848)),
            PinInfo("51", "Kaisergruft", "Kapuzinergruft, 1. Bezirk", Position(16.3701, 48.2057)),
            PinInfo("52", "Palais Liechtenstein", "9. Bezirk", Position(16.3595, 48.2224)),
            PinInfo("53", "Gasometer", "11. Bezirk", Position(16.4189, 48.1851)),
            PinInfo("54", "Wien Hauptbahnhof", "10. Bezirk", Position(16.3772, 48.1851))
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
                baseStyle = mapStyle,
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
