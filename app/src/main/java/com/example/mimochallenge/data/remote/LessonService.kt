package com.example.mimochallenge.data.remote

import com.example.mimochallenge.data.model.LessonResponse
import retrofit2.http.GET

interface LessonService {
    @GET("api/lessons")
    suspend fun getLessons(): LessonResponse
}