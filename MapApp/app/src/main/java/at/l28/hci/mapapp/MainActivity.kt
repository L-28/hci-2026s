package at.l28.hci.mapapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import at.l28.hci.mapapp.ui.screens.DownloadsScreen
import at.l28.hci.mapapp.ui.screens.HomeScreen
import at.l28.hci.mapapp.ui.screens.SettingsScreen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import at.l28.hci.mapapp.ui.theme.MapAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import at.l28.hci.mapapp.viewmodels.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            MapAppTheme(darkTheme = themeViewModel.isDarkMode) {
                MapAppApp(themeViewModel)
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun MapAppApp(themeViewModel: ThemeViewModel = viewModel()) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

    NavigationSuiteScaffold(
        modifier = Modifier.fillMaxSize(),
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            when (currentDestination) {
                AppDestinations.MAP -> HomeScreen()
                AppDestinations.DOWNLOADS -> DownloadsScreen()
                AppDestinations.SETTINGS -> SettingsScreen(themeViewModel)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    MAP("Map", Icons.Default.Map),
    DOWNLOADS("Downloads", Icons.Default.Download),
    SETTINGS("Settings", Icons.Default.Menu),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapAppTheme {
        Greeting("Android")
    }
}