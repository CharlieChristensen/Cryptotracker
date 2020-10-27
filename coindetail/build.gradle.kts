plugins {
    id(BuildPlugins.dynamicFeature)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.safeArgs)
}

android {
    compileSdkVersion(AndroidSdk.target)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":app"))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.ktxCore)
    implementation(Libraries.appCompat)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)

    implementation(Libraries.lifecycleCommon)
    implementation(Libraries.savedState)
    implementation(Libraries.viewModel)

    implementation(Libraries.navigation)

    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)
    implementation(Libraries.corbindMaterial)

    implementation(Libraries.koinAndroid)
    implementation(Libraries.koinScope)
    implementation(Libraries.koinViewModel)

    implementation(Libraries.glide)

    implementation(Libraries.timber)

    implementation(Libraries.mpAndroidChart)
}
