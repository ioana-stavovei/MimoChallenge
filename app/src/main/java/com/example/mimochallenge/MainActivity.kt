package com.example.mimochallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mimochallenge.ui.lessons.LessonIntent
import com.example.mimochallenge.ui.lessons.LessonScreen
import com.example.mimochallenge.ui.lessons.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LessonViewModel = hiltViewModel()
            LaunchedEffect(Unit) { viewModel.handleIntent(LessonIntent.LoadLessons) }
            LessonScreen(viewModel)
        }
    }
}