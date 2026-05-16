package at.l28.hci.mapapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color.Yellow,
    onPrimary = Color.Black,
    secondary = Color.Cyan,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = Color.Red,
    onError = Color.White
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF0000AA),
    onPrimary = Color.White,
    secondary = Color(0xFF006600),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color.Red,
    onError = Color.White
)

@Composable
fun MapAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    useHighContrast: Boolean = false,
    useLargeFont: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useHighContrast -> {
            if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(useLargeFont),
        content = content
    )
}
