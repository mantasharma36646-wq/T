package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class TaruniAudioSynthesizer {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0f)
    val currentAmplitude = _currentAmplitude.asStateFlow()

    private val _currentBeat = MutableStateFlow(0)
    val currentBeat = _currentBeat.asStateFlow()

    private val _currentMeasure = MutableStateFlow(0)
    val currentMeasure = _currentMeasure.asStateFlow()

    // Note frequencies in Hz
    private val noteFreqs = mapOf(
        "C3" to 130.81, "D3" to 146.83, "E3" to 164.81, "F3" to 174.61, "G3" to 196.00, "A3" to 220.00, "B3" to 246.94,
        "C4" to 261.63, "D4" to 293.66, "E4" to 329.63, "F4" to 349.23, "G4" to 392.00, "A4" to 440.00, "B4" to 493.88,
        "C5" to 523.25, "D5" to 587.33, "E5" to 659.25, "F5" to 698.46, "G5" to 783.99, "A5" to 880.00
    )

    // Chord definitions: (Bass Note, Chord Triad Notes, Melody Notes)
    private val chordMap = mapOf(
        "C" to Triple("C3", listOf("C4", "E4", "G4"), listOf("E4", "G4", "C5", "G4")),
        "G" to Triple("G3", listOf("G4", "B4", "D5"), listOf("D4", "G4", "B4", "D5")),
        "Am" to Triple("A3", listOf("A4", "C5", "E5"), listOf("C4", "E4", "A4", "C5")),
        "F" to Triple("F3", listOf("F4", "A4", "C5"), listOf("C4", "F4", "A4", "C5")),
        "Dm" to Triple("D3", listOf("D4", "F4", "A4"), listOf("D4", "F4", "A4", "D5")),
        "Em" to Triple("E3", listOf("E4", "G4", "B4"), listOf("E4", "G4", "B4", "E5"))
    )

    fun startPlayback(
        scope: CoroutineScope,
        bpm: Int = 95,
        chordProgression: List<String> = listOf("C", "G", "Am", "F"),
        style: String = "POP" // "POP", "LOFI", "ROMANTIC", "SYNTHWAVE"
    ) {
        stop()

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        _isPlaying.value = true

        playbackJob = scope.launch(Dispatchers.Default) {
            val beatDurationSec = 60.0 / bpm.coerceIn(60, 160)
            val stepDurationSec = beatDurationSec / 2.0 // Eighth-note steps
            val stepSamples = (sampleRate * stepDurationSec).toInt()
            val chords = if (chordProgression.isNotEmpty()) chordProgression else listOf("C", "G", "Am", "F")

            var chordIndex = 0
            var measure = 0

            try {
                while (isActive) {
                    val currentChordName = chords[chordIndex % chords.size]
                    val chordData = chordMap[currentChordName] ?: chordMap["C"]!!

                    val (bassNote, triad, melodyNotes) = chordData
                    val bassFreq = noteFreqs[bassNote] ?: 130.81
                    val triadFreqs = triad.mapNotNull { noteFreqs[it] }

                    _currentMeasure.value = measure

                    // 8 eighth-note steps per measure (4/4 time signature)
                    for (step in 0 until 8) {
                        if (!isActive) break

                        _currentBeat.value = step / 2

                        val melodyFreq = noteFreqs[melodyNotes[step % melodyNotes.size]] ?: 440.0
                        val isBassHit = (step == 0 || step == 4)

                        val pcmBuffer = ShortArray(stepSamples)
                        var maxAmp = 0f

                        for (i in 0 until stepSamples) {
                            val t = i.toDouble() / sampleRate
                            val progress = i.toDouble() / stepSamples

                            // ADSR envelope: Fast attack (0.02s), exponential decay
                            val attack = (progress * 15.0).coerceAtMost(1.0)
                            val decay = exp(-progress * (if (style == "ROMANTIC") 1.8 else 3.2))
                            val env = attack * decay

                            // 1. Melody Voice (Warm Triangle/Sine)
                            val melPhase = 2.0 * PI * melodyFreq * t
                            val melSample = sin(melPhase) * 0.45 + sin(melPhase * 2.0) * 0.12

                            // 2. Chords Pad (Harmonic sum)
                            var chordSample = 0.0
                            triadFreqs.forEach { f ->
                                val p = 2.0 * PI * f * t
                                chordSample += sin(p) * 0.18 + sin(p * 2.0) * 0.04
                            }

                            // 3. Bass Pulse
                            var bassSample = 0.0
                            if (isBassHit) {
                                val bassEnv = exp(-progress * 2.2)
                                val bp = 2.0 * PI * bassFreq * t
                                bassSample = (sin(bp) + sin(bp * 0.5) * 0.5) * 0.35 * bassEnv
                            }

                            // Master blend
                            val mixed = (melSample * 0.5 + chordSample * 0.3 + bassSample * 0.35) * env
                            val clamped = mixed.coerceIn(-0.95, 0.95)
                            val sampleShort = (clamped * 32767).toInt().toShort()
                            pcmBuffer[i] = sampleShort

                            val absVal = kotlin.math.abs(clamped.toFloat())
                            if (absVal > maxAmp) maxAmp = absVal
                        }

                        _currentAmplitude.value = maxAmp
                        audioTrack?.write(pcmBuffer, 0, pcmBuffer.size)
                    }

                    chordIndex++
                    measure++
                }
            } catch (e: Exception) {
                // Playback stopped or cancelled
            } finally {
                _isPlaying.value = false
                _currentAmplitude.value = 0f
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore track release errors
        }
        audioTrack = null
        _isPlaying.value = false
        _currentAmplitude.value = 0f
    }
}
