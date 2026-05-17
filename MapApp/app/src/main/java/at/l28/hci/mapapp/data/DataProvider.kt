package at.l28.hci.mapapp.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import at.l28.hci.mapapp.models.Dataset
import at.l28.hci.mapapp.models.DownloadState
import at.l28.hci.mapapp.models.PinInfo
import org.maplibre.spatialk.geojson.Position

object DataProvider {
    val allDatasets = listOf(
        Dataset("map_vienna", "Wien - Basiskarte", "Basis", "Vollständige Offline-Karte für das gesamte Stadtgebiet.", "412 MB", DownloadState.DOWNLOADED, Icons.Default.Map),
        Dataset("map_vienna_center", "Wien - Innere Stadt (HD)", "Basis", "Hochauflösende Details für den 1. Bezirk.", "85 MB", DownloadState.NOT_DOWNLOADED, Icons.Default.LocationCity),
        Dataset("wl_network", "Wiener Linien - Netz", "Verkehr", "S-Bahn, U-Bahn, Straßenbahn und Busstationen.", "12 MB", DownloadState.DOWNLOADED, Icons.Default.DirectionsBus),
        Dataset("bike_lanes", "Radwege Wien", "Verkehr", "Umfassendes Verzeichnis aller Radwege und Abstellplätze.", "4.5 MB", DownloadState.NOT_DOWNLOADED, Icons.AutoMirrored.Filled.DirectionsBike),
        Dataset("trees", "Wiener Baumkataster", "Umwelt", "Standorte und Arten von über 200.000 Stadtbäumen.", "38 MB", DownloadState.NOT_DOWNLOADED, Icons.Default.Park),
        Dataset("fountains", "Trinkbrunnen & Kühlung", "Umwelt", "Wasserspender und Nebelduschen für heiße Tage.", "1.2 MB", DownloadState.DOWNLOADED, Icons.Default.WaterDrop),
        Dataset("museums", "Kultur & Tourismus", "Kultur", "Museen, Denkmäler und historische Sehenswürdigkeiten.", "15 MB", DownloadState.NOT_DOWNLOADED, Icons.Default.Museum),
        Dataset("history_1912", "Stadtplan 1912", "Kultur", "Historische Kartenebene für Zeitreisen durch Wien.", "1.2 GB", DownloadState.NOT_DOWNLOADED, Icons.Default.HistoryEdu),
        Dataset("wifi", "Free Wave Hotspots", "Infrastruktur", "Öffentliche WLAN-Standorte in ganz Wien.", "0.8 MB", DownloadState.DOWNLOADED, Icons.Default.Wifi)
    )

    val allPins = listOf(
        PinInfo("1", "Stephansdom", "Wahrzeichen Wiens, 1. Bezirk", Position(16.3731, 48.2085), "map_vienna_center"),
        PinInfo("2", "Rathaus", "Wiener Rathaus, 1. Bezirk", Position(16.3589, 48.2109), "map_vienna_center"),
        PinInfo("3", "Schönbrunn", "Schloss Schönbrunn, 13. Bezirk", Position(16.3122, 48.1848), "museums"),
        PinInfo("4", "Prater Riesenrad", "Wiener Riesenrad, 2. Bezirk", Position(16.3959, 48.2166), "map_vienna"),
        PinInfo("5", "Hofburg", "Kaiserliche Residenz, 1. Bezirk", Position(16.3648, 48.2065), "map_vienna_center"),
        PinInfo("6", "Belvedere", "Schloss Belvedere, 3. Bezirk", Position(16.3808, 48.1915), "museums"),
        PinInfo("7", "Staatsoper", "Wiener Staatsoper, 1. Bezirk", Position(16.3691, 48.2030), "map_vienna_center"),
        PinInfo("8", "Kunsthistorisches Museum", "Museumsquartier, 1. Bezirk", Position(16.3617, 48.2038), "museums"),
        PinInfo("9", "Naturhistorisches Museum", "Museumsquartier, 1. Bezirk", Position(16.3591, 48.2052), "museums"),
        PinInfo("10", "Karlskirche", "Barockkirche am Karlsplatz, 4. Bezirk", Position(16.3719, 48.1982), "map_vienna_center"),
        PinInfo("11", "Parlament", "Österreichisches Parlament, 1. Bezirk", Position(16.3592, 48.2081), "map_vienna_center"),
        PinInfo("12", "Burgtheater", "Nationaltheater, 1. Bezirk", Position(16.3614, 48.2103), "map_vienna_center"),
        PinInfo("13", "Albertina", "Kunstmuseum, 1. Bezirk", Position(16.3681, 48.2047), "museums"),
        PinInfo("14", "Donauturm", "Aussichtsturm, 22. Bezirk", Position(16.4128, 48.2403), "map_vienna"),
        PinInfo("15", "Naschmarkt", "Berühmter Markt, 6. Bezirk", Position(16.3600, 48.1985), "map_vienna"),
        PinInfo("16", "Secession", "Ausstellungshaus, 1. Bezirk", Position(16.3657, 48.2003), "museums"),
        PinInfo("17", "Hundertwasserhaus", "Architektur-Highlight, 3. Bezirk", Position(16.3931, 48.2076), "map_vienna"),
        PinInfo("18", "Universität Wien", "Hauptgebäude, 1. Bezirk", Position(16.3608, 48.2130), "map_vienna_center"),
        PinInfo("19", "Votivkirche", "Neugotische Kirche, 9. Bezirk", Position(16.3597, 48.2152), "map_vienna_center"),
        PinInfo("20", "Zentralfriedhof", "Ehrengräber, 11. Bezirk", Position(16.4397, 48.1508), "map_vienna"),
        PinInfo("21", "Tiergarten Schönbrunn", "Ältester Zoo, 13. Bezirk", Position(16.3027, 48.1820), "museums"),
        PinInfo("22", "Gloriette", "Aussichtspunkt Schönbrunn, 13. Bezirk", Position(16.3084, 48.1782), "museums"),
        PinInfo("23", "Stadtpark", "Johann-Strauß-Denkmal, 1. Bezirk", Position(16.3793, 48.2045), "map_vienna_center"),
        PinInfo("24", "Volksgarten", "Sisi-Denkmal, 1. Bezirk", Position(16.3622, 48.2085), "map_vienna_center"),
        PinInfo("25", "Minoritenkirche", "1. Bezirk", Position(16.3641, 48.2096), "map_vienna_center"),
        PinInfo("26", "Peterskirche", "1. Bezirk", Position(16.3701, 48.2094), "map_vienna_center"),
        PinInfo("27", "Haus des Meeres", "Aquarium im Flakturm, 6. Bezirk", Position(16.3529, 48.1977), "map_vienna"),
        PinInfo("28", "Museumsquartier (MQ)", "7. Bezirk", Position(16.3585, 48.2030), "museums"),
        PinInfo("29", "Austrian National Library", "Prunksaal, 1. Bezirk", Position(16.3661, 48.2062), "museums"),
        PinInfo("30", "Maria-Theresien-Platz", "1. Bezirk", Position(16.3604, 48.2045), "map_vienna_center"),
        PinInfo("31", "Graben", "Einkaufsstraße, 1. Bezirk", Position(16.3698, 48.2088), "map_vienna_center"),
        PinInfo("32", "Kohlmarkt", "Luxusmeile, 1. Bezirk", Position(16.3675, 48.2088), "map_vienna_center"),
        PinInfo("33", "Michaelerplatz", "Antike Ausgrabungen, 1. Bezirk", Position(16.3666, 48.2082), "map_vienna_center"),
        PinInfo("34", "Am Hof", "Historischer Platz, 1. Bezirk", Position(16.3679, 48.2111), "map_vienna_center"),
        PinInfo("35", "Freyung", "Palais-Viertel, 1. Bezirk", Position(16.3653, 48.2116), "map_vienna_center"),
        PinInfo("36", "Hoher Markt", "Ankeruhr, 1. Bezirk", Position(16.3735, 48.2108), "map_vienna_center"),
        PinInfo("37", "Judenplatz", "Holocaust-Mahnmal, 1. Bezirk", Position(16.3696, 48.2116), "map_vienna_center"),
        PinInfo("38", "Stock-im-Eisen-Platz", "1. Bezirk", Position(16.3719, 48.2083), "map_vienna_center"),
        PinInfo("39", "Kursalon Wien", "Stadtpark, 1. Bezirk", Position(16.3779, 48.2023), "map_vienna_center"),
        PinInfo("40", "Schwarzenbergplatz", "Hochstrahlbrunnen, 4. Bezirk", Position(16.3762, 48.1983), "fountains"),
        PinInfo("41", "Beethoven Museum", "19. Bezirk", Position(16.3557, 48.2447), "museums"),
        PinInfo("42", "Sigmund Freud Museum", "9. Bezirk", Position(16.3631, 48.2187), "museums"),
        PinInfo("43", "Mozarthaus Vienna", "1. Bezirk", Position(16.3752, 48.2085), "museums"),
        PinInfo("44", "Haydnhaus", "6. Bezirk", Position(16.3497, 48.1969), "museums"),
        PinInfo("45", "Schubert Geburtshaus", "9. Bezirk", Position(16.3562, 48.2268), "museums"),
        PinInfo("46", "MAK", "Museum für angewandte Kunst, 1. Bezirk", Position(16.3815, 48.2076), "museums"),
        PinInfo("47", "Leopold Museum", "7. Bezirk", Position(16.3592, 48.2023), "museums"),
        PinInfo("48", "MUMOK", "Museum moderner Kunst, 7. Bezirk", Position(16.3578, 48.2033), "museums"),
        PinInfo("49", "Technisches Museum", "14. Bezirk", Position(16.3182, 48.1908), "museums"),
        PinInfo("50", "Heeresgeschichtliches Museum", "3. Bezirk", Position(16.3879, 48.1848), "museums"),
        PinInfo("51", "Kaisergruft", "Kapuzinergruft, 1. Bezirk", Position(16.3701, 48.2057), "map_vienna_center"),
        PinInfo("52", "Palais Liechtenstein", "9. Bezirk", Position(16.3595, 48.2224), "museums"),
        PinInfo("53", "Gasometer", "11. Bezirk", Position(16.4189, 48.1851), "map_vienna"),
        PinInfo("54", "Wien Hauptbahnhof", "10. Bezirk", Position(16.3772, 48.1851), "wl_network"),
        // New Pins for missing datasets
        PinInfo("55", "Opernring Radweg", "Wichtige Radverkehrsverbindung", Position(16.368, 48.204), "bike_lanes"),
        PinInfo("56", "Rathauspark-Eiche", "Historischer Baum im Rathauspark", Position(16.359, 48.211), "trees"),
        PinInfo("57", "Virgilkapelle", "Mittelalterliche Kapelle (U-Bahn Station Stephansplatz)", Position(16.373, 48.208), "history_1912"),
        PinInfo("58", "Westbahnhof WiFi", "Free Wave Hotspot am Westbahnhof", Position(16.337, 48.196), "wifi")
    )

    fun getColorForCategory(category: String): Color {
        return when (category) {
            "Basis" -> Color(0xFF9E9E9E) // Grey
            "Verkehr" -> Color(0xFF2196F3) // Blue
            "Umwelt" -> Color(0xFF4CAF50) // Green
            "Kultur" -> Color(0xFFFF9800) // Orange
            "Infrastruktur" -> Color(0xFF9C27B0) // Purple
            else -> Color.Black
        }
    }

    fun getColorStringForCategory(category: String): String {
        val color = getColorForCategory(category)
        return String.format("#%06X", 0xFFFFFF and color.toArgb())
    }
}
