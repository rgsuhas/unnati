package com.example.unnati.ui.theme

import androidx.compose.ui.graphics.Color

// ── Palette definitions ────────────────────────────────────────────────────
// To switch palettes: change ActivePalette in Theme.kt (one line).
// Each palette has a light and dark variant stored together in PalettePair.

data class AppColorPalette(
    // Brand
    val primary: Color,           // icon/text tint; light lavender in dark mode
    val primaryContainer: Color,  // button container background
    val onPrimary: Color = Color.White,
    val onPrimaryContainer: Color,
    val secondaryContainer: Color,   // amber/saffron accent
    val onSecondaryContainer: Color,
    // Surfaces
    val bodyBackground: Color,   // page/scaffold background
    val surface: Color,          // card base colour
    val surfaceContainer: Color, // elevated section bg
    val onSurface: Color,        // primary text
    val onSurfaceVariant: Color, // secondary text / icons
    val outline: Color,          // borders / dividers
    // Dark-mode-only stored fields
    val backgroundDark: Color,
    val surfaceDark: Color,
    // Glass token — adapts per variant without Compose context
    val glassCardBg: Color,
    val glassBorder: Color,
    val glassTopBarBg: Color,
    val glassBottomNavBg: Color,
) {
    // Backward-compat aliases
    val primaryDark: Color get() = primaryContainer
    val accent: Color get() = secondaryContainer
    val accentLight: Color get() = onSecondaryContainer
    val backgroundLight: Color get() = bodyBackground
    val surfaceLight: Color get() = surface
    // Button helpers — use these in all screens for correct dark/light contrast
    val buttonContainer: Color get() = primaryContainer
    val buttonContent: Color get() = onPrimaryContainer
}

// ── Light glass tokens ─────────────────────────────────────────────────────
// Card bg is near-white; border uses a faint shadow so cards visibly lift off the lavender page.
private val LightGlassCardBg     = Color.White.copy(alpha = 0.96f)
private val LightGlassBorder     = Color(0xFF1A1B23).copy(alpha = 0.07f)  // subtle dark outline
private val LightGlassTopBarBg   = Color.White.copy(alpha = 0.96f)
private val LightGlassBottomNav  = Color.White.copy(alpha = 0.96f)

// ── Dark glass tokens (from HTML: rgba(44,40,51,0.4) surface-container-high) ──
private val DarkGlassCardBg      = Color(0xFF2C2833).copy(alpha = 0.85f)
private val DarkGlassBorder      = Color(0xFFD2BBFF).copy(alpha = 0.12f)  // primary-fixed-dim at 12%
private val DarkGlassTopBarBg    = Color(0xFF1D1A24).copy(alpha = 0.92f)  // surface-container-low
private val DarkGlassBottomNav   = Color(0xFF1D1A24).copy(alpha = 0.92f)

object Palettes {
    // ── Violet & Saffron ──────────────────────────────────────────────────
    val VioletSaffron = AppColorPalette(
        primary                = Color(0xFF630ED4),
        primaryContainer       = Color(0xFF630ED4), // = primary in light (button bg)
        onPrimaryContainer     = Color.White,
        secondaryContainer     = Color(0xFFFEA619),
        onSecondaryContainer   = Color(0xFF684000),
        bodyBackground         = Color(0xFFF5F3FF),
        surface                = Color(0xFFFBF8FF),
        surfaceContainer       = Color(0xFFEEECF8),
        onSurface              = Color(0xFF0F0E17),
        onSurfaceVariant       = Color(0xFF3D3553),
        outline                = Color(0xFF6B6476),
        backgroundDark         = Color(0xFF141218),
        surfaceDark            = Color(0xFF1C1B20),
        glassCardBg            = LightGlassCardBg,
        glassBorder            = LightGlassBorder,
        glassTopBarBg          = LightGlassTopBarBg,
        glassBottomNavBg       = LightGlassBottomNav,
    )

    val VioletSaffronDark = AppColorPalette(
        primary                = Color(0xFFD2BBFF), // light lavender — for text/icons in dark
        primaryContainer       = Color(0xFF7C3AED), // purple — button bg in dark
        onPrimaryContainer     = Color(0xFFEDE0FF),
        secondaryContainer     = Color(0xFFEE9800),
        onSecondaryContainer   = Color(0xFF5B3800),
        bodyBackground         = Color(0xFF15121B),
        surface                = Color(0xFF1D1A24),
        surfaceContainer       = Color(0xFF221E28),
        onSurface              = Color(0xFFE8DFEE),
        onSurfaceVariant       = Color(0xFFCCC3D8),
        outline                = Color(0xFF958DA1),
        backgroundDark         = Color(0xFF15121B),
        surfaceDark            = Color(0xFF1D1A24),
        glassCardBg            = DarkGlassCardBg,
        glassBorder            = DarkGlassBorder,
        glassTopBarBg          = DarkGlassTopBarBg,
        glassBottomNavBg       = DarkGlassBottomNav,
    )

    // ── Forest & Gold ─────────────────────────────────────────────────────
    val ForestGold = AppColorPalette(
        primary                = Color(0xFF166534),
        primaryContainer       = Color(0xFF166534),
        onPrimaryContainer     = Color.White,
        secondaryContainer     = Color(0xFFCA8A04),
        onSecondaryContainer   = Color(0xFF422006),
        bodyBackground         = Color(0xFFF0FDF4),
        surface                = Color(0xFFF7FEF2),
        surfaceContainer       = Color(0xFFDCFCE7),
        onSurface              = Color(0xFF0A1510),
        onSurfaceVariant       = Color(0xFF2E5040),
        outline                = Color(0xFF5A6E62),
        backgroundDark         = Color(0xFF0A120D),
        surfaceDark            = Color(0xFF141E18),
        glassCardBg            = LightGlassCardBg,
        glassBorder            = LightGlassBorder,
        glassTopBarBg          = LightGlassTopBarBg,
        glassBottomNavBg       = LightGlassBottomNav,
    )

    val ForestGoldDark = AppColorPalette(
        primary                = Color(0xFF6EE7B7),
        primaryContainer       = Color(0xFF14532D),
        onPrimaryContainer     = Color(0xFFCCFBE1),
        secondaryContainer     = Color(0xFFB45309),
        onSecondaryContainer   = Color(0xFFFEF3C7),
        bodyBackground         = Color(0xFF0A120D),
        surface                = Color(0xFF141E18),
        surfaceContainer       = Color(0xFF1A2B20),
        onSurface              = Color(0xFFCCE8D5),
        onSurfaceVariant       = Color(0xFF9AB5A2),
        outline                = Color(0xFF5E7867),
        backgroundDark         = Color(0xFF0A120D),
        surfaceDark            = Color(0xFF141E18),
        glassCardBg            = Color(0xFF1A2B20).copy(alpha = 0.85f),
        glassBorder            = Color(0xFF6EE7B7).copy(alpha = 0.12f),
        glassTopBarBg          = Color(0xFF141E18).copy(alpha = 0.92f),
        glassBottomNavBg       = Color(0xFF141E18).copy(alpha = 0.92f),
    )

    // ── Ocean & Coral ─────────────────────────────────────────────────────
    val OceanCoral = AppColorPalette(
        primary                = Color(0xFF1D4ED8),
        primaryContainer       = Color(0xFF1D4ED8),
        onPrimaryContainer     = Color.White,
        secondaryContainer     = Color(0xFFEF4444),
        onSecondaryContainer   = Color(0xFF7F1D1D),
        bodyBackground         = Color(0xFFEFF6FF),
        surface                = Color(0xFFF8FAFF),
        surfaceContainer       = Color(0xFFDFEAFF),
        onSurface              = Color(0xFF0E0F1C),
        onSurfaceVariant       = Color(0xFF36394F),
        outline                = Color(0xFF5E616F),
        backgroundDark         = Color(0xFF0A0F1E),
        surfaceDark            = Color(0xFF1A1B27),
        glassCardBg            = LightGlassCardBg,
        glassBorder            = LightGlassBorder,
        glassTopBarBg          = LightGlassTopBarBg,
        glassBottomNavBg       = LightGlassBottomNav,
    )

    val OceanCoralDark = AppColorPalette(
        primary                = Color(0xFF93C5FD),
        primaryContainer       = Color(0xFF1E3A8A),
        onPrimaryContainer     = Color(0xFFDBEAFE),
        secondaryContainer     = Color(0xFFB91C1C),
        onSecondaryContainer   = Color(0xFFFEE2E2),
        bodyBackground         = Color(0xFF0A0F1E),
        surface                = Color(0xFF141827),
        surfaceContainer       = Color(0xFF1C2135),
        onSurface              = Color(0xFFCFD6F0),
        onSurfaceVariant       = Color(0xFF8E96B5),
        outline                = Color(0xFF5C6380),
        backgroundDark         = Color(0xFF0A0F1E),
        surfaceDark            = Color(0xFF141827),
        glassCardBg            = Color(0xFF1C2135).copy(alpha = 0.85f),
        glassBorder            = Color(0xFF93C5FD).copy(alpha = 0.12f),
        glassTopBarBg          = Color(0xFF141827).copy(alpha = 0.92f),
        glassBottomNavBg       = Color(0xFF141827).copy(alpha = 0.92f),
    )

    val all = mapOf(
        "Violet & Saffron" to Pair(VioletSaffron, VioletSaffronDark),
        "Forest & Gold"    to Pair(ForestGold,    ForestGoldDark),
        "Ocean & Coral"    to Pair(OceanCoral,    OceanCoralDark),
    )
}

// Semantic colors — palette-independent
val SuccessGreen = Color(0xFF16A34A)
val ErrorRed     = Color(0xFFBA1A1A)
val WarningAmber = Color(0xFFD97706)
