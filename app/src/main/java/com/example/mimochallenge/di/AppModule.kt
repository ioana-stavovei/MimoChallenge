package com.example.mimochallenge.di

import android.content.Context
import androidx.room.Room
import com.example.mimochallenge.data.local.AppDatabase
import com.example.mimochallenge.data.local.LessonDao
import com.example.mimochallenge.data.remote.LessonService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mimo_challenge_db"
        ).build()
    }

    @Provides
    fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()

    @Provides
    @Singleton
    fun provideLessonService(): LessonService {
        return Retrofit.Builder()
            .baseUrl("https://mimochallenge.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LessonService::class.java)
    }
}