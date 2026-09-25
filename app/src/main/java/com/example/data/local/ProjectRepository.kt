package com.example.data.local

import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: TaruniDao) {
    val allProjects: Flow<List<SavedProject>> = dao.getAllProjects()

    fun getProjectsByType(type: String): Flow<List<SavedProject>> = dao.getProjectsByType(type)

    suspend fun saveProject(project: SavedProject): Long = dao.insertProject(project)

    suspend fun deleteProject(id: Long) = dao.deleteProject(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = dao.updateFavorite(id, isFavorite)
}
