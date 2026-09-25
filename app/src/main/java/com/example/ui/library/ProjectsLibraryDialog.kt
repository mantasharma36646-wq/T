package com.example.ui.library

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.SavedProject
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsLibraryDialog(
    projects: List<SavedProject>,
    onDismiss: () -> Unit,
    onDeleteProject: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onSelectProject: (SavedProject) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredProjects = projects.filter {
        when (selectedFilter) {
            "ALL" -> true
            "VIDEO" -> it.type.startsWith("VIDEO")
            "SONG" -> it.type == "SONG"
            "MONTAGE" -> it.type == "COUPLE_MONTAGE"
            else -> true
        }
    }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = StudioDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardStroke)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Taruni Studio Library",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${projects.size} Saved Creations",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_library_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filters Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL" to "All (${projects.size})", "VIDEO" to "Videos (2D/3D)", "SONG" to "Songs", "MONTAGE" to "Couple Montages").forEach { (key, label) ->
                        val isSel = selectedFilter == key
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFilter = key },
                            label = { Text(text = label, color = if (isSel) Color.White else TextSecondary, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonViolet.copy(alpha = 0.35f),
                                containerColor = StudioCardBg
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSel) NeonViolet else StudioCardStroke,
                                enabled = true,
                                selected = isSel
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content List
                if (filteredProjects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Empty",
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No saved creations yet",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Generate a video, song, or couple montage and save it!",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredProjects, key = { it.id }) { item ->
                            val typeColor = when (item.type) {
                                "VIDEO_3D" -> NeonViolet
                                "VIDEO_2D" -> CyberCyan
                                "SONG" -> GoldenAmber
                                else -> RomanticRose
                            }

                            val typeIcon = when (item.type) {
                                "VIDEO_3D", "VIDEO_2D" -> Icons.Default.Movie
                                "SONG" -> Icons.Default.MusicNote
                                else -> Icons.Default.Favorite
                            }

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectProject(item) },
                                shape = RoundedCornerShape(16.dp),
                                colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                                    containerColor = StudioCardBg
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = typeColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = typeIcon,
                                                contentDescription = item.type,
                                                tint = typeColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${item.style} • ${dateFormat.format(Date(item.timestamp))}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = { onToggleFavorite(item.id, !item.isFavorite) }
                                    ) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (item.isFavorite) RomanticRose else TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteProject(item.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = TextMuted,
                                            modifier = Modifier.size(20.dp)
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
}
