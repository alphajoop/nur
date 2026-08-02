package com.example.mysalat.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mysalat.data.Verse
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.icons.AppIcon
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.ArabicVerseStyle
import com.example.mysalat.ui.theme.Spacing
import com.example.mysalat.ui.theme.brand

/**
 * Verse of the day: Arabic line, French translation, reference, and a share
 * action handing the text to the system share sheet.
 */
@Composable
fun VerseCard(
    verse: Verse,
    modifier: Modifier = Modifier
) {
    val brand = MaterialTheme.brand
    val context = LocalContext.current

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBadge(
                        icon = AppIcons.Quote,
                        containerColor = brand.goldWash,
                        tint = brand.gold,
                        size = 36.dp,
                        iconSize = IconSize.md
                    )
                    Text(
                        text = "Verset du jour",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AppIcon(
                    icon = AppIcons.Share,
                    contentDescription = "Partager le verset",
                    size = IconSize.md,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(role = Role.Button) {
                            val body = buildString {
                                appendLine(verse.arabic)
                                appendLine()
                                appendLine(verse.translation)
                                append("— ${verse.reference}")
                            }
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, body)
                            }
                            context.startActivity(
                                Intent.createChooser(send, "Partager le verset")
                            )
                        }
                        .padding(Spacing.xxs)
                )
            }

            Text(
                text = verse.arabic,
                style = ArabicVerseStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = verse.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = verse.reference,
                style = MaterialTheme.typography.labelMedium,
                color = brand.gold
            )
        }
    }
}
