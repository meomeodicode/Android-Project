plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.instagram"
    compileSdk = 34
    //Test

    defaultConfig {
        applicationId = "com.example.instagram"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding  = true;
    }
}
//
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.vanniktech:android-image-cropper:4.6.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.gms:play-services-location:20.0.0")
}