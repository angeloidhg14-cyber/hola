package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track
import kotlinx.coroutines.delay

@Composable
fun KaraokeLyricsView(
    track: Track,
    currentPositionMs: Long,
    isVocalCutEnabled: Boolean,
    onToggleVocalCut: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val lyrics = track.lyrics
    val listState = rememberLazyListState()

    // Find the currently active lyric line index based on playback time
    val activeIndex = remember(currentPositionMs, lyrics) {
        val index = lyrics.indexOfLast { it.timeMs <= currentPositionMs }
        if (index >= 0) index else 0
    }

    // Auto-scroll so current lyric stays comfortably visible
    LaunchedEffect(activeIndex) {
        if (lyrics.isNotEmpty() && activeIndex >= 0) {
            val target = (activeIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(target)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("karaoke_lyrics_view")
    ) {
        // Karaoke Controls Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.MicExternalOn,
                        contentDescription = "Micrófono Karaoke",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Letras en Vivo • Modo Karaoke",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = if (isVocalCutEnabled) "Atenuación vocal activa para cantar" else "Toca cualquier línea para saltar en la canción",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            fontSize = 11.sp
                        )
                    }
                }

                // Vocal Cut Toggle for singing along
                FilterChip(
                    selected = isVocalCutEnabled,
                    onClick = onToggleVocalCut,
                    label = {
                        Text(
                            text = if (isVocalCutEnabled) "Cantar ON" else "Cantar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("karaoke_vocal_cut_toggle")
                )
            }
        }

        if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Lyrics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Buscando letras sincronizadas en la nube...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex

                    val textColor by animateColorAsState(
                        targetValue = when {
                            isActive -> MaterialTheme.colorScheme.primary
                            isPast -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        },
                        label = "lyric_color"
                    )

                    val fontSize = if (isActive) 22.sp else 16.sp
                    val fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSeekTo(line.timeMs) }
                            .then(
                                if (isActive) {
                                    Modifier
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    Color.Transparent
                                                )
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                            RoundedCornerShape(14.dp)
                                        )
                                } else Modifier
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column {
                                Text(
                                    text = line.text,
                                    fontSize = fontSize,
                                    fontWeight = fontWeight,
                                    color = textColor,
                                    lineHeight = (fontSize.value * 1.3f).sp
                                )

                                if (line.translation != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = line.translation,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
