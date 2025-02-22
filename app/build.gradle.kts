plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.buddypunchclone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.buddypunchclone"
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
}

dependencies {
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Firebase dependencies
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-database:21.0.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Retrofit and networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10")

    // Room Database
    implementation("androidx.room:room-runtime:2.4.2")
    annotationProcessor("androidx.room:room-compiler:2.4.2")

    // PostgreSQL
    implementation("org.postgresql:postgresql:42.3.4")

    // RecyclerView and CardView
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

//    Google Map Configuration
    implementation ("com.google.android.gms:play-services-maps:18.1.0")

//    IPS Configuration
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("org.java-websocket:Java-WebSocket:1.5.3")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")


    // VLC media player library for streaming support
    implementation("org.videolan.android:libvlc-all:3.3.0")
    implementation ("androidx.cardview:cardview:1.0.0")

    // Add Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation(libs.com.google.firebase.firebase.database)
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")


    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    // Add Retrofit dependencies
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("com.google.mlkit:face-detection:16.0.0")



    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.firebase:firebase-storage:20.0.0")

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")


    implementation ("com.airbnb.android:lottie:6.1.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
