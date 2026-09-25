package com.example.ui.montage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.CoupleMontageScript
import com.example.data.ai.PromptPresets
import com.example.ui.components.CoupleMontageItem
import com.example.ui.components.CoupleMontagePlayer
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

@Composable
fun CoupleMontageScreen(
    currentScript: CoupleMontageScript?,
    isGenerating: Boolean,
    isPlayingMontage: Boolean,
    onGenerateMontage: (partner1: String, partner2: String, milestone: String, themeName: String) -> Unit,
    onTogglePlayMontage: () -> Unit,
    onSaveMontage: (CoupleMontageScript) -> Unit,
    onShareMontage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var partner1Name by remember { mutableStateOf("Aarav") }
    var partner2Name by remember { mutableStateOf("Taruni") }
    var milestoneText by remember { mutableStateOf("2nd Anniversary • Together Forever") }

    var selectedThemeId by remember { mutableStateOf("love_story") }
    val currentTheme = PromptPresets.montageThemes.find { it.id == selectedThemeId } ?: PromptPresets.montageThemes.first()

    // Preloaded couple moments list with editability
    val montageItems = remember {
        mutableStateListOf(
            CoupleMontageItem("The Beginning", "Where we first met", "That single smile that changed everything ✨", 0xFFFDA4AF),
            CoupleMontageItem("Coffee & Smiles", "Corner Cafe", "Spilling coffee and talking until closing time ☕", 0xFFFDE68A),
            CoupleMontageItem("Sunset By The Shore", "Golden Sands", "Watching the sky catch fire while holding hands 🌅", 0xFFF472B6),
            CoupleMontageItem("Rainy City Walk", "Downtown Lights", "Dancing under one small umbrella in the storm ☔", 0xFF93C5FD),
            CoupleMontageItem("Forever & Always", "Our Happy Place", "To every yesterday, today, and all our tomorrows 💍", 0xFFC084FC)
        )
    }

    // Android Zero-permission Photo Picker for personal couple photos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            montageItems.add(
                0,
                CoupleMontageItem(
                    title = "Special Memory",
                    locationOrDate = "Photo Added",
                    caption = "A beautiful moment frozen in time ❤️",
                    colorHex = 0xFFF43F5E
                )
            )
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // 1. Montage Player Canvas
        Column(modifier = Modifier.padding(16.dp)) {
            CoupleMontagePlayer(
                partnerNames = if (currentScript != null) currentScript.coupleNames else "$partner1Name & $partner2Name",
                milestone = if (currentScript != null) currentScript.milestoneText else milestoneText,
                loveQuote = currentScript?.loveQuote ?: currentTheme.defaultQuote,
                soundtrackName = currentTheme.soundtrack,
                items = montageItems,
                isPlaying = isPlayingMontage,
                onTogglePlay = onTogglePlayMontage
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentScript?.let { script ->
                    OutlinedButton(
                        onClick = { onSaveMontage(script) },
                        modifier = Modifier.testTag("save_montage_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RomanticRose),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Save",
                            tint = RomanticRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Montage", color = RomanticRose, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val shareText = """
                                💖 Couple Montage: ${script.coupleNames}
                                📅 Milestone: ${script.milestoneText}
                                ✨ Quote: "${script.loveQuote}"
                                
                                Chapters:
                                ${script.chapters.joinToString("\n") { "• ${it.title}: ${it.loveNote}" }}
                                
                                Created with Taruni AI Studio
                            """.trimIndent()
                            onShareMontage(shareText)
                        },
                        modifier = Modifier.testTag("share_montage_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Story", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Personalize Couple Names & Milestone
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                containerColor = StudioCardBg
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Couple",
                        tint = RomanticRose,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Couple Details",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = partner1Name,
                        onValueChange = { partner1Name = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("partner1_name_input"),
                        label = { Text("Partner 1", color = TextSecondary, fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StudioSurfaceVariant,
                            unfocusedContainerColor = StudioSurfaceVariant,
                            focusedBorderColor = RomanticRose,
                            unfocusedBorderColor = StudioCardStroke,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = partner2Name,
                        onValueChange = { partner2Name = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("partner2_name_input"),
                        label = { Text("Partner 2", color = TextSecondary, fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StudioSurfaceVariant,
                            unfocusedContainerColor = StudioSurfaceVariant,
                            focusedBorderColor = RomanticRose,
                            unfocusedBorderColor = StudioCardStroke,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = milestoneText,
                    onValueChange = { milestoneText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("milestone_input"),
                    label = { Text("Occasion / Milestone / Date", color = TextSecondary, fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurfaceVariant,
                        unfocusedContainerColor = StudioSurfaceVariant,
                        focusedBorderColor = RomanticRose,
                        unfocusedBorderColor = StudioCardStroke,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Montage Theme Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Montage Aesthetic & Mood",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            PromptPresets.montageThemes.forEach { theme ->
                val isSel = theme.id == selectedThemeId
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { selectedThemeId = theme.id },
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
                        containerColor = if (isSel) RomanticRose.copy(alpha = 0.15f) else StudioCardBg
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) RomanticRose else StudioCardStroke
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isSel) RomanticRose else StudioSurfaceVariant,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            if (isSel) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = theme.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = theme.subtitle,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "♫ ${theme.soundtrack}",
                                color = GoldenAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Photo Picker Button & Moments Timeline
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Montage Moments (${montageItems.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo", color = CyberCyan, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal row of moment thumbnails
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                montageItems.forEachIndexed { idx, item ->
                    Surface(
                        color = StudioCardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardStroke),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color(item.colorHex),
                                                Color(0xFF2E0818)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${idx + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.title,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = item.locationOrDate,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Auto-Edit Magic Button
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = {
                    onGenerateMontage(partner1Name, partner2Name, milestoneText, currentTheme.title)
                },
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auto_edit_montage_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RomanticRose
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Auto-Editing Couple Montage...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Auto Edit",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto-Edit Couple Video & Montage",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Generated Montage Story Script Breakdown
        currentScript?.let { script ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Montage Storyboard & Love Quotes",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Love Quote Card
                Surface(
                    color = StudioCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RomanticRose.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DEDICATION QUOTE",
                            color = RomanticRose,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${script.loveQuote}\"",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chapter Cards
                script.chapters.forEachIndexed { i, ch ->
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
                                    color = RomanticRose,
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${i + 1}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ch.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = ch.visualEffect,
                                    color = GoldenAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ch.subtitle,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💬 \"${ch.loveNote}\"",
                                color = RomanticRose,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}
