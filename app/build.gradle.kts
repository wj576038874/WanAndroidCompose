plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.wanandroid.compose"
    compileSdk {
        version = release(37)

    }
    compileSdkMinor = 0

    defaultConfig {
        applicationId = "com.wanandroid.compose"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

//        ndk {
//            abiFilters.add("arm64-v8a")
//            abiFilters.add("armeabi-v7a")
//        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        disable += "Instantiatable"
    }

    signingConfigs {
        create("release"){
            keyAlias = "wanandroid"
            keyPassword = "123456"
            storeFile = file("${rootDir.absolutePath}/wanandroid.jks")
            storePassword = "123456"
        }
        getByName("debug"){
            keyAlias = "wanandroid"
            keyPassword = "123456"
            storeFile = file("${rootDir.absolutePath}/wanandroid.jks")
            storePassword = "123456"
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
//            ndk.abiFilters.add("arm64-v8a")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isShrinkResources = false
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
//            ndk.abiFilters.add("armeabi-v7a")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.exifinterface)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.viewmodel.compose)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)

    // 添加网络请求相关依赖
    implementation(libs.bundles.network)

    implementation(libs.bundles.coil)

    implementation(libs.bundles.kotlinx)

    implementation(libs.bundles.camerax)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
