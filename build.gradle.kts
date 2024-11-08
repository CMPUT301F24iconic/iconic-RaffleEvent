buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add_icon.xml configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false

}