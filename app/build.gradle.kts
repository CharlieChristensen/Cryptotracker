import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.safeArgs)
    id(BuildPlugins.sqlDelight)
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    packagingOptions {
        pickFirst("META-INF/kotlinx-coroutines-core.kotlin_module")
    }

    dynamicFeatures = mutableSetOf(":coindetail", ":settings", ":coinlist", ":portfolio", ":themeplayground")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":remote"))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.ktxCore)
    implementation(Libraries.appCompat)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.activity)
    implementation(Libraries.fragment)
    implementation(Libraries.workManager)
    implementation(Libraries.recyclerView)

    implementation(Libraries.lifecycleExtensions)
    implementation(Libraries.lifecycleCommon)
    implementation(Libraries.viewModel)
    implementation(Libraries.liveData)

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

    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitMoshi)
    implementation(Libraries.okhttp)
    implementation(Libraries.okhttpLogging)
    implementation(Libraries.moshi)

    implementation(Libraries.glide)
    kapt(Libraries.glideCompiler)

    implementation(Libraries.sqlDelight)
    implementation(Libraries.sqlDelightCoroutines)

    implementation(Libraries.timber)

    debugImplementation(Libraries.leakCanary)

    testImplementation(TestLibraries.junit)
    testImplementation(TestLibraries.mockitoKotlin)

    androidTestImplementation(TestLibraries.coreTesting)
    androidTestImplementation(TestLibraries.testRunner)
    androidTestImplementation(TestLibraries.espresso)
}
