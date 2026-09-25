package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.VideoScene
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.StudioDarkBg
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class CoupleMontageItem(
    val title: String,
    val locationOrDate: String,
    val caption: String,
    val colorHex: Long = 0xFFFDA4AF
)

@Composable
fun VideoPlayerCanvas(
    scenes: List<VideoScene>,
    is3D: Boolean,
    styleName: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSceneIndex by remember { mutableIntStateOf(0) }
    var sceneProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, scenes.size) {
        if (!isPlaying || scenes.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(50)
            sceneProgress += 0.015f
            if (sceneProgress >= 1f) {
                sceneProgress = 0f
                currentSceneIndex = (currentSceneIndex + 1) % scenes.size
            }
        }
    }

    val currentScene = scenes.getOrNull(currentSceneIndex) ?: VideoScene(
        sceneNumber = 1,
        title = "Opening Scene",
        visualPrompt = "Dynamic artistic render with cinematic lighting",
        cameraAngle = "Wide 3D Orbit",
        lightingMood = "Golden Hour Rim Light",
        motionType = "Slow Dolly In",
        audioAtmosphere = "Ambient pads"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "canvas_motion")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF080610))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Background Sky / Atmosphere Gradient
                val skyColors = if (is3D) {
                    listOf(Color(0xFF070514), Color(0xFF1B0F33), Color(0xFF2E1065), Color(0xFF0F172A))
                } else {
                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFF581C87))
                }
                drawRect(
                    brush = Brush.verticalGradient(skyColors),
                    size = size
                )

                if (is3D) {
                    // 3D Perspective Grid
                    draw3DPerspectiveGrid(w, h, time)
                    draw3DCelestialBody(w, h, time)
                    draw3DParallaxPillars(w, h, time, sceneProgress)
                } else {
                    // 2D Anime/Cartoon Layered Landscape
                    draw2DLayeredLandscape(w, h, time, sceneProgress)
                    draw2DFloatingParticles(w, h, time)
                }

                // Cinematic Letterbox (2.35:1 cinema bars)
                val letterboxH = h * 0.10f
                drawRect(Color.Black, topLeft = Offset.Zero, size = Size(w, letterboxH))
                drawRect(Color.Black, topLeft = Offset(0f, h - letterboxH), size = Size(w, letterboxH))
            }

            // Top Camera Badge Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPlaying) Color.Red else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${if (is3D) "3D" else "2D"} • ${currentScene.cameraAngle}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Top Right Dimension & Scene indicator
            Surface(
                color = if (is3D) NeonViolet.copy(alpha = 0.85f) else CyberCyan.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Scene ${currentSceneIndex + 1}/${scenes.size.coerceAtLeast(1)}",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Bottom Subtitle Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentScene.title.uppercase(),
                    color = GoldenAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = currentScene.visualPrompt,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Timeline progress & Playback controls
        LinearProgressIndicator(
            progress = { sceneProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = if (is3D) NeonViolet else CyberCyan,
            trackColor = Color(0xFF1E1735),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (scenes.isNotEmpty()) {
                        currentSceneIndex = (currentSceneIndex - 1 + scenes.size) % scenes.size
                        sceneProgress = 0f
                    }
                },
                modifier = Modifier.testTag("prev_scene_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Scene",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.testTag("toggle_play_video_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = if (is3D) NeonViolet else CyberCyan
                )
            }

            IconButton(
                onClick = {
                    if (scenes.isNotEmpty()) {
                        currentSceneIndex = (currentSceneIndex + 1) % scenes.size
                        sceneProgress = 0f
                    }
                },
                modifier = Modifier.testTag("next_scene_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Scene",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${currentScene.motionType} • $styleName",
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun CoupleMontagePlayer(
    partnerNames: String,
    milestone: String,
    loveQuote: String,
    soundtrackName: String,
    items: List<CoupleMontageItem>,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var itemProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, items.size) {
        if (!isPlaying || items.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(50)
            itemProgress += 0.012f // ~4 seconds per scene
            if (itemProgress >= 1f) {
                itemProgress = 0f
                currentIndex = (currentIndex + 1) % items.size
            }
        }
    }

    val currentItem = items.getOrNull(currentIndex) ?: CoupleMontageItem(
        title = "Our Story",
        locationOrDate = milestone,
        caption = loveQuote
    )

    val infiniteTransition = rememberInfiniteTransition(label = "montage_particles")
    val heartPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heart_phase"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF140D1D))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Ken Burns Zoom factor (from 1.0 to 1.15)
                val zoom = 1.0f + (itemProgress * 0.15f)
                val panOffset = Offset(
                    (sin(itemProgress.toDouble() * PI).toFloat() - 0.5f) * 25f,
                    (cos(itemProgress.toDouble() * PI).toFloat() - 0.5f) * 15f
                )

                // 1. Romantic Background Gradient with Ken Burns zoom simulation
                val tintColor = Color(currentItem.colorHex)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tintColor.copy(alpha = 0.55f),
                            Color(0xFF4C0519),
                            Color(0xFF180A1F)
                        ),
                        center = Offset(w / 2 + panOffset.x, h / 2 + panOffset.y),
                        radius = (w * 0.75f) * zoom
                    ),
                    size = size
                )

                // 2. Romantic Silhouettes & Golden Hour Horizon
                drawRomanticCoupleSilhouette(w, h, zoom, panOffset)

                // 3. Floating Animated Hearts Particles
                drawFloatingHearts(w, h, heartPhase)

                // 4. Subtle Vignette Shadow
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                        center = Offset(w / 2, h / 2),
                        radius = w * 0.7f
                    ),
                    size = size
                )
            }

            // Top Header: Personalized Couple Names & Love Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RomanticRose.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = partnerNames.ifBlank { "Aarav & Taruni" },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${currentIndex + 1} / ${items.size.coerceAtLeast(1)}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Center/Bottom Romantic Quote Card
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Color.Black.copy(alpha = 0.60f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentItem.title,
                    color = GoldenAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentItem.locationOrDate,
                    color = RomanticRose,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "\"${currentItem.caption}\"",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Beat-Sync Progress Bar
        LinearProgressIndicator(
            progress = { itemProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = RomanticRose,
            trackColor = Color(0xFF281124)
        )

        // Montage Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (items.isNotEmpty()) {
                        currentIndex = (currentIndex - 1 + items.size) % items.size
                        itemProgress = 0f
                    }
                },
                modifier = Modifier.testTag("prev_montage_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Moment",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.testTag("toggle_play_montage_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = RomanticRose
                )
            }

            IconButton(
                onClick = {
                    if (items.isNotEmpty()) {
                        currentIndex = (currentIndex + 1) % items.size
                        itemProgress = 0f
                    }
                },
                modifier = Modifier.testTag("next_montage_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Moment",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "♫ $soundtrackName",
                    color = RomanticRose,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Auto Beat-Matched Ken Burns",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ----------------- Canvas Drawing Helpers -----------------

private fun DrawScope.draw3DPerspectiveGrid(w: Float, h: Float, time: Float) {
    val horizonY = h * 0.52f
    val vanishX = w * 0.5f

    // Perspective lines radiating from vanishing point
    val lineCount = 14
    for (i in -lineCount..lineCount) {
        val bottomX = vanishX + (i * w * 0.08f)
        drawLine(
            color = NeonViolet.copy(alpha = 0.35f),
            start = Offset(vanishX, horizonY),
            end = Offset(bottomX, h),
            strokeWidth = 1.2.dp.toPx()
        )
    }

    // Moving horizontal grid lines (giving forward speed sensation)
    val speed = (time * 1.5f) % 40f
    for (step in 1..8) {
        val yProg = (step * 0.12f + speed * 0.003f) % 0.95f
        val lineY = horizonY + (h - horizonY) * (yProg * yProg)
        drawLine(
            color = CyberCyan.copy(alpha = (0.5f * yProg).coerceIn(0.1f, 0.7f)),
            start = Offset(0f, lineY),
            end = Offset(w, lineY),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

private fun DrawScope.draw3DCelestialBody(w: Float, h: Float, time: Float) {
    val sunX = w * 0.5f
    val sunY = h * 0.40f

    // Glowing synthwave sun / 3D sphere
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(GoldenAmber, RomanticRose, Color.Transparent),
            center = Offset(sunX, sunY),
            radius = w * 0.28f
        ),
        center = Offset(sunX, sunY),
        radius = w * 0.28f
    )

    // Inner bright core
    drawCircle(
        color = Color(0xFFFFFBEB),
        center = Offset(sunX, sunY),
        radius = w * 0.09f
    )
}

private fun DrawScope.draw3DParallaxPillars(w: Float, h: Float, time: Float, progress: Float) {
    val horizonY = h * 0.52f
    // Two cyber monolith silhouettes flanking the camera
    val pW = w * 0.12f
    val pH = h * 0.38f

    // Left pillar
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF0F0B1E), Color(0xFF2A1647))
        ),
        topLeft = Offset(w * 0.08f, horizonY - pH * 0.6f),
        size = Size(pW, pH)
    )

    // Right pillar
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF2A1647), Color(0xFF0F0B1E))
        ),
        topLeft = Offset(w * 0.80f, horizonY - pH * 0.6f),
        size = Size(pW, pH)
    )
}

private fun DrawScope.draw2DLayeredLandscape(w: Float, h: Float, time: Float, progress: Float) {
    val horizonY = h * 0.55f

    // Distant mountain ridge (slow parallax)
    val distantPath = Path().apply {
        moveTo(0f, horizonY)
        cubicTo(w * 0.25f, horizonY - 45f, w * 0.45f, horizonY - 20f, w * 0.65f, horizonY - 60f)
        cubicTo(w * 0.85f, horizonY - 25f, w * 0.95f, horizonY - 50f, w, horizonY - 15f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(distantPath, color = Color(0xFF2C194D))

    // Midground hills with warm rim
    val midOffset = (progress * 20f)
    val midPath = Path().apply {
        moveTo(0f, horizonY + 20f)
        cubicTo(w * 0.3f, horizonY - 15f + midOffset, w * 0.6f, horizonY + 30f, w, horizonY + 5f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        midPath,
        brush = Brush.verticalGradient(listOf(Color(0xFF4A1A6D), Color(0xFF1D0933)))
    )

    // Solitary anime tree / character silhouette on foreground cliff
    drawCircle(
        color = Color(0xFF0C0717),
        center = Offset(w * 0.25f, h * 0.72f),
        radius = 28.dp.toPx()
    )
}

private fun DrawScope.draw2DFloatingParticles(w: Float, h: Float, time: Float) {
    for (i in 0..12) {
        val px = (w * ((i * 0.08f + time * 0.02f) % 1.0f))
        val py = (h * ((i * 0.11f + time * 0.015f) % 0.8f))
        drawCircle(
            color = GoldenAmber.copy(alpha = 0.65f),
            center = Offset(px, py),
            radius = (1.5f + (i % 3)).dp.toPx()
        )
    }
}

private fun DrawScope.drawRomanticCoupleSilhouette(w: Float, h: Float, zoom: Float, pan: Offset) {
    val centerX = w * 0.5f + pan.x
    val baseY = h * 0.72f + pan.y

    // Couple standing on bridge / cliff silhouette
    drawOval(
        color = Color(0xFF12061A),
        topLeft = Offset(centerX - 80f * zoom, baseY),
        size = Size(160f * zoom, 40f * zoom)
    )

    // Silhouette Figures
    // Partner 1
    drawCircle(
        color = Color(0xFF12061A),
        center = Offset(centerX - 14f * zoom, baseY - 42f * zoom),
        radius = 12f * zoom
    )
    drawRoundRect(
        color = Color(0xFF12061A),
        topLeft = Offset(centerX - 24f * zoom, baseY - 30f * zoom),
        size = Size(20f * zoom, 34f * zoom),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
    )

    // Partner 2 (leaning slightly)
    drawCircle(
        color = Color(0xFF12061A),
        center = Offset(centerX + 8f * zoom, baseY - 38f * zoom),
        radius = 11f * zoom
    )
    drawRoundRect(
        color = Color(0xFF12061A),
        topLeft = Offset(centerX - 2f * zoom, baseY - 27f * zoom),
        size = Size(18f * zoom, 31f * zoom),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
    )
}

private fun DrawScope.drawFloatingHearts(w: Float, h: Float, phase: Float) {
    for (i in 0..8) {
        val yOffset = ((phase * 40f + i * 50f) % h)
        val hY = h - yOffset
        val sway = sin((phase + i).toDouble()).toFloat() * 20f
        val hX = (w * (0.15f + i * 0.10f)) + sway
        val sizeH = (6f + (i % 4) * 2f).dp.toPx()

        drawCircle(
            color = RomanticRose.copy(alpha = (0.7f * (1f - (yOffset / h))).coerceIn(0.1f, 0.8f)),
            center = Offset(hX, hY),
            radius = sizeH / 2
        )
    }
}
