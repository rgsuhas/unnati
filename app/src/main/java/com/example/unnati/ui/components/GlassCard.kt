package com.example.unnati.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.unnati.ui.theme.LocalAppPalette

// Glass card: high-alpha surface on the page background approximates frosted glass.
// Light mode = white/92%; dark mode = dark purple/85% — both come from the palette.
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalAppPalette.current
    val cardColors = CardDefaults.cardColors(
        containerColor = palette.glassCardBg,
    )
    val border = BorderStroke(1.5.dp, palette.glassBorder)

    if (onClick != null) {
        Card(
            onClick    = onClick,
            modifier   = modifier,
            shape      = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors     = cardColors,
            border     = border,
            elevation  = CardDefaults.cardElevation(defaultElevation = elevation),
            content    = content,
        )
    } else {
        Card(
            modifier  = modifier,
            shape     = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors    = cardColors,
            border    = border,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            content   = content,
        )
    }
}

// Tinted glass card — uses palette primary at low alpha (for hero sections)
@Composable
fun TintedGlassCard(
    modifier: Modifier = Modifier,
    tintAlpha: Float = 0.08f,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalAppPalette.current
    Card(
        modifier  = modifier,
        shape     = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = palette.primary.copy(alpha = tintAlpha)),
        border    = BorderStroke(1.5.dp, palette.primary.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content   = content,
    )
}
