package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.model.Track
import com.example.ui.theme.*

@Composable
fun MiniPlayer(
    playbackState: PlaybackState,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onOpenPlayer: () -> Unit,
    onOpenEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return

    // Infinite rotation for vinyl effect when playing
    val infiniteTransition = rememberInfiniteTransition(label = "mini_disc_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val progress = if (playbackState.durationMs > 0) {
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary)
            .testTag("mini_player_surface")
            .clickable(onClick = onOpenPlayer),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Smooth progress indicator line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini rotating vinyl disk
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = track.coverDrawableRes),
                        contentDescription = "Carátula de ${track.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (playbackState.isPlaying) Modifier.rotate(rotation) else Modifier)
                    )
                    // Vinyl center spindle hole
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Audio format badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (track.format.isHiRes) HiResGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (track.format.isHiRes) "Hi-Res" else "HD",
                                color = if (track.format.isHiRes) HiResGold else MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = "${track.artist} • ${track.format.label}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quick Equalizer button
                IconButton(
                    onClick = onOpenEqualizer,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("mini_player_equalizer_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "Abrir ecualizador",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Play / Pause Button with neon circle
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            ),
                            shape = CircleShape
                        )
                        .testTag("mini_player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pausar" else "Reproducir",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Skip Next Button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("mini_player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Siguiente canción",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
