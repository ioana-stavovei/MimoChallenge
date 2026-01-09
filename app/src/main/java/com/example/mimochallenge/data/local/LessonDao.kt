package com.example.mimochallenge.data.local

import androidx.room.*
import com.example.mimochallenge.data.model.LessonCompletion

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: LessonCompletion)

    @Query("SELECT * FROM lesson_completions")
    suspend fun getAllCompletions(): List<LessonCompletion>
}