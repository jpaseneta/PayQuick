# PayQuick

A simple application developed with MVVM + Clean Architecture guidelines, that displays the transaction list of a user. The app features a pull-to-refresh mechanism and pagination loading of results through continuous scrolling.

The app connects to a mock local webserver through Retrofit and OkHttp.

## Features
- **MVVM + Clean Architecture**: Separation of concerns into Data, Domain, and UI layers.
- **Transaction History**: Scrollable list of user transactions with pagination.
- **Pull to Refresh**: Swipe down to reload the latest transactions.
- **Continuous Scrolling**: Seamlessly load more transactions as you scroll.
- **Secure Token Storage**: Uses `EncryptedSharedPreferences` to securely store authentication and refresh tokens.
- **Token Refresh Mechanism**: Automatically refreshes the access token when it expires and retries the failed request.
- **Edge-to-Edge UI**: Modern Android UI that flows behind system bars.
- **Accessibility**: Optimized for screen readers and keyboard navigation.

## Technologies & Libraries Used
- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern toolkit for building native UI.
- **Dagger Hilt**: Dependency injection.
- **Retrofit & OkHttp**: Networking and API communication.
- **Gson**: JSON serialization/deserialization.
- **Jetpack Navigation**: In-app navigation.
- **ViewModel & LiveData/Flow**: State management.
- **EncryptedSharedPreferences**: Secure data persistence.
- **Material 3**: Design system for modern Android apps.
- **Mockito & Kotlinx Coroutines Test**: Unit testing framework.

## Architecture Layers
1. **Domain Layer**: Contains business logic, entity models, and repository interfaces. Pure Kotlin, no Android dependencies.
2. **Data Layer**: Implementation of repositories, API services (Retrofit), DTOs, and mapping logic to domain entities.
3. **UI Layer**: Composable screens, ViewModels, and state management using Jetpack Compose.

## How to Run
1. Ensure you have a local mock server running at `http://localhost:3000` (or update `NetworkModule.kt` with your server's IP).
2. Open the project in Android Studio.
3. Sync Gradle and run the `app` module on an emulator or physical device.
