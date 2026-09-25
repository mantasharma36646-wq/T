package com.example.data.ai

object PromptPresets {
    data class StyleOption(val id: String, val name: String, val badge: String, val description: String)
    data class PresetIdea(val title: String, val prompt: String, val styleId: String, val is3D: Boolean)

    val video2DStyles = listOf(
        StyleOption("anime_ghibli", "Anime Studio", "2D", "Hand-drawn warm anime watercolor aesthetic"),
        StyleOption("cyber_manga", "Cyber Manga", "2D", "High contrast comic line-art with neon accents"),
        StyleOption("cartoon_classic", "Retro Cartoon", "2D", "Golden era Disney & classic 90s animation"),
        StyleOption("flat_vector", "Vector Minimal", "2D", "Clean geometric motion graphics & vibrant silhouettes")
    )

    val video3DStyles = listOf(
        StyleOption("unreal_cinematic", "Unreal 5 Ultra", "3D", "Photorealistic raytraced 4K cinematic lighting"),
        StyleOption("pixar_3d", "Pixar Animation", "3D", "Charming 3D stylized character animation & subsurface glow"),
        StyleOption("cyber_cgi", "Cyberpunk CGI", "3D", "Blade Runner neo-Tokyo neon reflections & holographic rain"),
        StyleOption("claymation_3d", "Stop-Motion Clay", "3D", "Textured clay puppet stop-motion tactile realism")
    )

    val videoIdeas = listOf(
        PresetIdea(
            "Cyber Samurai in Rain",
            "A solitary neon samurai walking through rain-slicked alleys in Neo-Tokyo, reflecting holographic cherry blossoms and glowing billboards.",
            "cyber_cgi",
            true
        ),
        PresetIdea(
            "Floating Castle at Dawn",
            "A celestial floating fortress wrapped in pastel pink clouds, ancient waterfalls cascading into golden sunlight with flying airships.",
            "anime_ghibli",
            false
        ),
        PresetIdea(
            "Baby Robot & Sunflower",
            "A curious small rusted brass robot discovering a single blooming sunflower amidst abandoned overgrown ruins, cinematic shallow depth of field.",
            "pixar_3d",
            true
        ),
        PresetIdea(
            "Cosmic Voyage",
            "A sleek interstellar spacecraft warping through a swirling purple and cyan galaxy nebula with ringed planets in the horizon.",
            "unreal_cinematic",
            true
        ),
        PresetIdea(
            "Urban Cat Detective",
            "A stylish cat in a trench coat investigating under streetlamps on a foggy Parisian night, vintage jazz ambiance.",
            "cartoon_classic",
            false
        )
    )

    data class SongGenre(val id: String, val name: String, val defaultBpm: Int, val iconName: String)
    val songGenres = listOf(
        SongGenre("romantic_pop", "Romantic Pop", 95, "heart"),
        SongGenre("lofi_chill", "Lo-Fi Chillhop", 82, "coffee"),
        SongGenre("bollywood_acoustic", "Acoustic Melody", 100, "guitar"),
        SongGenre("synthwave", "Cyber Synthwave", 120, "retro"),
        SongGenre("indie_folk", "Indie Warmth", 88, "camp"),
        SongGenre("party_edm", "Upbeat Dance", 128, "fire")
    )

    val songThemes = listOf(
        "Stargazing on a summer rooftop with someone special",
        "Late night quiet drive thinking about unsaid words",
        "A monsoon romance in the city coffee shop",
        "Chasing dreams across neon bridges at midnight",
        "Waking up on a slow Sunday morning beside you"
    )

    data class MontageTheme(
        val id: String,
        val title: String,
        val subtitle: String,
        val soundtrack: String,
        val defaultQuote: String
    )

    val montageThemes = listOf(
        MontageTheme(
            "love_story",
            "Eternal Love Story",
            "Romantic warm golden glow, heart transitions & gentle acoustic waltz",
            "Lovers Waltz (Piano & Strings)",
            "In all the world, there is no heart for me like yours. In all the world, there is no love for you like mine."
        ),
        MontageTheme(
            "anniversary",
            "Milestones & Memories",
            "Chronological milestone chapters, memory polaroids & sweet captions",
            "Starlight Memories (Acoustic)",
            "Every second with you is a memory I want to keep forever."
        ),
        MontageTheme(
            "travel_vibe",
            "Wanderlust Together",
            "Fast rhythmic beat-sync cuts, adventure transitions & vibrant colors",
            "Sunset Highway (Lo-Fi Chill)",
            "With you, every destination feels like home."
        ),
        MontageTheme(
            "vintage_cinema",
            "Retro 8mm Film Romance",
            "Warm vintage grain, sepia light leaks & soft romantic jazz",
            "Midnight In Paris (Warm Chords)",
            "You are my favorite chapter in this beautiful story."
        )
    )

    data class SampleCoupleScene(
        val title: String,
        val location: String,
        val caption: String,
        val colorTint: Long
    )

    val sampleCoupleScenes = listOf(
        SampleCoupleScene("The Beginning", "Where we first met", "The day everything changed for the better ✨", 0xFFFDA4AF),
        SampleCoupleScene("Coffee & Laughter", "Corner Cafe", "Spilling coffee and talking until closing time ☕", 0xFFFDE68A),
        SampleCoupleScene("Sunset By The Shore", "Golden Sands", "Watching the sky catch fire while holding hands 🌅", 0xFFF472B6),
        SampleCoupleScene("Rainy City Walk", "Downtown Lights", "Dancing under one small umbrella in the storm ☔", 0xFF93C5FD),
        SampleCoupleScene("Forever & Always", "Our Happy Place", "To every yesterday, today, and all our tomorrows 💍", 0xFFC084FC)
    )
}
