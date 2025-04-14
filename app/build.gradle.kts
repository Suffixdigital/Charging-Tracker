plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.suffixdigital.chargingtracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.suffixdigital.chargingtracker"
        minSdk = 26
        compileSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "Charging Tracker 1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += arrayOf("-Xjvm-default=all")
    }

    dataBinding {
        enable = true
    }

    lint {
        // Example: Treat warnings as errors
        //warning("MissingTranslation")
        // Example: Disable a specific check
        //disable("TypographyFractions", "TypographyQuotes")
        // Example: Set text report output path
        textReport = true
        textOutput = file("lint-results.txt")
        // Example: Set HTML report output path
        htmlReport = true
        htmlOutput = file("lint-report.html")
        // Example: Set XML report output path
        xmlReport = true
        xmlOutput = file("lint-report.xml")
    }

}


dependencies {
//    implementation(project(":custombottomnavigation"))

    implementation(libs.androidx.core.ktx)

    //Material Design, Navigation Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Material Design, Architecture UI Components
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.runtime)


    //Support components to add new page at runtime
    implementation(libs.androidx.viewpager2)

    //Dimension handling for all screen size
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    //Shared Preferences to store key-value format of data
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)

    //library to store session cookies
    implementation(libs.okhttp.urlconnection)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)

    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Google Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.lifecycle.extensions)

    implementation("androidx.multidex:multidex:2.0.1")

    implementation(libs.lottie)

    implementation(libs.tyrus.standalone.client)
    implementation(libs.okhttp3.okhttp)


}