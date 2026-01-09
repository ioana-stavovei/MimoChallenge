package com.example.mimochallenge.ui.lessons

import com.example.mimochallenge.data.model.Lesson
import com.example.mimochallenge.data.model.LessonCompletion

data class LessonState(
    val lessons: List<Lesson> = emptyList(),
    val currentIndex: Int = 0,
    val userInput: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val startTime: Long = 0L,
    val allCompletions: List<LessonCompletion> = emptyList()
)

sealed class LessonIntent {
    object LoadLessons : LessonIntent()
    data class OnInputChanged(val text: String) : LessonIntent()
    object OnNextClicked : LessonIntent()
}