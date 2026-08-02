package com.example.mysalat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mysalat.HomeUiState
import com.example.mysalat.ui.theme.ArabicGreetingStyle
import com.example.mysalat.ui.theme.Spacing
import com.example.mysalat.ui.theme.brand

/**
 * Top of the home screen: Arabic salaam, personalised greeting, today's date,
 * and a tappable avatar leading to the profile tab.
 */
@Composable
fun GreetingHeader(
    state: HomeUiState,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brand = MaterialTheme.brand

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = "السلام عليكم",
                style = ArabicGreetingStyle,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${state.dayPart.greeting} ${state.userName}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = state.formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(brand.accentBrush)
                .clickable(role = Role.Button, onClick = onAvatarClick)
                .semantics { contentDescription = "Ouvrir le profil" },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.userName.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
