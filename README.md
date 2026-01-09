Mimo Challenge - Android
A modern Android application demonstrating a "fill-in-the-blank" coding lesson experience. This project focuses on Clean Architecture, Dependency Injection, and Reactive UI using Jetpack Compose.

Features
Dynamic Lesson Loading: Fetches coding challenges via a REST API.

Fill-in-the-blank Engine: Parses code snippets and identifies interactive input ranges.

Persistent Progress: Saves lesson completion timestamps locally using Room DB.

Celebration UI: Integrated Konfetti animations upon course completion.

Debug-Ready: Full execution stats are logged to Logcat for performance auditing.

 Tech Stack
Language: Kotlin

UI Framework: Jetpack Compose (Material 3)

Dependency Injection: Hilt (Dagger)

Networking: Retrofit + OkHttp

Database: Room Persistence Library

Asynchronous Logic: Kotlin Coroutines & Flow

Testing: JUnit 4, Mockito, and Coroutines Test library

Architecture
The app follows a strict MVI (Model-View-Intent) pattern within a Clean Architecture boundary:

UI Layer: Composable functions that react to LessonState and emit LessonIntent.

ViewModel: Manages business logic and handles the "Result" wrapper from data calls.

Data Layer: A repository pattern that mediates between a remote API and a local Room database.

How to Run
Clone the repository: git clone https://github.com/ioana-stavovei/MimoChallenge.git
Open the project in Android Studio (Ladybug or newer).

Sync Gradle and run the app module on an emulator or physical device.

 Testing
The project includes unit tests for the LessonViewModel, covering:

Successful lesson loading.

Network failure handling.

Input validation logic.

Course completion state transitions.

Run tests via terminal: ./gradlew test

Important: Completion stats are viewable in Logcat under the tag MimoChallenge.