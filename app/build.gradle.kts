plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
//    id("com.android.application")
}

android {
    namespace = "com.example.donona"
    compileSdk = 35
    ndkPath = "D:/Program Files/UnityEditor/2022.3.44f1/Editor/Data/PlaybackEngines/AndroidPlayer/NDK"

    defaultConfig {
        applicationId = "com.example.donona"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        resConfigs("en", "vi")

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
    buildFeatures {
        viewBinding = true
    }

//    sourceSets.getByName("main") {
//        res.srcDir("src/main/res/layout/fragment")
//        res.srcDir("src/main/res/layout")
//        res.srcDir("src/main/res")
//    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase bom (version manager)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase authentication
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase store
    implementation("com.google.firebase:firebase-firestore")

    // Firebase storage
    implementation("com.google.firebase:firebase-storage")

    // For facebook authentication
//    implementation("com.facebook.android:facebook-android-sdk:8.x")

    // Prebuilt Auth UI
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")

    // Cardview module
    implementation("androidx.cardview:cardview:1.0.0")


//    implementation("com.facebook.android:facebook-login:latest.release")

    // For credential manager since everything else is deprecated
//    implementation("androidx.credentials:credentials:1.5.0-alpha04")
//    implementation("androidx.credentials:credentials-play-services-auth:<latest version>")
//    implementation("com.google.android.libraries.identity.googleid:googleid:<latest version>")

    // Vietmap module
    implementation("com.github.vietmap-company:maps-sdk-android:2.0.4")
    implementation("com.github.vietmap-company:maps-sdk-plugin-localization-android:2.0.0")
    implementation("com.github.vietmap-company:vietmap-services-geojson-android:1.0.0")
    implementation("com.github.vietmap-company:vietmap-services-turf-android:1.0.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")

    //Add picasso to load image from url to imageView
    implementation("com.squareup.picasso:picasso:2.71828")

    // Material UI
    implementation("com.google.android.material:material:1.3.0-alpha02")

    //Applying FireStorage
    implementation ("com.google.firebase:firebase-storage:20.2.1")

    // Vietmap navigation
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.jakewharton:butterknife:10.2.3")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.github.vietmap-company:maps-sdk-android:2.6.0")
    implementation("com.github.vietmap-company:maps-sdk-navigation-ui-android:2.3.2")
    implementation("com.github.vietmap-company:maps-sdk-navigation-android:2.3.3")
    implementation("com.github.vietmap-company:vietmap-services-core:1.0.0")
    implementation("com.github.vietmap-company:vietmap-services-directions-models:1.0.1")
    implementation("com.github.vietmap-company:vietmap-services-android:1.1.2")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.okhttp3:okhttp:3.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.0.0-beta4")
    // Auto Image Slider in Homepage
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.0")
    // Stripe
    implementation("com.stripe:stripe-android:20.51.1")
    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.core:core-splashscreen:1.2.0-alpha02")
    // Unity game
//    implementation(project(":unityLibrary"))

}



