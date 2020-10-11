plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    kotlin(BuildPlugins.serialization) version kotlinVersion
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
    viewBinding {
        isEnabled = true
    }
    dataBinding {
        isEnabled = true
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)
    implementation(Libraries.coroutinesReactive)

    implementation(Libraries.okhttp)
    implementation(Libraries.okhttpLogging)
    implementation(Libraries.moshi)
    kapt(Libraries.moshiCompiler)

    implementation(Libraries.ktorOkHttp)
    implementation(Libraries.ktorJson)
    implementation(Libraries.ktorSerialization)
    implementation(Libraries.ktorLogging)
    implementation(Libraries.ktorWebSockets)

    implementation(Libraries.serialization)
    implementation(Libraries.serializationConverter)

    implementation(Libraries.scarlett)
    implementation(Libraries.scarlettLifecycle)
    implementation(Libraries.scarlettMoshi)
    implementation(Libraries.scarlettOkHttp)

    implementation(Libraries.dagger)
    kapt(Libraries.daggerCompiler)

    implementation(Libraries.timber)

    debugImplementation(Libraries.flipper)
    debugImplementation(Libraries.flipperSO)
    debugImplementation(Libraries.flipperNetwork)

    releaseImplementation(Libraries.flipperNoOp)

    implementation(Libraries.socketIO)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
