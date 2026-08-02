package com.example.mysalat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Motion

/**
 * Circular check indicator that fills with brand green and draws its tick with
 * a bouncy spring. Purely visual — the parent row owns the click and haptics.
 */
@Composable
fun ModernCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val colorScheme = MaterialTheme.colorScheme

    val fill by animateColorAsState(
        targetValue = if (checked) colorScheme.primary else Color.Transparent,
        animationSpec = Motion.springSoft(),
        label = "checkboxFill"
    )
    val ring by animateColorAsState(
        targetValue = if (checked) colorScheme.primary else colorScheme.outline,
        animationSpec = Motion.springSoft(),
        label = "checkboxRing"
    )
    val pop by animateFloatAsState(
        targetValue = if (checked) 1f else 0.9f,
        animationSpec = Motion.springBouncy(),
        label = "checkboxPop"
    )
    val tickProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = Motion.springBouncy(),
        label = "checkboxTick"
    )

    val tickColor = colorScheme.onPrimary

    Box(
        modifier = modifier
            .size(size)
            .scale(pop)
            .clip(CircleShape)
            .background(fill)
            .border(
                width = if (checked) Elevation.hairline else 2.dp,
                color = ring,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.5f)) {
            if (tickProgress <= 0f) return@Canvas

            val w = this.size.width
            val h = this.size.height
            val start = Offset(0f, h * 0.55f)
            val mid = Offset(w * 0.38f, h * 0.92f)
            val end = Offset(w, h * 0.1f)

            // First leg draws, then the long leg, so the tick feels "written".
            val firstLegProgress = (tickProgress / 0.4f).coerceIn(0f, 1f)
            val secondLegProgress = ((tickProgress - 0.4f) / 0.6f).coerceIn(0f, 1f)

            val path = Path().apply {
                moveTo(start.x, start.y)
                lineTo(
                    start.x + (mid.x - start.x) * firstLegProgress,
                    start.y + (mid.y - start.y) * firstLegProgress
                )
                if (secondLegProgress > 0f) {
                    lineTo(
                        mid.x + (end.x - mid.x) * secondLegProgress,
                        mid.y + (end.y - mid.y) * secondLegProgress
                    )
                }
            }

            drawPath(
                path = path,
                color = tickColor,
                style = Stroke(width = w * 0.22f, cap = StrokeCap.Round)
            )
        }
    }
}
