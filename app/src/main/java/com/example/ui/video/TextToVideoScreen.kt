package com.example.ui.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.PromptPresets
import com.example.data.ai.VideoScene
import com.example.data.ai.VideoStoryboard
import com.example.ui.components.VideoPlayerCanvas
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardStroke
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextToVideoScreen(
    currentStoryboard: VideoStoryboard?,
    isGenerating: Boolean,
    onGenerate: (prompt: String, is3D: Boolean, styleName: String) -> Unit,
    onSaveProject: (storyboard: VideoStoryboard) -> Unit,
    onShareText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDimensionTab by remember { mutableIntStateOf(1) } // 0 = 2D, 1 = 3D
    val is3D = selectedDimensionTab == 1

    val currentStyles = if (is3D) PromptPresets.video3DStyles else PromptPresets.video2DStyles
    var selectedStyleId by remember(is3D) { mutableStateOf(currentStyles.first().id) }
    val selectedStyle = currentStyles.find { it.id == selectedStyleId } ?: currentStyles.first()

    var promptInput by remember {
        mutableStateOf("A solitary neon samurai walking through rain-slicked alleys in Neo-Tokyo, reflecting holographic cherry blossoms and glowing billboards.")
    }

    var isVideoPlaying by remember { mutableStateOf(true) }
    var selectedAspectRatio by remember { mutableStateOf("16:9 Cinema") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // 1. Dimension Switcher Tab (2D Animation vs 3D Cinematic)
        TabRow(
            selectedTabIndex = selectedDimensionTab,
            containerColor = StudioCardBg,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedDimensionTab])
                        .height(3.dp)
                        .background(if (is3D) NeonViolet else CyberCyan)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedDimensionTab == 0,
                onClick = { selectedDimensionTab = 0 },
                modifier = Modifier.testTag("tab_2d_video"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "2D",
                            tint = if (selectedDimensionTab == 0) CyberCyan else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2D Animation",
                            color = if (selectedDimensionTab == 0) TextPrimary else TextMuted,
                            fontWeight = if (selectedDimensionTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
            Tab(
                selected = selectedDimensionTab == 1,
                onClick = { selectedDimensionTab = 1 },
                modifier = Modifier.testTag("tab_3d_video"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "3D",
                            tint = if (selectedDimensionTab == 1) NeonViolet else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3D Cinematic",
                            color = if (selectedDimensionTab == 1) TextPrimary else TextMuted,
                            fontWeight = if (selectedDimensionTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Video Player & Preview Canvas
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            val scenesToDisplay = currentStoryboard?.scenes ?: listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Opening Atmosphere",
                    visualPrompt = promptInput,
                    cameraAngle = if (is3D) "3D Orbit Cam" else "2D Crane Pan",
                    lightingMood = "Cinematic Rim Lighting",
                    motionType = "Dolly Forward",
                    audioAtmosphere = "Rain & synthesizer pads"
                )
            )

            VideoPlayerCanvas(
                scenes = scenesToDisplay,
                is3D = is3D,
                styleName = selectedStyle.name,
                isPlaying = isVideoPlaying,
                onTogglePlay = { isVideoPlaying = !isVideoPlaying }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row: Save & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentStoryboard?.let { sb ->
                    OutlinedButton(
                        onClick = { onSaveProject(sb) },
                        modifier = Modifier.testTag("save_video_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Save",
                            tint = NeonViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Video", color = NeonViolet, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val shareSummary = """
                                🎬 Taruni ${sb.dimension} Video: ${sb.title}
                                Style: ${sb.visualStyle}
                                Logline: ${sb.logline}
                                Scenes: ${sb.scenes.size}
                                Generated with Taruni AI Studio
                            """.trimIndent()
                            onShareText(shareSummary)
                        },
                        modifier = Modifier.testTag("share_video_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Script", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Style Picker Chips
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "${if (is3D) "3D" else "2D"} Art Style",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentStyles.forEach { style ->
                    val isSelected = style.id == selectedStyleId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStyleId = style.id },
                        label = {
                            Text(
                                text = style.name,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (is3D) NeonViolet else CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (is3D) NeonViolet.copy(alpha = 0.35f) else CyberCyan.copy(alpha = 0.35f),
                            containerColor = StudioCardBg
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) (if (is3D) NeonViolet else CyberCyan) else StudioCardStroke,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
            Text(
                text = selectedStyle.description,
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Prompt Input & Preset Inspiration
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Text-to-Video Prompt",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Powered by Gemini AI",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_prompt_input"),
                placeholder = {
                    Text(
                        "Describe your scene: character, environment, mood, action...",
                        color = TextMuted
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StudioCardBg,
                    unfocusedContainerColor = StudioCardBg,
                    focusedBorderColor = if (is3D) NeonViolet else CyberCyan,
                    unfocusedBorderColor = StudioCardStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Presets
            Text(
                text = "Preset Prompts:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PromptPresets.videoIdeas.forEach { idea ->
                    Surface(
                        color = StudioSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable {
                                promptInput = idea.prompt
                                selectedDimensionTab = if (idea.is3D) 1 else 0
                                selectedStyleId = idea.styleId
                            }
                    ) {
                        Text(
                            text = idea.title,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Aspect Ratio & Motion Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("16:9 Cinema", "9:16 Shorts", "1:1 Square").forEach { ratio ->
                val isSel = selectedAspectRatio == ratio
                Surface(
                    color = if (isSel) (if (is3D) NeonViolet.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.25f)) else StudioCardBg,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) (if (is3D) NeonViolet else CyberCyan) else StudioCardStroke
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedAspectRatio = ratio }
                ) {
                    Text(
                        text = ratio,
                        color = if (isSel) TextPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 6. Generate Button
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = { onGenerate(promptInput, is3D, selectedStyle.name) },
                enabled = !isGenerating && promptInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_video_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (is3D) NeonViolet else CyberCyan
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Synthesizing ${if (is3D) "3D" else "2D"} Video...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Generate",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate ${if (is3D) "3D" else "2D"} Video & Storyboard",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Multi-Scene Storyboard Breakdown
        currentStoryboard?.let { sb ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Storyboard Director Breakdown",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sb.title} • ${sb.dimension} ${sb.visualStyle}",
                    color = GoldenAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                sb.scenes.forEach { scene ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                            containerColor = StudioCardBg
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (is3D) NeonViolet else CyberCyan,
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${scene.sceneNumber}",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = scene.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = scene.visualPrompt,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = StudioSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "🎥 ${scene.cameraAngle}",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = StudioSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "💡 ${scene.lightingMood}",
                                        color = GoldenAmber,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
