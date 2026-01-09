package com.example.mimochallenge.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class LessonResponse(
    @SerializedName("lessons") val lessons: List<Lesson>
)

data class Lesson(
    val id: Int,
    val content: List<ContentBlock>,
    val input: InputRange? = null
)

data class ContentBlock(
    val color: String,
    val text: String
)

data class InputRange(
    val startIndex: Int,
    val endIndex: Int
)

@Entity(tableName = "lesson_completions")
data class LessonCompletion(
    @PrimaryKey val id: Int,
    val startTime: Long,
    val endTime: Long
)