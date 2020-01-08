plugins {
    id(BuildPlugins.javaLibrary)
    id(BuildPlugins.kotlin)
    id(BuildPlugins.kotlinKapt)
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)

    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitMoshi)
    implementation(Libraries.moshi)
    kapt(Libraries.moshiCompiler)

    implementation(Libraries.okhttp)
    implementation(Libraries.okhttpLogging)

    implementation(Libraries.dagger)
    kapt(Libraries.daggerCompiler)

    implementation(Libraries.socketIO)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
