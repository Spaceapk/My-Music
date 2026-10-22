package com.music.bitchord.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.*
import org.junit.Test

class ExpressiveColorSchemeTest {
    @Test fun darkBackgroundStaysBlackForEveryAccent() {
        listOf(Color.Red, Color.Blue, Color.Green, Color.Black, Color.White).forEach { accent ->
            val colors = expressiveColorScheme(true, accent)
            assertEquals(Color.Black, colors.background)
            assertEquals(Color.Black, colors.surfaceContainerLowest)
            assertTrue(colors.surfaceContainerHigh.luminance() < 0.02f)
            assertTrue(colors.onSurface.luminance() > 0.8f)
        }
    }
    @Test fun accentButtonsKeepReadableLabelsInBothModes() {
        listOf(false, true).forEach { dark ->
            listOf(Color.Red, Color.Blue, Color.Green, Color.Black, Color.White).forEach { accent ->
                val c = expressiveColorScheme(dark, accent)
                fun contrast(a: Color, b: Color): Float {
                    val x = a.luminance(); val y = b.luminance()
                    return (maxOf(x, y) + 0.05f) / (minOf(x, y) + 0.05f)
                }
                assertTrue(contrast(c.primary, c.onPrimary) >= 4.5f)
                assertTrue(contrast(c.primaryContainer, c.onPrimaryContainer) >= 4.5f)
            }
        }
    }
}
