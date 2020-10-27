plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
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
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)
    implementation(Libraries.coroutinesReactive)

    implementation(Libraries.okhttp)
    implementation(Libraries.okhttpLogging)

    implementation(Libraries.ktorOkHttp)
    implementation(Libraries.ktorJson)
    implementation(Libraries.ktorSerialization)
    implementation(Libraries.ktorLogging)
    implementation(Libraries.ktorWebSockets)

    implementation(Libraries.serialization)
    implementation(Libraries.serializationConverter)

    implementation(Libraries.koinAndroid)
    implementation(Libraries.koinScope)
    implementation(Libraries.koinViewModel)

    implementation(Libraries.timber)

    debugImplementation(Libraries.flipper)
    debugImplementation(Libraries.flipperSO)
    debugImplementation(Libraries.flipperNetwork)

    releaseImplementation(Libraries.flipperNoOp)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
