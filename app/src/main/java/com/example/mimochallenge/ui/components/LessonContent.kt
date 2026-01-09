package com.example.mimochallenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.mimochallenge.data.model.Lesson
import com.example.mimochallenge.util.ColorUtils

@Composable
fun LessonContent(lesson: Lesson) {
    val range = lesson.input

    val annotatedString = buildAnnotatedString {
        var currentGlobalIndex = 0
        var boxDrawn = false

        lesson.content.forEach { block ->
            block.text.forEach { char ->
                // Check if current character falls within the hidden "input" range
                val isHidden = range != null &&
                        currentGlobalIndex >= range.startIndex &&
                        currentGlobalIndex < range.endIndex

                if (!isHidden) {
                    withStyle(style = SpanStyle(color = ColorUtils.fromHex(block.color))) {
                        append(char)
                    }
                } else {
                    // Draw the visual "rectangle" placeholder once when entering the hidden range
                    if (!boxDrawn) {
                        withStyle(
                            style = SpanStyle(
                                background = Color.Gray.copy(alpha = 0.3f),
                                color = Color.Transparent
                            )
                        ) {
                            // Create a fixed-width rectangle (e.g., 8 spaces)
                            append("        ")
                        }
                        boxDrawn = true
                    }
                }
                currentGlobalIndex++
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            // 1. Accessibility: Describe the content for TalkBack users
            .semantics {
                contentDescription = "Code snippet with a missing part to fill in"
            }
            // 2. Styling: Add a slight background and padding to the code block
            .background(
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    )
}