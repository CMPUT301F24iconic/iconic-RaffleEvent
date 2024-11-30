plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.iconic_raffleevent"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.iconic_raffleevent"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
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


    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation(libs.play.services.vision.common)
    implementation(libs.play.services.vision)
    implementation(libs.cronet.embedded)
    implementation(libs.play.services.location)
    implementation(libs.espresso.intents)
    //noinspection GradlePath
    //implementation(files("C:/Users/Aiden Teal.DESKTOP-1RMQJI7/AppData/Local/Android/Sdk/platforms/android-35/android.jar"))
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.test.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.ext.junit)
    androidTestImplementation(libs.ext.junit)

    // Added the following dependencies
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.mockito.core) // Adds Mockito for unit testing
    androidTestImplementation(libs.mockito.android) // Adds Mockito for Android tests

    // Additional dependencies created by Zhiyuan
    // The additional dependencies for Firebase Storage, ZXing core, and ZXing Android Embedded are added.
    implementation(libs.firebase.storage)
    implementation(libs.core)

    //Zxing Android Embedded (https://github.com/journeyapps/zxing-android-embedded)
    implementation(libs.zxing.android.embedded)


    implementation(libs.constraintlayout)

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

    implementation (libs.annotation)

    // avatar generation and image upload
    implementation (libs.avatarview.coil)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.activity.v170)


    // Other AndroidX and Google Libraries
    implementation(libs.appcompat)
    implementation(libs.material.v180)
    implementation(libs.activity.v161)
    implementation(libs.constraintlayout.v220)
    implementation(libs.play.services.vision.common.v1912)
    implementation(libs.play.services.vision)
    implementation(libs.play.services.location.v2101)
    implementation(libs.core.ktx)  // Replacing libs.core.ktx

    // image cropper
    implementation (libs.circleimageview)

    // Testing dependencies
    testImplementation (libs.junit.jupiter.api)
    testImplementation (libs.junit.jupiter.engine.v582)
    testImplementation (libs.mockito.core.v480)
    testImplementation (libs.mockito.inline)
    testImplementation (libs.mockito.junit.jupiter)

    // Android test dependencies
    testImplementation (libs.core.v150)
    testImplementation (libs.robolectric)

    testImplementation (libs.mockito.core.v480)
    testImplementation (libs.mockito.inline)

    testImplementation ("junit:junit:4.13.2")

    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.uiautomator.v230alpha03)
    // Firebase test dependencies
    testImplementation (libs.google.firebase.firestore)
}