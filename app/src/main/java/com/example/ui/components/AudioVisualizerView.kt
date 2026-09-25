package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import kotlin.math.sin

@Composable
fun AudioVisualizerBars(
    isPlaying: Boolean,
    amplitude: Float,
    barCount: Int = 24,
    modifier: Modifier = Modifier,
    barColor: Color = NeonViolet
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(48.dp)) {
        val totalWidth = size.width
        val maxHeight = size.height
        val barWidth = (totalWidth / barCount) * 0.65f
        val gap = (totalWidth - (barWidth * barCount)) / (barCount + 1)

        val brush = Brush.verticalGradient(
            colors = listOf(RomanticRose, barColor, CyberCyan)
        )

        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount
            val wave = sin((phase + normalizedIndex * 4.0f).toDouble()).toFloat()
            val dynamicFactor = if (isPlaying) {
                0.25f + 0.75f * ((wave + 1f) / 2f) * (0.4f + amplitude * 1.5f).coerceIn(0.2f, 1f)
            } else {
                0.12f
            }

            val currentBarHeight = (maxHeight * dynamicFactor).coerceIn(4.dp.toPx(), maxHeight)
            val left = gap + i * (barWidth + gap)
            val top = maxHeight - currentBarHeight

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, currentBarHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

@Composable
fun VinylRecordPlayer(
    isPlaying: Boolean,
    trackTitle: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentRotation = if (isPlaying) rotation else 0f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(Color(0xFF110D20))
            .rotate(currentRotation)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Outer vinyl groove rings
            for (r in listOf(0.9f, 0.82f, 0.74f, 0.66f, 0.58f)) {
                drawCircle(
                    color = Color(0xFF261D40),
                    radius = radius * r,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Center album sticker
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonViolet, RomanticRose, Color(0xFF4C0519)),
                    center = center,
                    radius = radius * 0.42f
                ),
                radius = radius * 0.42f,
                center = center
            )

            // Center spindle hole
            drawCircle(
                color = Color(0xFF0C0916),
                radius = radius * 0.08f,
                center = center
            )
        }

        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Playing Music",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
