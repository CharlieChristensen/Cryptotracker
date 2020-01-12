const val kotlinVersion = "1.3.61"

object BuildPlugins {

    object Versions {
        const val gradlePlugin = "4.0.0-alpha08"
        const val safeArgs = "2.2.0-rc04"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val dynamicFeature = "com.android.dynamic-feature"
    const val javaLibrary = "java-library"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val safeArgs = "androidx.navigation.safeargs.kotlin"
    const val safeArgsPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeArgs}"
}

object AndroidSdk {
    const val min = 21
    const val target = 29
}

object Libraries {
    private object Versions {
        const val kotlin            =  "1.3.61"
        const val retrofit          =  "2.6.1"
        const val okhttp            =  "4.2.2"
        const val dagger            =  "2.25.4"
        const val assistedInject    =  "0.5.2"
        const val coroutines        =  "1.3.3"
        const val constraintLayout  =  "2.0.0-beta4"
        const val androidxCore      =  "1.2.0-rc01"
        const val appCompat         =  "1.2.0-alpha01"
        const val activity          =  "1.1.0-rc03"
        const val fragment          =  "1.2.0-rc04"
        const val cardView          =  "1.0.0"
        const val material          =  "1.2.0-alpha03"
        const val savedState        =  "1.0.0-rc03"
        const val lifecycle         =  "2.2.0-rc03"
        const val room              =  "2.2.3"
        const val navigation        =  "2.2.0-rc04"
        const val dynamicNavigation =  "2.3.0-SNAPSHOT"
        const val workManager       =  "2.3.0-beta02"
        const val corbind           =  "1.3.0"
        const val flowPrefs         =  "1.0.0"
        const val moshi             =  "1.9.1"
        const val glide             =  "4.10.0"
        const val socketIO          =  "1.0.0"
        const val leakCanary        =  "2.0"
        const val mpAndroidChart    =  "v3.1.0"
    }

    const val kotlinStdLib           = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    const val coroutines             = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid      = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val flowPreferences        = "com.github.tfcporciuncula:flow-preferences:${Versions.flowPrefs}"
    const val appCompat              = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val activity               = "androidx.activity:activity-ktx:${Versions.activity}"
    const val fragment               = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    const val constraintLayout       = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore                = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val roomRuntime            = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx                = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler           = "androidx.room:room-compiler:${Versions.room}"
    const val workManager            = "androidx.work:work-runtime-ktx:${Versions.workManager}"
    const val dagger                 = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler         = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val assistInjectAnnot      = "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.assistedInject}"
    const val assistInjectCompiler   = "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.assistedInject}"
    const val retrofit               = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitMoshi          = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val moshi                  = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiCompiler          = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val okhttp                 = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging          = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val socketIO               = "io.socket:socket.io-client:${Versions.socketIO}"
    const val material               = "com.google.android.material:material:${Versions.material}"
    const val lifecycleExtensions    = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleCommon        = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val lifecycleSavedState    = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.savedState}"
    const val viewModel              = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val liveData               = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val corbind                = "ru.ldralighieri.corbind:corbind:${Versions.corbind}"
    const val corbindMaterial        = "ru.ldralighieri.corbind:corbind-material:${Versions.corbind}"
    const val corbindAppCompat       = "ru.ldralighieri.corbind:corbind-appcompat:${Versions.corbind}"
    const val corbindNavigation      = "ru.ldralighieri.corbind:corbind-navigation:${Versions.corbind}"
    const val navigation             = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUI           = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationDynamic      = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.dynamicNavigation}"
    const val glide                  = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler          = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val mpAndroidChart         = "com.github.PhilJay:MPAndroidChart:${Versions.mpAndroidChart}"
    const val cardView               = "androidx.cardview:cardview:${Versions.cardView}"
    const val leakCanary             = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}

object TestLibraries {
    private object Versions {
        const val mockitoKotlin     =  "1.5.0"
        const val junit             =  "4.12"
        const val espresso          =  "3.2.0"
        const val testRunner        =  "1.2.0"
        const val coreTesting       =  "2.1.0"
    }
    const val junit             = "junit:junit:${Versions.junit}"
    const val testRunner        = "androidx.test:runner:${Versions.testRunner}"
    const val espresso          = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val coreTesting       = "androidx.arch.core:core-testing:${Versions.coreTesting}"
    const val mockitoKotlin     = "com.nhaarman:mockito-kotlin:${Versions.mockitoKotlin}"
}
