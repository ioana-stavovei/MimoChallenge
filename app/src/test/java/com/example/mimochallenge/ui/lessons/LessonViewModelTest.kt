package com.example.mimochallenge.ui.lessons

import com.example.mimochallenge.data.model.ContentBlock
import com.example.mimochallenge.data.model.InputRange
import com.example.mimochallenge.data.model.Lesson
import com.example.mimochallenge.data.model.LessonCompletion
import com.example.mimochallenge.data.repository.LessonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    // 1. Setup a test dispatcher for Coroutines
    private val testDispatcher = StandardTestDispatcher()

    // 2. Mock the repository
    private val repository: LessonRepository = mock()

    private lateinit var viewModel: LessonViewModel

    @Before
    fun setup() {
        // Set the Main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = LessonViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset Main dispatcher to original
        Dispatchers.resetMain()
    }

    @Test
    fun `when LoadLessons is called, it updates state with lessons`() = runTest {
        // Given
        val mockLessons = listOf(
            Lesson(id = 1, content = listOf(ContentBlock("#FFFFFF", "var ")), input = null)
        )
        // Updated to return Result.success
        whenever(repository.fetchLessons()).thenReturn(Result.success(mockLessons))

        // When
        viewModel.handleIntent(LessonIntent.LoadLessons)
        advanceUntilIdle() // Wait for coroutine to finish

        // Then
        val state = viewModel.state.value
        assertEquals(mockLessons, state.lessons)
        assertFalse(state.isLoading)
    }

    @Test
    fun `when fetchLessons fails, it sets isError state`() = runTest {
        // Given
        whenever(repository.fetchLessons()).thenReturn(Result.failure(Exception("Network Error")))

        // When
        viewModel.handleIntent(LessonIntent.LoadLessons)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.isError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `when correct input is submitted, it saves completion and moves to next`() = runTest {
        // Given: Lesson 7 scenario (input "number" is index 4 to 10)
        val lesson = Lesson(
            id = 7,
            content = listOf(ContentBlock("#FFFFFF", "var number = 1")),
            input = InputRange(4, 10)
        )
        // Providing two lessons ensures it moves to the next index instead of finishing
        val mockLessons = listOf(lesson, lesson.copy(id = 8))
        whenever(repository.fetchLessons()).thenReturn(Result.success(mockLessons))

        viewModel.handleIntent(LessonIntent.LoadLessons)
        advanceUntilIdle()

        // When
        viewModel.handleIntent(LessonIntent.OnInputChanged("number"))
        viewModel.handleIntent(LessonIntent.OnNextClicked)
        advanceUntilIdle()

        // Then
        verify(repository).saveCompletion(any()) // Check DB write
        assertEquals(1, viewModel.state.value.currentIndex) // Moved to next
        assertFalse(viewModel.state.value.isError)
    }

    @Test
    fun `when last lesson is completed, it fetches all completions and finishes`() = runTest {
        // Given: Only one lesson in the list
        val lesson = Lesson(id = 7, content = listOf(ContentBlock("#FFFFFF", "End")), input = null)
        val mockHistory = listOf(LessonCompletion(7, 1000, 2000))

        whenever(repository.fetchLessons()).thenReturn(Result.success(listOf(lesson)))
        whenever(repository.getAllCompletions()).thenReturn(mockHistory)

        viewModel.handleIntent(LessonIntent.LoadLessons)
        advanceUntilIdle()

        // When
        viewModel.handleIntent(LessonIntent.OnNextClicked)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isFinished)
        assertEquals(mockHistory, viewModel.state.value.allCompletions)
        verify(repository).getAllCompletions() // Verify history was fetched for the logs
    }

    @Test
    fun `when wrong input is submitted, it sets error state`() = runTest {
        // Given
        val lesson = Lesson(
            id = 7,
            content = listOf(ContentBlock("#FFFFFF", "var number = 1")),
            input = InputRange(4, 10)
        )
        whenever(repository.fetchLessons()).thenReturn(Result.success(listOf(lesson)))

        viewModel.handleIntent(LessonIntent.LoadLessons)
        advanceUntilIdle()

        // When
        viewModel.handleIntent(LessonIntent.OnInputChanged("wrong_answer"))
        viewModel.handleIntent(LessonIntent.OnNextClicked)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isError)
        assertEquals(0, viewModel.state.value.currentIndex) // Stayed on same lesson
        verify(repository, never()).saveCompletion(any()) // No DB write
    }
}