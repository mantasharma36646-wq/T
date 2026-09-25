package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaruniDao {
    @Query("SELECT * FROM saved_projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<SavedProject>>

    @Query("SELECT * FROM saved_projects WHERE type = :type ORDER BY timestamp DESC")
    fun getProjectsByType(type: String): Flow<List<SavedProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: SavedProject): Long

    @Query("DELETE FROM saved_projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Query("UPDATE saved_projects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
}
