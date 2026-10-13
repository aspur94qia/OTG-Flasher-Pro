plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.otg.flasherpro"; compileSdk = 34
    defaultConfig { applicationId = "com.otg.flasherpro"; minSdk = 26; targetSdk = 34; versionCode = 2; versionName = "2.0" }
    buildFeatures { compose = true }; composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.5.8")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.2")
}
