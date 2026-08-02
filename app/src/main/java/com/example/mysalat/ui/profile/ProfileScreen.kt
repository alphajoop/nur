package com.example.mysalat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysalat.data.StatsSummary
import com.example.mysalat.ui.components.ButtonVariant
import com.example.mysalat.ui.components.DialogActions
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.components.ModernButton
import com.example.mysalat.ui.components.ModernDialog
import com.example.mysalat.ui.components.ModernTextField
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.placeholder.ComingSoonScreen
import com.example.mysalat.ui.theme.MySalatTheme
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.Spacing

/**
 * Profile tab. The only working setting today is the first name used by the
 * home greeting; everything else is announced as upcoming.
 */
@Composable
fun ProfileScreen(
    userName: String,
    summary: StatsSummary,
    onUserNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var editing by remember { mutableStateOf(false) }

    ComingSoonScreen(
        title = "Profil",
        subtitle = "Votre parcours, vos réglages, votre rythme.",
        icon = AppIcons.Profile,
        highlights = listOf(
            "Rappels d'adhan personnalisés",
            "Objectifs hebdomadaires",
            "Sauvegarde de votre progression"
        ),
        modifier = modifier,
        contentPadding = contentPadding,
        footer = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                IdentityCard(
                    userName = userName,
                    onEdit = { editing = true }
                )
                SummaryStrip(summary = summary)
            }
        }
    )

    if (editing) {
        NameDialog(
            currentName = userName,
            onDismiss = { editing = false },
            onConfirm = { newName ->
                onUserNameChanged(newName)
                editing = false
            }
        )
    }
}

@Composable
private fun IdentityCard(
    userName: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(icon = AppIcons.Profile, size = 48.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Prénom affiché sur l'accueil",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ModernButton(
                text = "Modifier mon prénom",
                onClick = onEdit,
                variant = ButtonVariant.Secondary,
                icon = AppIcons.Settings,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryStrip(summary: StatsSummary, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatTile(
                icon = AppIcons.Flame,
                value = summary.currentStreak.toString(),
                label = "série"
            )
            StatTile(
                icon = AppIcons.CheckAll,
                value = summary.completeDays.toString(),
                label = "jours pleins"
            )
            StatTile(
                icon = AppIcons.History,
                value = "${summary.prayerCompletionPercent} %",
                label = "assiduité"
            )
        }
    }
}

@Composable
private fun StatTile(
    icon: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        IconBadge(icon = icon, size = 36.dp, iconSize = IconSize.md, cornerRadius = Radius.sm)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var draft by remember { mutableStateOf(currentName) }

    ModernDialog(
        title = "Votre prénom",
        subtitle = "Il sera utilisé pour vous saluer sur l'accueil.",
        onDismiss = onDismiss
    ) {
        ModernTextField(
            value = draft,
            onValueChange = { draft = it },
            placeholder = "Ahmed",
            onImeDone = { onConfirm(draft) }
        )
        DialogActions(
            confirmText = "Enregistrer",
            onConfirm = { onConfirm(draft) },
            dismissText = "Annuler",
            onDismiss = onDismiss
        )
    }
}

@Preview(name = "Profil clair", heightDp = 900)
@Composable
private fun ProfileLightPreview() {
    MySalatTheme(darkTheme = false) {
        ProfileScreen(
            userName = "Ahmed",
            summary = StatsSummary(
                completeDays = 12,
                prayerCompletionPercent = 74,
                currentStreak = 5,
                trackedDays = 21
            ),
            onUserNameChanged = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}

@Preview(name = "Profil sombre", heightDp = 900)
@Composable
private fun ProfileDarkPreview() {
    MySalatTheme(darkTheme = true) {
        ProfileScreen(
            userName = "Yacine",
            summary = StatsSummary(
                completeDays = 3,
                prayerCompletionPercent = 41,
                currentStreak = 1,
                trackedDays = 9
            ),
            onUserNameChanged = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}
