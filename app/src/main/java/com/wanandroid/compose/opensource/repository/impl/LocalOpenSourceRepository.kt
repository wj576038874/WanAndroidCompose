package com.wanandroid.compose.opensource.repository.impl

import com.wanandroid.compose.R
import com.wanandroid.compose.opensource.model.OpenSourceLibrary
import com.wanandroid.compose.opensource.repository.OpenSourceRepository
import javax.inject.Inject

/** Keep in sync with app/build.gradle.kts and gradle/libs.versions.toml.
 * Related runtime, debug, test and code-generation modules are grouped by project.
 */
class LocalOpenSourceRepository @Inject constructor() : OpenSourceRepository {
    override fun getLibraries(): List<OpenSourceLibrary> = libraries

    private val libraries = listOf(
        OpenSourceLibrary("compose", "Jetpack Compose", R.string.string_oss_compose, "https://github.com/androidx/androidx/tree/androidx-main/compose"),
        OpenSourceLibrary("core", "AndroidX Core / SplashScreen", R.string.string_oss_core, "https://github.com/androidx/androidx/tree/androidx-main/core"),
        OpenSourceLibrary("activity", "AndroidX Activity", R.string.string_oss_activity, "https://github.com/androidx/androidx/tree/androidx-main/activity"),
        OpenSourceLibrary("appcompat", "AndroidX AppCompat", R.string.string_oss_appcompat, "https://github.com/androidx/androidx/tree/androidx-main/appcompat"),
        OpenSourceLibrary("lifecycle", "AndroidX Lifecycle / ViewModel", R.string.string_oss_lifecycle, "https://github.com/androidx/androidx/tree/androidx-main/lifecycle"),
        OpenSourceLibrary("navigation", "Navigation 3", R.string.string_oss_navigation, "https://github.com/androidx/androidx/tree/androidx-main/navigation3"),
        OpenSourceLibrary("paging", "Paging 3", R.string.string_oss_paging, "https://github.com/androidx/androidx/tree/androidx-main/paging"),
        OpenSourceLibrary("browser", "AndroidX Browser", R.string.string_oss_browser, "https://github.com/androidx/androidx/tree/androidx-main/browser"),
        OpenSourceLibrary("constraintlayout", "ConstraintLayout", R.string.string_oss_constraintlayout, "https://github.com/androidx/androidx/tree/androidx-main/constraintlayout"),
        OpenSourceLibrary("camera", "CameraX", R.string.string_oss_camera, "https://github.com/androidx/androidx/tree/androidx-main/camera"),
        OpenSourceLibrary("exif", "ExifInterface", R.string.string_oss_exif, "https://github.com/androidx/androidx/tree/androidx-main/exifinterface"),
        OpenSourceLibrary("hilt", "Dagger / Hilt", R.string.string_oss_hilt, "https://github.com/google/dagger"),
        OpenSourceLibrary("androidx_hilt", "AndroidX Hilt", R.string.string_oss_androidx_hilt, "https://github.com/androidx/androidx/tree/androidx-main/hilt"),
        OpenSourceLibrary("accompanist", "Accompanist Permissions", R.string.string_oss_accompanist, "https://github.com/google/accompanist"),
        OpenSourceLibrary("coil", "Coil", R.string.string_oss_coil, "https://github.com/coil-kt/coil"),
        OpenSourceLibrary("retrofit", "Retrofit", R.string.string_oss_retrofit, "https://github.com/square/retrofit"),
        OpenSourceLibrary("okhttp", "OkHttp", R.string.string_oss_okhttp, "https://github.com/square/okhttp"),
        OpenSourceLibrary("gson", "Gson", R.string.string_oss_gson, "https://github.com/google/gson"),
        OpenSourceLibrary("kotlin", "Kotlin", R.string.string_oss_kotlin, "https://github.com/JetBrains/kotlin"),
        OpenSourceLibrary("coroutines", "Kotlin Coroutines", R.string.string_oss_coroutines, "https://github.com/Kotlin/kotlinx.coroutines"),
        OpenSourceLibrary("serialization", "Kotlin Serialization", R.string.string_oss_serialization, "https://github.com/Kotlin/kotlinx.serialization"),
        OpenSourceLibrary("structured", "Structured Coroutines", R.string.string_oss_structured, "https://github.com/santimattius/structured-coroutines"),
        OpenSourceLibrary("leakcanary", "LeakCanary / Android Studio", R.string.string_oss_leakcanary, "https://github.com/square/leakcanary"),
        OpenSourceLibrary("junit", "JUnit 4", R.string.string_oss_junit, "https://github.com/junit-team/junit4"),
        OpenSourceLibrary("android_test", "AndroidX Test / Espresso", R.string.string_oss_android_test, "https://github.com/android/android-test"),
        OpenSourceLibrary("ksp", "Kotlin Symbol Processing", R.string.string_oss_ksp, "https://github.com/google/ksp"),
        OpenSourceLibrary("gradle", "Gradle", R.string.string_oss_gradle, "https://github.com/gradle/gradle"),
    )
}
