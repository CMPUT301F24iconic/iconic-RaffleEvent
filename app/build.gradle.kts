plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.iconic_raffleevent"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iconic_raffleevent"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Added to dependencies to allow for unit testing
    tasks.withType<Test>{
        useJUnitPlatform()
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)

    // Added the following dependencies
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.mockito.core) // Adds Mockito for unit testing
    androidTestImplementation(libs.mockito.android) // Adds Mockito for Android tests

    // Additional dependencies created by Zhiyuan
    // The additional dependencies for Firebase Storage, ZXing core, and ZXing Android Embedded are added.
    implementation(libs.firebase.storage)
    implementation(libs.core)
    implementation(libs.zxing.android.embedded)


    implementation(libs.constraintlayout)
    implementation (libs.glide)
    implementation(libs.firebase.storage.v2030)
    implementation(libs.play.services.tasks)
    implementation(libs.play.services.maps)
    implementation(libs.barcode.scanning.common)
    androidTestImplementation(libs.uiautomator)

    // CameraX core library
    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.camera.view)

    implementation (libs.barcode.scanning)
    implementation (libs.play.services.mlkit.barcode.scanning)
    implementation (libs.firebase.messaging)
    implementation (libs.volley)

}