# CODEBUDDY.md

This file provides guidance to CodeBuddy Code when working with code in this repository.

## Project Overview

WanAndroidCompose is an Android application built with Jetpack Compose that demonstrates modern Android development practices. It consumes the WanAndroid API (https://wanandroid.com/) to provide articles, Q&A, navigation, and user features.

## Tech Stack

- **UI Framework**: Jetpack Compose with Material 3
- **Navigation**: Navigation 3 (Nav3) with type-safe navigation
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit 2 + OkHttp 5 + Gson
- **Pagination**: Paging 3
- **State Management**: StateFlow
- **Image Loading**: Coil 3
- **Camera**: CameraX
- **Build System**: Gradle with Kotlin DSL (AGP 9.0, Kotlin 2.3.10)
- **Minimum SDK**: 24, Target SDK: 36

## Build Commands

```bash
# Build the project
./gradlew build

# Clean build
./gradlew clean

# Install debug APK
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate lint report
./gradlew lint
```

## Project Architecture

### Clean Architecture with Repository Pattern

The project follows Clean Architecture with a feature-based package structure:

```
app/src/main/java/com/wanandroid/compose/
├── bean/                  # Data models
├── http/                  # Network layer (OkHttp, Retrofit setup)
├── module/                # Hilt DI modules
│   ├── AppModule.kt       # Provides API interfaces, Retrofit
│   └── RepositoryModule.kt # Binds repository implementations
├── route/                 # Navigation setup
│   ├── RouteNavKey.kt     # Sealed class defining all navigation keys
│   ├── Navigator.kt       # Navigation helper with login interception
│   └── *EntryProvider.kt  # Navigation entry providers per screen
├── [feature]/             # Feature packages (login, main, collect, coin, etc.)
│   ├── api/               # Retrofit API interfaces
│   ├── repository/        # Repository interfaces
│   │   └── impl/          # Repository implementations
│   ├── screen/            # Compose screens
│   ├── viewmodel/         # ViewModels (Hilt injected)
│   ├── state/             # UI state classes
│   ├── event/             # UI event classes
│   └── action/            # User action classes
└── ui/theme/              # Theme, colors, typography
```

### Navigation System

Uses Navigation 3 (Nav3) with the following patterns:

1. **RouteNavKey**: Sealed class with `@Serializable` annotation defining all destinations
   - Supports login-required routes via `requiresLogin` parameter
   - Passes data objects directly (e.g., `ArticleDetail(val articleItem: ArticleItem)`)

2. **Navigator**: Custom wrapper around NavBackStack with login interception
   - Automatically redirects to login for protected routes when user is not authenticated
   - Stores redirect target in `Login` key for post-login navigation

3. **Entry Providers**: Each screen registers via `entryProvider { }` in `WanAndroidApp.kt`

### Dependency Injection

- Uses Dagger Hilt with `@HiltViewModel` for ViewModels
- API interfaces provided in `AppModule.kt` as singletons
- Repositories bound via `@Binds` in `RepositoryModule.kt`
- Base URL: `https://wanandroid.com/`

### State Management

- **ViewModels**: Use `StateFlow` for state, `SharedFlow` for events
- **State Pattern**: Each screen has `*UiState` and `*Event` classes
- **Collection**: UserManager singleton tracks login state and collected article IDs
- **Theme/Language**: AppViewModel manages theme mode and locale (in-memory, no restart needed)

### Network Layer

- **OkHttp**: Configured with custom `PersistentCookieJar` for session management
- **Retrofit**: Gson converter, singleton pattern via DI
- **BaseResponse**: Wrapper for API responses with error code handling
- **Paging**: Custom `LazyColumnPaging` for article list, Paging3 for other lists

## Key Conventions

1. **Screens**: Named `*Screen.kt`, composable functions accept navigator and optional parameters
2. **ViewModels**: Injected via Hilt, use `viewModelScope` for coroutines
3. **Lists**: Article lists use custom load-more; other lists use Paging3 + `LazyColumn`
4. **Layouts**: Prefer `ConstraintLayout` for list item layouts
5. **Images**: Use Coil Compose for image loading
6. **Permissions**: Use Accompanist Permissions library

## File Locations

- **Main Activity**: `app/src/main/java/com/wanandroid/compose/MainActivity.kt`
- **App Entry**: `app/src/main/java/com/wanandroid/compose/WanAndroidApp.kt`
- **DI Modules**: `app/src/main/java/com/wanandroid/compose/module/`
- **Navigation Keys**: `app/src/main/java/com/wanandroid/compose/route/RouteNavKey.kt`
- **API Definitions**: Each feature's `api/` subdirectory
- **Resources**: `app/src/main/res/` (minimal, mostly Compose theming)
- **Signing Config**: Debug and release use `wanandroid.jks` (password: 123456)

## Testing

- **Unit Tests**: `app/src/test/java/`
- **Instrumented Tests**: `app/src/androidTest/java/`
- Current test coverage is minimal (example tests only)

## Important Notes

- Uses AGP 9.0 with built-in Kotlin support (no separate kotlin-android plugin needed)
- ProGuard enabled for release builds
- Edge-to-edge enabled with transparent navigation bar
- Custom locale implementation allows language switching without Activity restart
- SSL certificate validation disabled in OkHttp (for development only)
