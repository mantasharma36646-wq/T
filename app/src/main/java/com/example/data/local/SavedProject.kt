package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_projects")
data class SavedProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "VIDEO_2D", "VIDEO_3D", "SONG", "COUPLE_MONTAGE"
    val title: String,
    val prompt: String,
    val style: String,
    val subData: String, // JSON payload or text (e.g. lyrics with chords, scene breakdown, couple details)
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
