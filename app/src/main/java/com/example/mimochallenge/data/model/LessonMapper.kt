package com.example.mimochallenge.data.model

object LessonMapper {
    /**
     * Extracts the exact string the user needs to type based on indices.
     */
    fun getExpectedAnswer(lesson: Lesson): String? {
        val range = lesson.input ?: return null
        val fullText = lesson.content.joinToString("") { it.text }

        return if (range.startIndex >= 0 && range.endIndex <= fullText.length) {
            fullText.substring(range.startIndex, range.endIndex)
        } else null
    }
}