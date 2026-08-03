package com.example.mysalat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysalat.BuildConfig
import com.example.mysalat.data.City
import com.example.mysalat.data.CityCatalog
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
import com.example.mysalat.ui.theme.brand

/**
 * Profile tab: editable first name, city for prayer times, and upcoming features.
 */
@Composable
fun ProfileScreen(
    userName: String,
    city: City,
    summary: StatsSummary,
    onUserNameChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var editingName by remember { mutableStateOf(false) }
    var editingCity by remember { mutableStateOf(false) }

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
                    onEdit = { editingName = true }
                )
                CityCard(
                    city = city,
                    onEdit = { editingCity = true }
                )
                SummaryStrip(summary = summary)
                Text(
                    text = "Nur · ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm)
                )
            }
        }
    )

    if (editingName) {
        NameDialog(
            currentName = userName,
            onDismiss = { editingName = false },
            onConfirm = { newName ->
                onUserNameChanged(newName)
                editingName = false
            }
        )
    }

    if (editingCity) {
        CityDialog(
            currentCityId = city.id,
            onDismiss = { editingCity = false },
            onConfirm = { cityId ->
                onCityChanged(cityId)
                editingCity = false
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
private fun CityCard(
    city: City,
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
                IconBadge(icon = AppIcons.Mosque, size = 48.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Ville pour les horaires",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = city.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            ModernButton(
                text = "Changer de ville",
                onClick = onEdit,
                variant = ButtonVariant.Secondary,
                icon = AppIcons.Hijri,
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

@Composable
private fun CityDialog(
    currentCityId: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedId by remember { mutableStateOf(currentCityId) }
    val haptic = LocalHapticFeedback.current
    val brand = MaterialTheme.brand

    ModernDialog(
        title = "Choisir une ville",
        subtitle = "Les horaires de prière seront calculés pour cette ville.",
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
        ) {
            CityCatalog.cities.forEach { city ->
                val selected = city.id == selectedId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.RadioButton) {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            selectedId = city.id
                        }
                        .background(
                            if (selected) brand.greenWash
                            else MaterialTheme.colorScheme.surface
                        )
                        .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = city.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    if (selected) {
                        IconBadge(
                            icon = AppIcons.Check,
                            size = 28.dp,
                            iconSize = IconSize.sm,
                            cornerRadius = Radius.sm
                        )
                    }
                }
            }
        }
        DialogActions(
            confirmText = "Enregistrer",
            onConfirm = { onConfirm(selectedId) },
            dismissText = "Annuler",
            onDismiss = onDismiss
        )
    }
}

@Preview(name = "Profil clair", heightDp = 1100)
@Composable
private fun ProfileLightPreview() {
    MySalatTheme(darkTheme = false) {
        ProfileScreen(
            userName = "Alpha",
            city = CityCatalog.default,
            summary = StatsSummary(
                completeDays = 12,
                prayerCompletionPercent = 74,
                currentStreak = 5,
                trackedDays = 21
            ),
            onUserNameChanged = {},
            onCityChanged = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}

@Preview(name = "Profil sombre", heightDp = 1100)
@Composable
private fun ProfileDarkPreview() {
    MySalatTheme(darkTheme = true) {
        ProfileScreen(
            userName = "Yacine",
            city = CityCatalog.byId("paris"),
            summary = StatsSummary(
                completeDays = 3,
                prayerCompletionPercent = 41,
                currentStreak = 1,
                trackedDays = 9
            ),
            onUserNameChanged = {},
            onCityChanged = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}
