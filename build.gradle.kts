buildscript {
    repositories {
        google()  // Make sure you have this in the repositories section
        mavenCentral()
    }
    dependencies {
        // Google Services classpath
        classpath ("com.google.gms:google-services:4.3.10") // Make sure the version matches the plugin version
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false

}
