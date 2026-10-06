package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.audio.RepeatMode as AudioRepeatMode
import com.example.model.Track
import com.example.ui.theme.HiResGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPlayer(
    playbackState: PlaybackState,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenKaraoke: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onOpenSleepTimer: () -> Unit,
    onCutRingtone: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    var isVinylMode by remember { mutableStateOf(true) }

    // Vinyl rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_rotation"
    )

    // Current lyric preview
    val currentLyric = remember(playbackState.currentPositionMs, track.lyrics) {
        track.lyrics.findLast { it.timeMs <= playbackState.currentPositionMs }?.text
            ?: "Toca el icono de micrófono para ver la letra sincronizada"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("full_screen_player"),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("player_collapse_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Minimizar reproductor",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "REPRODUCIENDO AHORA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = track.album,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(
                        onClick = onOpenSleepTimer,
                        modifier = Modifier.testTag("player_sleep_timer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = "Temporizador de apagado",
                            tint = if (playbackState.sleepTimerMinutesLeft != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Vinyl vs Art Switcher Toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isVinylMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.clickable { isVinylMode = true }
                ) {
                    Text(
                        text = "Vinilo Lark",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVinylMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (!isVinylMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.clickable { isVinylMode = false }
                ) {
                    Text(
                        text = "Carátula HD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isVinylMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Center Artwork / Vinyl Area
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = if (isVinylMode) CircleShape else RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isVinylMode) {
                    // Vinyl Outer Rim Grooves
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF0F1115))
                            .border(6.dp, Color(0xFF1E222B), CircleShape)
                            .border(12.dp, Color(0xFF14171E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Rotating Label
                        Image(
                            painter = painterResource(id = track.coverDrawableRes),
                            contentDescription = track.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .then(if (playbackState.isPlaying) Modifier.rotate(discRotation) else Modifier)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )

                        // Center Spindle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                } else {
                    // Full Square Album Art
                    Image(
                        painter = painterResource(id = track.coverDrawableRes),
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(28.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Waveform Visualizer simulation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                playbackState.waveformFrequencies.forEach { amp ->
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height((26.dp * amp).coerceAtLeast(3.dp))
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hi-Res Format & Smart Tags Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (track.format.isHiRes) HiResGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (track.format.isHiRes) HiResGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = if (track.format.isHiRes) HiResGold else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${track.format.label} • ${track.format.sampleRate}",
                            color = if (track.format.isHiRes) HiResGold else MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = track.smartTags.firstOrNull() ?: "#Audiofilo",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Track Title, Artist & Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { onToggleFavorite(track.id) },
                    modifier = Modifier.testTag("player_favorite_toggle")
                ) {
                    Icon(
                        imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (track.isFavorite) Color(0xFFFF2A6D) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-time Lyrics Preview Banner (Clickable to open Karaoke)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenKaraoke),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = "Karaoke",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = currentLyric,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Karaoke >",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Slider & Timestamps
            val progressFraction = if (playbackState.durationMs > 0) {
                (playbackState.currentPositionMs.toFloat() / playbackState.durationMs).coerceIn(0f, 1f)
            } else 0f

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progressFraction,
                    onValueChange = { frac ->
                        onSeekTo((frac * playbackState.durationMs).toLong())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_seek_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(playbackState.currentPositionMs),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = formatTime(playbackState.durationMs),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Shuffle Toggle
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("player_shuffle_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Aleatorio",
                        tint = if (playbackState.isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Previous
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("player_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Anterior",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Play / Pause Glowing Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(16.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .clickable(onClick = onTogglePlayPause)
                        .testTag("player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pausar" else "Reproducir",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Siguiente",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Repeat Mode Toggle
                IconButton(
                    onClick = onCycleRepeat,
                    modifier = Modifier.testTag("player_repeat_toggle")
                ) {
                    Icon(
                        imageVector = when (playbackState.repeatMode) {
                            AudioRepeatMode.ONE -> Icons.Rounded.RepeatOne
                            AudioRepeatMode.ALL -> Icons.Rounded.Repeat
                            AudioRepeatMode.OFF -> Icons.Rounded.Repeat
                        },
                        contentDescription = "Repetir",
                        tint = when (playbackState.repeatMode) {
                            AudioRepeatMode.OFF -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Quick Actions: Karaoke, Equalizer, Speed, Ringtone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Karaoke
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(onClick = onOpenKaraoke)
                        .testTag("btn_karaoke_modal")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MicExternalOn,
                        contentDescription = "Karaoke",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Karaoke", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                // Equalizer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(onClick = onOpenEqualizer)
                        .testTag("btn_equalizer_modal")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Ecualizador",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "DSP EQ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                // Speed
                var showSpeedMenu by remember { mutableStateOf(false) }
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { showSpeedMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Speed,
                            contentDescription = "Velocidad",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${playbackState.playbackSpeed}x", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                            DropdownMenuItem(
                                text = { Text("${spd}x") },
                                onClick = {
                                    onSetPlaybackSpeed(spd)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }

                // Ringtone Cutter
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onCutRingtone(track) }
                        .testTag("btn_cut_ringtone")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCut,
                        contentDescription = "Crear Tono",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Tono", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSecs = (ms / 1000).coerceAtLeast(0)
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}
