import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(BuildPlugins.dynamicFeature)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
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
    viewBinding {
        isEnabled = true
    }
    dataBinding {
        isEnabled = true
    }
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":app"))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.ktxCore)
    implementation(Libraries.appCompat)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.cardView)

    implementation(Libraries.lifecycleExtensions)
    implementation(Libraries.lifecycleCommon)
    implementation(Libraries.liveData)

    implementation(Libraries.navigation)

    implementation(Libraries.coroutines)
    implementation(Libraries.coroutinesAndroid)
    implementation(Libraries.corbindMaterial)

    implementation(Libraries.dagger)
    compileOnly(Libraries.assistInjectAnnot)
    kapt(Libraries.assistInjectCompiler)
    kapt(Libraries.daggerCompiler)

    implementation(Libraries.glide)

    implementation(Libraries.mpAndroidChart)
}
