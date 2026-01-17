plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // HILT
    alias(libs.plugins.hilt)
    // KSP
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.estholon.authentication"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kotlin{
        jvmToolchain(8)
    }
}

dependencies {

    // PROJECTS
    implementation(project(":core"))
    implementation(project(":analytics"))

    // FIREBASE
    implementation(libs.firebase.auth)

    // HILT
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // FACEBOOK
    implementation(libs.facebook.login)

    // GOOGLE
    implementation (libs.googleid)

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.play.services.tasks)

}