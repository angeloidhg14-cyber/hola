package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun VoiceSearchDialog(
    isListening: Boolean,
    currentTranscript: String,
    onStartListening: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_scale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("voice_search_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Búsqueda por Voz Lark",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsing Mic Sphere
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(if (isListening) scale else 1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Mic,
                            contentDescription = "Micrófono",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isListening) "Escuchando... Di el nombre de una canción, artista o etiqueta" else currentTranscript.ifBlank { "Toca el botón para hablar" },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Cyberpunk Horizon", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Música Acústica", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartListening,
                    modifier = Modifier.fillMaxWidth().testTag("btn_start_voice_listening"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isListening) "Escuchando audio..." else "Hablar ahora")
                }
            }
        }
    }
}
