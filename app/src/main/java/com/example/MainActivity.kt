package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SavedProject
import com.example.ui.components.AboutTaruniDialog
import com.example.ui.components.StudioTopBar
import com.example.ui.library.ProjectsLibraryDialog
import com.example.ui.montage.CoupleMontageScreen
import com.example.ui.song.SongCreatorScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.video.TextToVideoScreen
import com.example.viewmodel.TaruniViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: TaruniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TaruniApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TaruniApp(viewModel: TaruniViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()

    var showLibraryDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Video State
    val currentStoryboard by viewModel.currentStoryboard.collectAsStateWithLifecycle()
    val isVideoGenerating by viewModel.isVideoGenerating.collectAsStateWithLifecycle()

    // Song State
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isSongGenerating by viewModel.isSongGenerating.collectAsStateWithLifecycle()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsStateWithLifecycle()
    val audioAmplitude by viewModel.audioAmplitude.collectAsStateWithLifecycle()

    // Couple Montage State
    val currentCoupleScript by viewModel.currentCoupleScript.collectAsStateWithLifecycle()
    val isMontageGenerating by viewModel.isMontageGenerating.collectAsStateWithLifecycle()
    val isPlayingMontage by viewModel.isPlayingMontage.collectAsStateWithLifecycle()

    // Back handler: return to main section 0 if on another tab
    if (currentSection != 0) {
        BackHandler {
            viewModel.setSection(0)
        }
    }

    val shareContent: (String) -> Unit = { text ->
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share with"))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StudioTopBar(
                savedCount = savedProjects.size,
                onOpenLibrary = { showLibraryDialog = true },
                onOpenInfo = { showAboutDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StudioCardBg,
                contentColor = TextPrimary,
                tonalElevation = 8.dp
            ) {
                // Section 0: Text-to-Video
                val isSel0 = currentSection == 0
                NavigationBarItem(
                    selected = isSel0,
                    onClick = { viewModel.setSection(0) },
                    modifier = Modifier.testTag("nav_video_tab"),
                    icon = {
                        Icon(
                            imageVector = if (isSel0) Icons.Filled.Movie else Icons.Outlined.Movie,
                            contentDescription = "2D & 3D Video",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "2D & 3D Video",
                            fontSize = 11.sp,
                            fontWeight = if (isSel0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonViolet,
                        selectedTextColor = NeonViolet,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = NeonViolet.copy(alpha = 0.2f)
                    )
                )

                // Section 1: Songs
                val isSel1 = currentSection == 1
                NavigationBarItem(
                    selected = isSel1,
                    onClick = { viewModel.setSection(1) },
                    modifier = Modifier.testTag("nav_songs_tab"),
                    icon = {
                        Icon(
                            imageVector = if (isSel1) Icons.Filled.MusicNote else Icons.Outlined.MusicNote,
                            contentDescription = "Songs",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "AI Songs",
                            fontSize = 11.sp,
                            fontWeight = if (isSel1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = CyberCyan.copy(alpha = 0.2f)
                    )
                )

                // Section 2: Couple Montage
                val isSel2 = currentSection == 2
                NavigationBarItem(
                    selected = isSel2,
                    onClick = { viewModel.setSection(2) },
                    modifier = Modifier.testTag("nav_montage_tab"),
                    icon = {
                        Icon(
                            imageVector = if (isSel2) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Couple Montage",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Couple Montage",
                            fontSize = 11.sp,
                            fontWeight = if (isSel2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RomanticRose,
                        selectedTextColor = RomanticRose,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = RomanticRose.copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StudioDarkBg)
                .padding(innerPadding)
        ) {
            when (currentSection) {
                0 -> {
                    TextToVideoScreen(
                        currentStoryboard = currentStoryboard,
                        isGenerating = isVideoGenerating,
                        onGenerate = { prompt, is3D, style ->
                            viewModel.generateVideo(prompt, is3D, style)
                        },
                        onSaveProject = { sb ->
                            viewModel.saveVideoProject(sb)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Video saved to Taruni Library!")
                            }
                        },
                        onShareText = shareContent
                    )
                }
                1 -> {
                    SongCreatorScreen(
                        currentSong = currentSong,
                        isGenerating = isSongGenerating,
                        isPlayingAudio = isPlayingAudio,
                        audioAmplitude = audioAmplitude,
                        onGenerateSong = { theme, genre, bpm, vocal ->
                            viewModel.generateSong(theme, genre, bpm, vocal)
                        },
                        onTogglePlayAudio = { viewModel.togglePlayAudio() },
                        onSaveSong = { song ->
                            viewModel.saveSongProject(song)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Song saved to Taruni Library!")
                            }
                        },
                        onShareSong = shareContent
                    )
                }
                2 -> {
                    CoupleMontageScreen(
                        currentScript = currentCoupleScript,
                        isGenerating = isMontageGenerating,
                        isPlayingMontage = isPlayingMontage,
                        onGenerateMontage = { p1, p2, milestone, theme ->
                            viewModel.generateCoupleMontage(p1, p2, milestone, theme)
                        },
                        onTogglePlayMontage = { viewModel.togglePlayMontage() },
                        onSaveMontage = { script ->
                            viewModel.saveMontageProject(script)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Couple montage saved to Taruni Library!")
                            }
                        },
                        onShareMontage = shareContent
                    )
                }
            }
        }

        // Library Dialog
        if (showLibraryDialog) {
            ProjectsLibraryDialog(
                projects = savedProjects,
                onDismiss = { showLibraryDialog = false },
                onDeleteProject = { id -> viewModel.deleteProject(id) },
                onToggleFavorite = { id, isFav -> viewModel.toggleFavorite(id, isFav) },
                onSelectProject = { proj ->
                    showLibraryDialog = false
                    when {
                        proj.type.startsWith("VIDEO") -> viewModel.setSection(0)
                        proj.type == "SONG" -> viewModel.setSection(1)
                        proj.type == "COUPLE_MONTAGE" -> viewModel.setSection(2)
                    }
                    Toast.makeText(context, "Loaded: ${proj.title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AboutTaruniDialog(onDismiss = { showAboutDialog = false })
        }
    }
}
