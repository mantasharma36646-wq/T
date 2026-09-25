package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class VideoScene(
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val cameraAngle: String,
    val lightingMood: String,
    val motionType: String,
    val audioAtmosphere: String
)

data class VideoStoryboard(
    val title: String,
    val logline: String,
    val dimension: String, // "2D" or "3D"
    val visualStyle: String,
    val scenes: List<VideoScene>
)

data class LyricSection(
    val label: String, // "Verse 1", "Chorus", "Verse 2", "Bridge", "Outro"
    val chords: String, // e.g. "[C] [Am] [F] [G]"
    val lines: List<String>
)

data class SongComposition(
    val title: String,
    val genre: String,
    val bpm: Int,
    val musicalKey: String,
    val vocalStyle: String,
    val chordProgression: List<String>,
    val sections: List<LyricSection>
)

data class CoupleMontageScript(
    val title: String,
    val coupleNames: String,
    val milestoneText: String,
    val loveQuote: String,
    val chapters: List<CoupleChapter>
)

data class CoupleChapter(
    val title: String,
    val subtitle: String,
    val loveNote: String,
    val visualEffect: String
)

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateVideoStoryboard(
        userPrompt: String,
        is3D: Boolean,
        styleName: String
    ): VideoStoryboard = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackVideoStoryboard(userPrompt, is3D, styleName)
        }

        val systemPrompt = """
            You are Taruni Studio's expert film director. Transform the user's text prompt into a cinematic ${if (is3D) "3D" else "2D"} video storyboard breakdown.
            Visual style: $styleName.
            Output strict JSON with this exact format:
            {
              "title": "Short creative title",
              "logline": "One sentence summary",
              "scenes": [
                {
                  "sceneNumber": 1,
                  "title": "Establishing Shot",
                  "visualPrompt": "Detailed visual description",
                  "cameraAngle": "Wide Crane shot / Aerial Drone / Extreme Close-up",
                  "lightingMood": "Golden hour glow / Neon backlit / Soft atmospheric",
                  "motionType": "Slow pan right / Dolly forward / Dynamic 360 orbit",
                  "audioAtmosphere": "Rain ambience and low synth drone"
                },
                {
                  "sceneNumber": 2,
                  "title": "Climax Action",
                  "visualPrompt": "Action or emotional core moment",
                  "cameraAngle": "Tracking shot / Low angle perspective",
                  "lightingMood": "High-contrast rim lighting / Sparkling bokeh",
                  "motionType": "Fast zoom / Parallax glide",
                  "audioAtmosphere": "Crescendo swell"
                },
                {
                  "sceneNumber": 3,
                  "title": "Resolving Horizon",
                  "visualPrompt": "Poetic closing shot",
                  "cameraAngle": "Aerial pulling back",
                  "lightingMood": "Dusk twilight / Warm horizon wash",
                  "motionType": "Slow zoom out",
                  "audioAtmosphere": "Echoing piano resolution"
                }
              ]
            }
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", "$systemPrompt\n\nUser text prompt: $userPrompt")
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiService", "API call failed with ${response.code}: $responseBody")
                return@withContext fallbackVideoStoryboard(userPrompt, is3D, styleName)
            }

            val responseJson = JSONObject(responseBody)
            val candidateText = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val parsed = JSONObject(candidateText)
            val scenesArray = parsed.getJSONArray("scenes")
            val sceneList = mutableListOf<VideoScene>()
            for (i in 0 until scenesArray.length()) {
                val sc = scenesArray.getJSONObject(i)
                sceneList.add(
                    VideoScene(
                        sceneNumber = sc.optInt("sceneNumber", i + 1),
                        title = sc.optString("title", "Scene ${i + 1}"),
                        visualPrompt = sc.optString("visualPrompt", ""),
                        cameraAngle = sc.optString("cameraAngle", "Cinematic Wide"),
                        lightingMood = sc.optString("lightingMood", "Dynamic Lighting"),
                        motionType = sc.optString("motionType", "Smooth Pan"),
                        audioAtmosphere = sc.optString("audioAtmosphere", "Atmospheric pads")
                    )
                )
            }

            VideoStoryboard(
                title = parsed.optString("title", "Taruni Creation"),
                logline = parsed.optString("logline", userPrompt),
                dimension = if (is3D) "3D" else "2D",
                visualStyle = styleName,
                scenes = sceneList
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Error calling Gemini", e)
            fallbackVideoStoryboard(userPrompt, is3D, styleName)
        }
    }

    suspend fun generateSongLyrics(
        theme: String,
        genreName: String,
        bpm: Int,
        vocalStyle: String
    ): SongComposition = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackSongComposition(theme, genreName, bpm, vocalStyle)
        }

        val prompt = """
            You are Taruni's master songwriter and music producer. Generate a complete song with chords and lyrics based on:
            Theme: "$theme"
            Genre: $genreName
            Tempo: $bpm BPM
            Vocal Style: $vocalStyle

            Output strict JSON in this format:
            {
              "title": "Catchy Song Title",
              "musicalKey": "C Major",
              "chordProgression": ["C", "G", "Am", "F"],
              "sections": [
                {
                  "label": "Verse 1",
                  "chords": "[C] [G] [Am] [F]",
                  "lines": [
                    "First poetic line of verse one",
                    "Second resonant line continuing rhyme",
                    "Third line building emotional momentum",
                    "Fourth line resolving before the lift"
                  ]
                },
                {
                  "label": "Chorus",
                  "chords": "[F] [G] [Em] [Am]",
                  "lines": [
                    "High energy memorable chorus hook",
                    "Singalong line with powerful emotion",
                    "Repetition or anthem climax",
                    "Resolution into the groove"
                  ]
                },
                {
                  "label": "Verse 2",
                  "chords": "[C] [G] [Am] [F]",
                  "lines": [
                    "Deeper imagery and story progression",
                    "Setting the evening scene in vivid detail",
                    "Catching the rhythm in our hearts",
                    "Ready to take flight once again"
                  ]
                },
                {
                  "label": "Bridge",
                  "chords": "[Dm] [Em] [F] [G]",
                  "lines": [
                    "Harmonic twist that changes the perspective",
                    "Reaching the emotional peak of the melody"
                  ]
                },
                {
                  "label": "Outro",
                  "chords": "[C] [Am] [F] [C]",
                  "lines": [
                    "Gentle fading whisper of the main theme",
                    "Final note hanging in the quiet air"
                  ]
                }
              ]
            }
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", prompt)
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.75)
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext fallbackSongComposition(theme, genreName, bpm, vocalStyle)
            }

            val responseJson = JSONObject(responseBody)
            val candidateText = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val parsed = JSONObject(candidateText)
            val chordArr = parsed.optJSONArray("chordProgression")
            val chords = mutableListOf<String>()
            if (chordArr != null) {
                for (i in 0 until chordArr.length()) chords.add(chordArr.getString(i))
            } else {
                chords.addAll(listOf("C", "G", "Am", "F"))
            }

            val secArr = parsed.getJSONArray("sections")
            val sections = mutableListOf<LyricSection>()
            for (i in 0 until secArr.length()) {
                val s = secArr.getJSONObject(i)
                val linesArr = s.getJSONArray("lines")
                val lines = mutableListOf<String>()
                for (j in 0 until linesArr.length()) lines.add(linesArr.getString(j))
                sections.add(
                    LyricSection(
                        label = s.optString("label", "Section"),
                        chords = s.optString("chords", "[C] [G] [Am] [F]"),
                        lines = lines
                    )
                )
            }

            SongComposition(
                title = parsed.optString("title", "Harmonies of the Night"),
                genre = genreName,
                bpm = bpm,
                musicalKey = parsed.optString("musicalKey", "C Major"),
                vocalStyle = vocalStyle,
                chordProgression = chords,
                sections = sections
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Song generation error", e)
            fallbackSongComposition(theme, genreName, bpm, vocalStyle)
        }
    }

    suspend fun generateCoupleMontageStory(
        partner1: String,
        partner2: String,
        milestone: String,
        themeName: String
    ): CoupleMontageScript = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackCoupleMontage(partner1, partner2, milestone, themeName)
        }

        val prompt = """
            Create an auto-edit couple video & montage script for $partner1 & $partner2.
            Occasion/Milestone: $milestone
            Montage Style: $themeName
            
            Return JSON:
            {
              "title": "Our Unwritten Story",
              "loveQuote": "Poetic 2-sentence love quote celebrating their journey",
              "chapters": [
                {
                  "title": "First Chapter",
                  "subtitle": "Where sparks first flew",
                  "loveNote": "Short tender caption for photo/video overlay",
                  "visualEffect": "Heart Dissolve & Golden Glow"
                },
                {
                  "title": "Adventures Together",
                  "subtitle": "Laughing across every mile",
                  "loveNote": "Every sunset with you feels like a dream",
                  "visualEffect": "Ken Burns Pan & Light Flare"
                },
                {
                  "title": "Little Things",
                  "subtitle": "Quiet mornings and warm coffee",
                  "loveNote": "You are my safest harbor in this crowded world",
                  "visualEffect": "Soft Bokeh Blur Transition"
                },
                {
                  "title": "Forever & Beyond",
                  "subtitle": "To a lifetime of holding hands",
                  "loveNote": "I loved you yesterday, love you still, always have, always will",
                  "visualEffect": "Heart Burst Climax"
                }
              ]
            }
        """.trimIndent()

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", prompt)
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext fallbackCoupleMontage(partner1, partner2, milestone, themeName)
            }

            val responseJson = JSONObject(responseBody)
            val candidateText = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val parsed = JSONObject(candidateText)
            val chapArr = parsed.getJSONArray("chapters")
            val chapters = mutableListOf<CoupleChapter>()
            for (i in 0 until chapArr.length()) {
                val c = chapArr.getJSONObject(i)
                chapters.add(
                    CoupleChapter(
                        title = c.optString("title", "Chapter ${i + 1}"),
                        subtitle = c.optString("subtitle", ""),
                        loveNote = c.optString("loveNote", ""),
                        visualEffect = c.optString("visualEffect", "Golden Glow")
                    )
                )
            }

            CoupleMontageScript(
                title = parsed.optString("title", "$partner1 & $partner2"),
                coupleNames = "$partner1 & $partner2",
                milestoneText = milestone,
                loveQuote = parsed.optString("loveQuote", "Every love story is beautiful, but ours is my favorite."),
                chapters = chapters
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Couple montage generation error", e)
            fallbackCoupleMontage(partner1, partner2, milestone, themeName)
        }
    }

    private fun fallbackVideoStoryboard(prompt: String, is3D: Boolean, style: String): VideoStoryboard {
        val dim = if (is3D) "3D" else "2D"
        val cleanTitle = if (prompt.length > 25) prompt.take(25) + "..." else prompt.ifBlank { "Celestial Dreams" }
        return VideoStoryboard(
            title = cleanTitle,
            logline = if (prompt.isNotBlank()) prompt else "An ethereal journey through light and shadow.",
            dimension = dim,
            visualStyle = style,
            scenes = listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Opening Horizon",
                    visualPrompt = "Establishing wide canvas of $prompt with atmospheric depth, ambient particle glow and cinematic volumetric fog.",
                    cameraAngle = if (is3D) "Aerial 3D Orbit Shot" else "Panoramic 2D Crane Pan",
                    lightingMood = "Golden Hour Rim Glow & Bioluminescence",
                    motionType = "Slow Dolly Inward with Depth of Field",
                    audioAtmosphere = "Gentle wind chimes and low frequency warm pad"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "The Climax Movement",
                    visualPrompt = "Dynamic focal subject in motion reflecting stylized lighting, neon sparks flying across the frame.",
                    cameraAngle = if (is3D) "Low Angle Tracking Shot" else "Dynamic Anime Motion Glide",
                    lightingMood = "High Voltage Cyberpunk Accents",
                    motionType = "Parallax Glide & Camera Tilt",
                    audioAtmosphere = "Heartbeat pulse with rising melodic strings"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "Sunset Resolution",
                    visualPrompt = "Wide cinematic pull back, revealing the vast breathtaking landscape as twilight colors bathe the horizon.",
                    cameraAngle = "Slow Ascending Crane Shot",
                    lightingMood = "Twilight Violet & Warm Rose Glow",
                    motionType = "Gentle Zoom Out with Lens Flare",
                    audioAtmosphere = "Resonant acoustic guitar outro"
                )
            )
        )
    }

    private fun fallbackSongComposition(
        theme: String,
        genre: String,
        bpm: Int,
        vocalStyle: String
    ): SongComposition {
        val cleanTheme = theme.ifBlank { "Late Night Memories" }
        return SongComposition(
            title = "Midnight Echoes",
            genre = genre,
            bpm = bpm,
            musicalKey = "A Minor",
            vocalStyle = vocalStyle,
            chordProgression = listOf("Am", "F", "C", "G"),
            sections = listOf(
                LyricSection(
                    label = "Verse 1",
                    chords = "[Am] [F] [C] [G]",
                    lines = listOf(
                        "The city falls asleep under neon rain",
                        "Watching headlights wash away the quiet pain",
                        "Holding onto every word you whispered soft and low",
                        "Where the shadows end is where our memories go"
                    )
                ),
                LyricSection(
                    label = "Chorus",
                    chords = "[F] [G] [Am] [Em]",
                    lines = listOf(
                        "And oh, we are burning like constellations in the sky",
                        "Never asking where we're going or why",
                        "Take my hand before the morning takes the night",
                        "Everything is golden in this starlight"
                    )
                ),
                LyricSection(
                    label = "Verse 2",
                    chords = "[Am] [F] [C] [G]",
                    lines = listOf(
                        "Radio is playing songs from years ago",
                        "Driving down the coastal road watching colors glow",
                        "Your laughter is the sweetest melody I've known",
                        "With you beside me, I am never on my own"
                    )
                ),
                LyricSection(
                    label = "Bridge",
                    chords = "[Dm] [Em] [F] [G]",
                    lines = listOf(
                        "Let the thunder roll, let the river sway",
                        "There is nothing in this world that can pull our love away"
                    )
                ),
                LyricSection(
                    label = "Outro",
                    chords = "[Am] [F] [C] [Am]",
                    lines = listOf(
                        "Midnight echoes fading in the breeze...",
                        "Just you and me under the evergreen trees"
                    )
                )
            )
        )
    }

    private fun fallbackCoupleMontage(
        partner1: String,
        partner2: String,
        milestone: String,
        theme: String
    ): CoupleMontageScript {
        val p1 = partner1.ifBlank { "Aarav" }
        val p2 = partner2.ifBlank { "Taruni" }
        val m = milestone.ifBlank { "Happy Anniversary" }
        return CoupleMontageScript(
            title = "$p1 & $p2",
            coupleNames = "$p1 & $p2",
            milestoneText = m,
            loveQuote = "Whatever our souls are made of, yours and mine are one and the same.",
            chapters = listOf(
                CoupleChapter(
                    title = "Where It All Began",
                    subtitle = "The first hello",
                    loveNote = "That single smile that quietly changed my entire universe.",
                    visualEffect = "Heart Dissolve & Rose Flare"
                ),
                CoupleChapter(
                    title = "Chasing Sunsets",
                    subtitle = "Our favorite escapes",
                    loveNote = "Every road trip with you is an adventure I never want to end.",
                    visualEffect = "Ken Burns Pan & Light Leak"
                ),
                CoupleChapter(
                    title = "The Quiet Moments",
                    subtitle = "Laughter and cozy mornings",
                    loveNote = "You are my favorite place to go when my mind searches for peace.",
                    visualEffect = "Soft Bokeh Glow"
                ),
                CoupleChapter(
                    title = "Forever & Always",
                    subtitle = "To all our tomorrows",
                    loveNote = "I loved you then, I love you still, always have, always will.",
                    visualEffect = "Heart Burst Climax"
                )
            )
        )
    }
}
