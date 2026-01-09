package com.example.mimochallenge.data.repository

import com.example.mimochallenge.data.local.LessonDao
import com.example.mimochallenge.data.model.Lesson
import com.example.mimochallenge.data.model.LessonCompletion
import com.example.mimochallenge.data.remote.LessonService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepository @Inject constructor(
    private val service: LessonService,
    private val dao: LessonDao
) {
    //  the return type is for handling the success/failure explicitly
    suspend fun fetchLessons(): Result<List<Lesson>> {
        return try {
            val response = service.getLessons()
            Result.success(response.lessons)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveCompletion(completion: LessonCompletion) {
        dao.insertCompletion(completion)
    }

    // Retrieve all saved events from Room
    suspend fun getAllCompletions(): List<LessonCompletion> = dao.getAllCompletions()
}