package com.example.mimochallenge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mimochallenge.data.model.LessonCompletion

@Database(
    entities = [LessonCompletion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
}