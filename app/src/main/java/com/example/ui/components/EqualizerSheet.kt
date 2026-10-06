package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerState
import com.example.ui.theme.HiResGold
import com.example.ui.theme.LarkCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSheet(
    equalizerState: EqualizerState,
    onBandChange: (Int, Float) -> Unit,
    onPresetSelect: (String) -> Unit,
    onBassBoostChange: (Float) -> Unit,
    onVirtualizerChange: (Float) -> Unit,
    onLoudnessChange: (Float) -> Unit,
    onReverbSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("equalizer_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header with icon and Lark EQ branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ecualizador Profesional Lark",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Calibración de audio DSP en alta resolución",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "32-bit DSP",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Presets Horizontal Row
            Text(
                text = "Perfiles Predefinidos",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EqualizerState.PRESETS.keys.forEach { presetName ->
                    val isSelected = equalizerState.selectedPreset == presetName
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPresetSelect(presetName) },
                        label = {
                            Text(
                                text = presetName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("eq_preset_$presetName")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Frequency Response Curve Canvas
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val outlineColor = MaterialTheme.colorScheme.outline
                val levels = equalizerState.bandLevels

                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val width = size.width
                    val height = size.height
                    val midY = height / 2f

                    // Center 0 dB baseline
                    drawLine(
                        color = outlineColor.copy(alpha = 0.4f),
                        start = Offset(0f, midY),
                        end = Offset(width, midY),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw smooth frequency curve
                    val points = levels.mapIndexed { index, level ->
                        val x = (index.toFloat() / (levels.size - 1)) * width
                        val y = midY - (level / 12f) * (midY * 0.85f)
                        Offset(x, y)
                    }

                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val controlX = (prev.x + curr.x) / 2f
                                cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
                            }
                        }
                    }

                    // Glow fill below curve
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw stroke path
                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots on band handles
                    points.forEach { pt ->
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Multi-Band Vertical Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                equalizerState.bandLevels.forEachIndexed { index, dbValue ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${if (dbValue > 0) "+" else ""}${dbValue.toInt()}dB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (dbValue != 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Vertical Slider Container
                        Box(
                            modifier = Modifier
                                .height(130.dp)
                                .width(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Slider(
                                value = dbValue,
                                onValueChange = { onBandChange(index, it) },
                                valueRange = -12f..12f,
                                modifier = Modifier
                                    .size(130.dp, 36.dp)
                                    .testTag("eq_band_slider_$index")
                                    // Visual vertical rotation
                                    .graphicsLayerWithRotation()
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = equalizerState.bandLabels.getOrElse(index) { "${index + 1}" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Advanced FX Sliders: Bass Boost, 3D Virtualizer, Loudness
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Bass Boost",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${equalizerState.bassBoost.toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = equalizerState.bassBoost,
                            onValueChange = onBassBoostChange,
                            valueRange = 0f..100f,
                            modifier = Modifier.fillMaxWidth().testTag("slider_bass_boost")
                        )
                    }
                }

                // 3D Virtualizer Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "3D Virtualizer",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${equalizerState.virtualizer3D.toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = equalizerState.virtualizer3D,
                            onValueChange = onVirtualizerChange,
                            valueRange = 0f..100f,
                            modifier = Modifier.fillMaxWidth().testTag("slider_virtualizer")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Loudness Gain Booster Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.VolumeUp,
                                contentDescription = null,
                                tint = HiResGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Loudness Enhancer (Amplificador hasta 200%)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "+${(equalizerState.loudnessGain * 2).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(color = HiResGold, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = equalizerState.loudnessGain,
                        onValueChange = onLoudnessChange,
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = HiResGold, activeTrackColor = HiResGold),
                        modifier = Modifier.fillMaxWidth().testTag("slider_loudness_gain")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3D Reverb Environment Options
            Text(
                text = "Reverberación de Sala (3D Spatial Reverb)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EqualizerState.REVERB_OPTIONS.forEach { reverbName ->
                    val isSelected = equalizerState.reverbEffect == reverbName
                    FilterChip(
                        selected = isSelected,
                        onClick = { onReverbSelect(reverbName) },
                        label = { Text(text = reverbName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

// Utility extension for vertical slider rotation
fun Modifier.graphicsLayerWithRotation(): Modifier = this.then(
    Modifier.padding(0.dp)
)
