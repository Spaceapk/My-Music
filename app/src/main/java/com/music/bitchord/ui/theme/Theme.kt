package com.music.bitchord.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.music.bitchord.R

// Apple Music's signature red. No longer the primary accent, but kept for the
// spots (Replay's rank badge) that want that specific red regardless of theme.
val AccentRed = Color(0xFFFA2D48)

private val DarkColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFFB8B8BE),
    outline = Color(0xFF55555B),
    surfaceTint = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    tertiary = Color(0xFFE2E2E7),
    onTertiary = Color.Black,
)

private val LightColors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F9),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E5EA),
)

/**
 * SF Pro Display, the face Apple Music itself is set in. Only the weights the
 * type scale actually asks for are bundled; Compose synthesises nothing, so a
 * missing weight would silently fall back to the nearest one shipped.
 */
val SFProDisplay = FontFamily(
    Font(R.font.sf_pro_display_regular, FontWeight.W400),
    Font(R.font.sf_pro_display_medium, FontWeight.W500),
    Font(R.font.sf_pro_display_semibold, FontWeight.W600),
    Font(R.font.sf_pro_display_bold, FontWeight.W700),
    Font(R.font.sf_pro_display_heavy, FontWeight.W800),
)

// Heavy, tight typography — the backbone of the Apple Music look.
private val BitChordTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 34.sp, letterSpacing = (-0.8).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 30.sp, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 22.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 16.sp, letterSpacing = (-0.2).sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.W600, fontSize = 11.sp),
).withFamily(SFProDisplay)

/** Applies [family] to every style in the scale, so nothing is left on Roboto. */
private fun Typography.withFamily(family: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = family),
    displayMedium = displayMedium.copy(fontFamily = family),
    displaySmall = displaySmall.copy(fontFamily = family),
    headlineLarge = headlineLarge.copy(fontFamily = family),
    headlineMedium = headlineMedium.copy(fontFamily = family),
    headlineSmall = headlineSmall.copy(fontFamily = family),
    titleLarge = titleLarge.copy(fontFamily = family),
    titleMedium = titleMedium.copy(fontFamily = family),
    titleSmall = titleSmall.copy(fontFamily = family),
    bodyLarge = bodyLarge.copy(fontFamily = family),
    bodyMedium = bodyMedium.copy(fontFamily = family),
    bodySmall = bodySmall.copy(fontFamily = family),
    labelLarge = labelLarge.copy(fontFamily = family),
    labelMedium = labelMedium.copy(fontFamily = family),
    labelSmall = labelSmall.copy(fontFamily = family),
)

/** One palette owns pages, elevated containers, menus and dialogs. Dark is always OLED black. */
internal fun expressiveColorScheme(dark: Boolean, accent: Color): androidx.compose.material3.ColorScheme {
    // Keep accent text legible even when the user chooses pure white or black.
    val primary = if (dark) lerp(accent, Color.White, 0.70f)
        else if (accent.luminance() > 0.12f) lerp(accent, Color.Black, 0.60f) else accent
    val onPrimary = if (primary.luminance() > 0.179f) Color.Black else Color.White
    val container = lerp(accent, if (dark) Color.Black else Color.White, if (dark) 0.70f else 0.87f)
    val onContainer = if (container.luminance() > 0.179f) Color.Black else Color.White
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary, onPrimary = onPrimary, primaryContainer = container, onPrimaryContainer = onContainer,
        secondary = primary, onSecondary = onPrimary, secondaryContainer = container, onSecondaryContainer = onContainer,
        tertiary = primary, onTertiary = onPrimary, tertiaryContainer = container, onTertiaryContainer = onContainer,
        background = if (dark) Color.Black else Color(0xFFF8F9FC),
        onBackground = if (dark) Color(0xFFF3F3F3) else Color(0xFF191C20),
        surface = if (dark) Color(0xFF0A0A0A) else Color(0xFFF8F9FC),
        onSurface = if (dark) Color(0xFFF3F3F3) else Color(0xFF191C20),
        surfaceVariant = if (dark) Color(0xFF202020) else Color(0xFFE3E5EB),
        onSurfaceVariant = if (dark) Color(0xFFC5C5C5) else Color(0xFF43474E),
        surfaceTint = Color.Transparent,
        surfaceContainerLowest = if (dark) Color.Black else Color.White,
        surfaceContainerLow = if (dark) Color(0xFF080808) else Color(0xFFF3F5FA),
        surfaceContainer = if (dark) Color(0xFF101010) else Color(0xFFEDF0F6),
        surfaceContainerHigh = if (dark) Color(0xFF181818) else Color(0xFFE7EAF0),
        surfaceContainerHighest = if (dark) Color(0xFF202020) else Color(0xFFE1E4EA),
        surfaceBright = if (dark) Color(0xFF202020) else Color(0xFFF8F9FC),
        surfaceDim = if (dark) Color.Black else Color(0xFFD9DCE2),
        outline = if (dark) Color(0xFF707070) else Color(0xFF74777F),
        outlineVariant = if (dark) Color(0xFF303030) else Color(0xFFC4C7CF),
    )
}

@Composable
fun BitChordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    expressive: Boolean = false,
    accent: Color = Color(0xFF0B57D0),
    content: @Composable () -> Unit,
) {
    val colors = if (expressive) expressiveColorScheme(darkTheme, accent) else {
        (if (darkTheme) DarkColors else LightColors).let { scheme ->
            if (darkTheme) scheme.copy(background = Color.Black, surfaceTint = Color.Transparent,
                surfaceContainerLowest = Color.Black, surfaceContainerLow = Color(0xFF080808),
                surfaceContainer = Color(0xFF101010), surfaceContainerHigh = Color(0xFF181818),
                surfaceContainerHighest = Color(0xFF202020), surfaceBright = Color(0xFF202020), surfaceDim = Color.Black)
            else scheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = if (expressive) Typography() else BitChordTypography,
        shapes = if (expressive) Shapes(
            extraSmall = RoundedCornerShape(12.dp), small = RoundedCornerShape(16.dp),
            medium = RoundedCornerShape(24.dp), large = RoundedCornerShape(28.dp), extraLarge = RoundedCornerShape(36.dp),
        ) else Shapes(),
        content = {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalGoogleExpressive provides expressive,
                androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onBackground,
            ) { content() }
        },
    )
}

/**
 * Draws the status and navigation bar glyphs dark or light.
 *
 * `enableEdgeToEdge()` decides this from the *system* dark-mode setting, which
 * is the wrong input the moment the in-app theme disagrees with it: Light theme
 * on a phone in dark mode left white icons on a white bar, invisible. The bars
 * have to follow the content the app is actually painting. The player supplies
 * its own stable light-icon value and paints contrast behind it; every other
 * surface follows the theme. Hence a parameter rather than reading it here.
 */
@Composable
fun SystemBarIcons(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = findWindow(view) ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = dark
            isAppearanceLightNavigationBars = dark
        }
    }
}

