package com.example.mimochallenge.ui.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimochallenge.data.model.LessonCompletion
import com.example.mimochallenge.data.model.LessonMapper
import com.example.mimochallenge.data.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state.asStateFlow()

    fun handleIntent(intent: LessonIntent) {
        when (intent) {
            is LessonIntent.LoadLessons -> loadLessons()
            is LessonIntent.OnInputChanged -> {
                _state.update { it.copy(userInput = intent.text, isError = false) }
            }

            is LessonIntent.OnNextClicked -> validateAndNext()
        }
    }

    private fun loadLessons() {
        viewModelScope.launch {
            val result = repository.fetchLessons()

            result.onSuccess { fetchedLessons ->
                _state.update {
                    it.copy(
                        lessons = fetchedLessons,
                        isLoading = false,
                        startTime = System.currentTimeMillis()
                    )
                }
            }.onFailure { exception ->
                // Handle the error case (e.g., API failure or no internet)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true // This can trigger an error message in the UI
                    )
                }
            }
        }
    }

    private fun validateAndNext() {
        val currentState = _state.value
        val currentLesson = currentState.lessons.getOrNull(currentState.currentIndex) ?: return

        val expected = LessonMapper.getExpectedAnswer(currentLesson)

        // If no input is required, or input matches exactly
        if (expected == null || currentState.userInput == expected) {
            saveAndMoveToNext(currentLesson.id, currentState)
        } else {
            _state.update { it.copy(isError = true) }
        }
    }

    private fun saveAndMoveToNext(lessonId: Int, currentState: LessonState) {
        viewModelScope.launch {
            repository.saveCompletion(
                LessonCompletion(
                    id = lessonId,
                    startTime = currentState.startTime,
                    endTime = System.currentTimeMillis()
                )
            )

            val nextIndex = currentState.currentIndex + 1
            if (nextIndex < currentState.lessons.size) {
                _state.update {
                    it.copy(
                        currentIndex = nextIndex,
                        userInput = "",
                        isError = false,
                        startTime = System.currentTimeMillis()
                    )
                }
            } else {
                // Course Finished: Fetch all completions from DB before showing Done screen
                val completions = repository.getAllCompletions()
                _state.update { it.copy(isFinished = true, allCompletions = completions) }
            }
        }
    }
}