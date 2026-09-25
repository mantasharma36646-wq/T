package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TaruniAudioSynthesizer
import com.example.data.ai.CoupleMontageScript
import com.example.data.ai.GeminiService
import com.example.data.ai.SongComposition
import com.example.data.ai.VideoStoryboard
import com.example.data.local.ProjectRepository
import com.example.data.local.SavedProject
import com.example.data.local.TaruniDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaruniViewModel(application: Application) : AndroidViewModel(application) {
    private val geminiService = GeminiService()
    private val audioSynthesizer = TaruniAudioSynthesizer()
    private val repository: ProjectRepository

    init {
        val db = TaruniDatabase.getInstance(application)
        repository = ProjectRepository(db.taruniDao())
    }

    val savedProjects: StateFlow<List<SavedProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active bottom navigation section (0 = Video, 1 = Songs, 2 = Couple Montage)
    private val _currentSection = MutableStateFlow(0)
    val currentSection = _currentSection.asStateFlow()

    fun setSection(section: Int) {
        _currentSection.value = section
    }

    // --- Section 1: Text-to-Video ---
    private val _currentStoryboard = MutableStateFlow<VideoStoryboard?>(null)
    val currentStoryboard = _currentStoryboard.asStateFlow()

    private val _isVideoGenerating = MutableStateFlow(false)
    val isVideoGenerating = _isVideoGenerating.asStateFlow()

    fun generateVideo(prompt: String, is3D: Boolean, styleName: String) {
        viewModelScope.launch {
            _isVideoGenerating.value = true
            try {
                val result = geminiService.generateVideoStoryboard(prompt, is3D, styleName)
                _currentStoryboard.value = result
            } finally {
                _isVideoGenerating.value = false
            }
        }
    }

    fun saveVideoProject(storyboard: VideoStoryboard) {
        viewModelScope.launch {
            repository.saveProject(
                SavedProject(
                    type = if (storyboard.dimension == "3D") "VIDEO_3D" else "VIDEO_2D",
                    title = storyboard.title,
                    prompt = storyboard.logline,
                    style = storyboard.visualStyle,
                    subData = storyboard.scenes.joinToString(" || ") { "${it.title}: ${it.visualPrompt}" }
                )
            )
        }
    }

    // --- Section 2: AI Songs ---
    private val _currentSong = MutableStateFlow<SongComposition?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isSongGenerating = MutableStateFlow(false)
    val isSongGenerating = _isSongGenerating.asStateFlow()

    val isPlayingAudio: StateFlow<Boolean> = audioSynthesizer.isPlaying
    val audioAmplitude: StateFlow<Float> = audioSynthesizer.currentAmplitude

    fun generateSong(theme: String, genre: String, bpm: Int, vocalStyle: String) {
        viewModelScope.launch {
            _isSongGenerating.value = true
            try {
                val result = geminiService.generateSongLyrics(theme, genre, bpm, vocalStyle)
                _currentSong.value = result
                // Auto play synth preview of the newly composed song chords
                audioSynthesizer.startPlayback(
                    scope = viewModelScope,
                    bpm = result.bpm,
                    chordProgression = result.chordProgression,
                    style = if (result.genre.contains("Romantic", ignoreCase = true)) "ROMANTIC" else "POP"
                )
            } finally {
                _isSongGenerating.value = false
            }
        }
    }

    fun togglePlayAudio() {
        if (isPlayingAudio.value) {
            audioSynthesizer.stop()
        } else {
            val song = _currentSong.value
            val chords = song?.chordProgression ?: listOf("C", "G", "Am", "F")
            val bpm = song?.bpm ?: 95
            audioSynthesizer.startPlayback(
                scope = viewModelScope,
                bpm = bpm,
                chordProgression = chords,
                style = if (song?.genre?.contains("Romantic", ignoreCase = true) == true) "ROMANTIC" else "POP"
            )
        }
    }

    fun saveSongProject(song: SongComposition) {
        viewModelScope.launch {
            repository.saveProject(
                SavedProject(
                    type = "SONG",
                    title = song.title,
                    prompt = "${song.genre} • ${song.bpm} BPM",
                    style = song.vocalStyle,
                    subData = song.sections.joinToString("\n") { s -> "[${s.label}]\n" + s.lines.joinToString("\n") }
                )
            )
        }
    }

    // --- Section 3: Couple Video & Montage ---
    private val _currentCoupleScript = MutableStateFlow<CoupleMontageScript?>(null)
    val currentCoupleScript = _currentCoupleScript.asStateFlow()

    private val _isMontageGenerating = MutableStateFlow(false)
    val isMontageGenerating = _isMontageGenerating.asStateFlow()

    private val _isPlayingMontage = MutableStateFlow(true)
    val isPlayingMontage = _isPlayingMontage.asStateFlow()

    fun generateCoupleMontage(partner1: String, partner2: String, milestone: String, themeName: String) {
        viewModelScope.launch {
            _isMontageGenerating.value = true
            try {
                val result = geminiService.generateCoupleMontageStory(partner1, partner2, milestone, themeName)
                _currentCoupleScript.value = result
                _isPlayingMontage.value = true
                // Play romantic accompaniment
                audioSynthesizer.startPlayback(
                    scope = viewModelScope,
                    bpm = 78,
                    chordProgression = listOf("C", "Am", "F", "G"),
                    style = "ROMANTIC"
                )
            } finally {
                _isMontageGenerating.value = false
            }
        }
    }

    fun togglePlayMontage() {
        _isPlayingMontage.value = !_isPlayingMontage.value
        if (_isPlayingMontage.value) {
            audioSynthesizer.startPlayback(
                scope = viewModelScope,
                bpm = 78,
                chordProgression = listOf("C", "Am", "F", "G"),
                style = "ROMANTIC"
            )
        } else {
            audioSynthesizer.stop()
        }
    }

    fun saveMontageProject(script: CoupleMontageScript) {
        viewModelScope.launch {
            repository.saveProject(
                SavedProject(
                    type = "COUPLE_MONTAGE",
                    title = script.title,
                    prompt = script.loveQuote,
                    style = script.milestoneText,
                    subData = script.chapters.joinToString(" || ") { "${it.title}: ${it.loveNote}" }
                )
            )
        }
    }

    // --- Database Operations ---
    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun toggleFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFav)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSynthesizer.stop()
    }
}
