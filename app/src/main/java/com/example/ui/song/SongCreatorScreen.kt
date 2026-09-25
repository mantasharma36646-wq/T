package com.example.ui.song

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.PromptPresets
import com.example.data.ai.SongComposition
import com.example.ui.components.AudioVisualizerBars
import com.example.ui.components.VinylRecordPlayer
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
fun SongCreatorScreen(
    currentSong: SongComposition?,
    isGenerating: Boolean,
    isPlayingAudio: Boolean,
    audioAmplitude: Float,
    onGenerateSong: (theme: String, genre: String, bpm: Int, vocalStyle: String) -> Unit,
    onTogglePlayAudio: () -> Unit,
    onSaveSong: (SongComposition) -> Unit,
    onShareSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var themeInput by remember {
        mutableStateOf("Stargazing on a summer rooftop with someone special, watching shooting stars")
    }

    var selectedGenreId by remember { mutableStateOf("romantic_pop") }
    val currentGenre = PromptPresets.songGenres.find { it.id == selectedGenreId } ?: PromptPresets.songGenres.first()

    var selectedBpm by remember { mutableIntStateOf(currentGenre.defaultBpm) }
    var selectedVocalStyle by remember { mutableStateOf("Warm Male Vocals") }

    val vocalStyles = listOf(
        "Warm Male Vocals",
        "Ethereal Female Vocals",
        "Romantic Duet",
        "Cyber Vocoder",
        "Acoustic Pure"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // 1. Audio Player Card & Live Synthesizer Visualizer
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                containerColor = StudioCardBg
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = RomanticRose.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Audio Engine",
                                tint = RomanticRose,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "REAL-TIME SYNTHESIZER",
                                color = RomanticRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${currentSong?.musicalKey ?: "C Major"} • ${currentSong?.bpm ?: selectedBpm} BPM",
                        color = GoldenAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vinyl Player Artwork
                VinylRecordPlayer(
                    isPlaying = isPlayingAudio,
                    trackTitle = currentSong?.title ?: "Taruni Melody",
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentSong?.title ?: "Generate Your Song",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${currentSong?.genre ?: currentGenre.name} • ${currentSong?.vocalStyle ?: selectedVocalStyle}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reactive Audio Visualizer Bars
                AudioVisualizerBars(
                    isPlaying = isPlayingAudio,
                    amplitude = audioAmplitude,
                    barColor = NeonViolet,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Play / Pause Synthesizer Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTogglePlayAudio,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("toggle_play_audio_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingAudio) RomanticRose else NeonViolet
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingAudio) "Pause Music" else "Play Music",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlayingAudio) "Pause Synthesizer" else "Play Generated Music",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Chords Display Badge
                currentSong?.chordProgression?.let { chords ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Chords:", color = TextMuted, fontSize = 11.sp)
                        chords.forEach { ch ->
                            Surface(
                                color = StudioSurfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ch,
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Song Theme Input & Presets
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Song Theme & Story",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = themeInput,
                onValueChange = { themeInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("song_theme_input"),
                placeholder = { Text("What is this song about? Describe feelings, places, moods...", color = TextMuted) },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StudioCardBg,
                    unfocusedContainerColor = StudioCardBg,
                    focusedBorderColor = RomanticRose,
                    unfocusedBorderColor = StudioCardStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Presets
            Text(
                text = "Inspiration Themes:",
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
                PromptPresets.songThemes.forEach { theme ->
                    Surface(
                        color = StudioSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { themeInput = theme }
                    ) {
                        Text(
                            text = theme,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Genre Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Musical Genre",
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
                PromptPresets.songGenres.forEach { g ->
                    val isSel = g.id == selectedGenreId
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedGenreId = g.id
                            selectedBpm = g.defaultBpm
                        },
                        label = {
                            Text(
                                text = g.name,
                                color = if (isSel) Color.White else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RomanticRose.copy(alpha = 0.35f),
                            containerColor = StudioCardBg
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSel) RomanticRose else StudioCardStroke,
                            enabled = true,
                            selected = isSel
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Vocal Style & Tempo (BPM)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Vocal Style",
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
                vocalStyles.forEach { v ->
                    val isSel = v == selectedVocalStyle
                    Surface(
                        color = if (isSel) NeonViolet.copy(alpha = 0.25f) else StudioCardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) NeonViolet else StudioCardStroke
                        ),
                        modifier = Modifier.clickable { selectedVocalStyle = v }
                    ) {
                        Text(
                            text = v,
                            color = if (isSel) TextPrimary else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tempo BPM Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tempo (BPM)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "$selectedBpm BPM", color = GoldenAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = selectedBpm.toFloat(),
                onValueChange = { selectedBpm = it.toInt() },
                valueRange = 65f..150f,
                colors = SliderDefaults.colors(
                    thumbColor = RomanticRose,
                    activeTrackColor = RomanticRose,
                    inactiveTrackColor = StudioCardStroke
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Generate AI Song Button
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = { onGenerateSong(themeInput, currentGenre.name, selectedBpm, selectedVocalStyle) },
                enabled = !isGenerating && themeInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_song_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RomanticRose
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Composing Melody & Lyrics...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Compose Song",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Make Song & Generate Music",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Generated Lyrics & Chords Display
        currentSong?.let { song ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = song.title,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Key: ${song.musicalKey} • ${song.genre}",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row {
                        OutlinedButton(
                            onClick = { onSaveSong(song) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_song_button")
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Save", tint = RomanticRose, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", color = RomanticRose, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                val fullLyrics = buildString {
                                    appendLine("🎵 ${song.title} (${song.genre} - ${song.bpm} BPM)")
                                    appendLine("Key: ${song.musicalKey}")
                                    appendLine()
                                    song.sections.forEach { s ->
                                        appendLine("[${s.label}] ${s.chords}")
                                        s.lines.forEach { line -> appendLine(line) }
                                        appendLine()
                                    }
                                    appendLine("Generated with Taruni Studio")
                                }
                                onShareSong(fullLyrics)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("share_lyrics_button")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Song Sections
                song.sections.forEach { section ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                            containerColor = StudioCardBg
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = section.label,
                                    color = GoldenAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = section.chords,
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            section.lines.forEach { line ->
                                Text(
                                    text = line,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
