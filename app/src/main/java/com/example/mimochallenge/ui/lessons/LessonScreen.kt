package com.example.mimochallenge.ui.lessons

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.mimochallenge.ui.components.LessonContent
import com.example.mimochallenge.R
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun LessonScreen(viewModel: LessonViewModel) {
    val state by viewModel.state.collectAsState()
    val backgroundColor = Color(0xFF2C2C2C)
    val contentColor = Color.White

    // Konfetti state controller for the celebration effect
    val party = remember {
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb980ff),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
    }

    // Side effect to trigger Logcat logging once when finished
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            // Logs all lesson completion data to the Android Studio console
            Log.d("MimoChallenge", "--- COURSE COMPLETED ---")
            state.allCompletions.forEach { completion ->
                val duration = (completion.endTime - completion.startTime) / 1000
                Log.d("MimoChallenge", "Lesson ID: ${completion.id} | Time Spent: ${duration}s")
            }
            Log.d("MimoChallenge", "------------------------")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (state.lessons.isNotEmpty() && !state.isFinished) {
                val progressValue = remember(state.currentIndex, state.lessons.size) {
                    if (state.lessons.isNotEmpty()) {
                        state.currentIndex.toFloat() / state.lessons.size.toFloat()
                    } else 0f
                }

                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color.Green,
                    trackColor = Color.Gray,
                    strokeCap = StrokeCap.Round
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(color = contentColor)
                    }

                    state.isFinished -> {
                        // Render Konfetti celebration behind the "Done" text
                        KonfettiView(
                            modifier = Modifier.fillMaxSize(),
                            parties = listOf(party)
                        )

                        Text(
                            text = stringResource(id = R.string.lesson_done),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                    }

                    state.lessons.isNotEmpty() -> {
                        val currentLesson = state.lessons[state.currentIndex]
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LessonContent(lesson = currentLesson)

                            Spacer(Modifier.height(32.dp))

                            if (currentLesson.input != null) {
                                OutlinedTextField(
                                    value = state.userInput,
                                    onValueChange = {
                                        viewModel.handleIntent(
                                            LessonIntent.OnInputChanged(
                                                it
                                            )
                                        )
                                    },
                                    isError = state.isError,
                                    label = {
                                        Text(
                                            stringResource(id = R.string.lesson_input_label),
                                            color = Color.Gray
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    // Disable autocorrect and capitalization for code input
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        autoCorrectEnabled = false
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                                if (state.isError) {
                                    Text(
                                        text = stringResource(id = R.string.lesson_error_message),
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            val isEnabled =
                                currentLesson.input == null || state.userInput.isNotEmpty()

                            Button(
                                onClick = { viewModel.handleIntent(LessonIntent.OnNextClicked) },
                                enabled = isEnabled,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black,
                                    disabledContainerColor = Color.LightGray,
                                    disabledContentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(stringResource(id = R.string.lesson_button_next))
                            }
                        }
                    }
                }
            }
        }
    }
}