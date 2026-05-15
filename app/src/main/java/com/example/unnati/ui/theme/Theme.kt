package com.example.unnati.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Active palette — change this one line to switch the whole app palette ──
val ActivePalette = Palettes.VioletSaffron

val LocalAppPalette = compositionLocalOf { ActivePalette }

private fun buildLightScheme(p: AppColorPalette) = lightColorScheme(
    primary                = p.primary,
    onPrimary              = p.onPrimary,
    primaryContainer       = p.primaryContainer,
    onPrimaryContainer     = p.onPrimaryContainer,
    secondary              = p.onSecondaryContainer,
    onSecondary            = Color.White,
    secondaryContainer     = p.secondaryContainer,
    onSecondaryContainer   = p.onSecondaryContainer,
    background             = p.bodyBackground,
    onBackground           = p.onSurface,
    surface                = p.surface,
    onSurface              = p.onSurface,
    surfaceVariant         = p.surfaceContainer,
    onSurfaceVariant       = p.onSurfaceVariant,
    outline                = p.outline,
    outlineVariant         = p.surfaceContainer,
    error                  = ErrorRed,
    onError                = Color.White,
    errorContainer         = Color(0xFFFFDAD6),
    onErrorContainer       = Color(0xFF93000A),
)

private fun buildDarkScheme(p: AppColorPalette) = darkColorScheme(
    primary                = p.primary,
    onPrimary              = Color(0xFF3B0099),
    primaryContainer       = p.primaryContainer,
    onPrimaryContainer     = p.onPrimaryContainer,
    secondary              = p.secondaryContainer,
    onSecondary            = p.onSecondaryContainer,
    secondaryContainer     = p.secondaryContainer,
    onSecondaryContainer   = p.onSecondaryContainer,
    background             = p.bodyBackground,
    onBackground           = p.onSurface,
    surface                = p.surface,
    onSurface              = p.onSurface,
    surfaceVariant         = p.surfaceContainer,
    onSurfaceVariant       = p.onSurfaceVariant,
    outline                = p.outline,
    outlineVariant         = p.surfaceContainer,
    error                  = Color(0xFFFFB4AB),
    onError                = Color(0xFF690005),
    errorContainer         = Color(0xFF93000A),
    onErrorContainer       = Color(0xFFFFDAD6),
)

@Composable
fun UnnatiTheme(
    palette: AppColorPalette = ActivePalette,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val effectivePalette = if (darkTheme) {
        Palettes.all.values.firstOrNull { it.first == palette }?.second ?: Palettes.VioletSaffronDark
    } else {
        palette
    }
    val colorScheme = if (darkTheme) buildDarkScheme(effectivePalette) else buildLightScheme(effectivePalette)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppPalette provides effectivePalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}
