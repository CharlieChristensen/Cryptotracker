import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.safeArgs)
}

android {
    compileSdkVersion(AndroidSdk.target)

    defaultConfig {
        applicationId = "com.charliechristensen.cryptotracker.cryptotracker"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    dynamicFeatures = mutableSetOf(":coindetail", ":settings", ":coinlist", ":portfolio", ":themeplayground")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":remote"))
    implementation(project(":database"))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.ktxCore)
    implementation(Libraries.appCompat)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.cardView)
    implementation(Libraries.activity)
    implementation(Libraries.fragment)
    implementation(Libraries.playCore)
    implementation(Libraries.workManager)

    implementation(Libraries.lifecycleExtensions)
    implementation(Libraries.lifecycleCommon)
    implementation(Libraries.viewModel)
    implementation(Libraries.liveData)
    implementation(Libraries.lifecycleSavedState)

    implementation(Libraries.navigation)
    implementation(Libraries.navigationUI)
    implementation(Libraries.navigationDynamic)

    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)
    implementation(Libraries.corbind)
    implementation(Libraries.corbindNavigation)
    implementation(Libraries.flowPreferences)

    implementation(Libraries.dagger)
    compileOnly(Libraries.assistInjectAnnot)
    kapt(Libraries.assistInjectCompiler)
    kapt(Libraries.daggerCompiler)

    implementation(Libraries.glide)
    kapt(Libraries.glideCompiler)

    debugImplementation(Libraries.leakCanary)

    testImplementation(TestLibraries.junit)
    testImplementation(TestLibraries.mockitoKotlin)

    androidTestImplementation(TestLibraries.coreTesting)
    androidTestImplementation(TestLibraries.testRunner)
    androidTestImplementation(TestLibraries.espresso)
}
