@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.mod_achievement"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        //applicationId = "com.example.mod_achievement"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        //versionCode = libs.versions.android.versionCode.get().toInt()
        //versionName = libs.versions.android.compileSdk.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.theRouter)
    kapt(libs.theRouter.apt)

    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    api(libs.fastKv)

    implementation(libs.lifecycle.runtime)

    implementation(libs.moshi)
    implementation(libs.xlog)
    implementation(libs.bundles.viewModel)

    implementation(project(":lib_common"))
    implementation(project(":lib_room"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}