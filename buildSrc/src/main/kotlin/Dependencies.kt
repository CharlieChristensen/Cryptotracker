const val kotlinVersion = "1.4.10"

object BuildPlugins {

    object Versions {
        const val gradlePlugin = "4.1.0-rc02"
        const val safeArgs = "2.3.0-alpha04"
        const val sqlDelight = "1.3.0"
        const val koin = "2.2.0-rc-2"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidApplication = "com.android.application"
    const val dynamicFeature = "com.android.dynamic-feature"
    const val androidLibrary = "com.android.library"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val sqlDelight = "com.squareup.sqldelight"
    const val koin = "koin"
    const val safeArgs = "androidx.navigation.safeargs.kotlin"
    const val sqlDelightClasspath = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
    const val safeArgsClasspath = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeArgs}"
    const val koinClasspath = "org.koin:koin-gradle-plugin:${Versions.koin}"
    const val serialization = "plugin.serialization"
}

object AndroidSdk {
    const val min = 21
    const val target = 29
}

object Libraries {
    private object Versions {
        const val coroutines             =  "1.4.0-M1"
        const val constraintLayout       =  "2.0.0"
        const val androidxCore           =  "1.3.0-rc01"
        const val activity               =  "1.2.0-alpha07"
        const val appCompat              =  "1.3.0-alpha01"
        const val fragment               =  "1.3.0-alpha07"
        const val lifecycle              =  "2.3.0-alpha06"
        const val navigation             =  "2.3.0"
        const val paging                 =  "2.1.2"
        const val recyclerView           =  "1.2.0-alpha05"
        const val workManager            =  "2.4.0"
        const val material               =  "1.2.0-alpha05"
        const val dagger                 =  "2.28.3"
        const val assistedInject         =  "0.5.2"
        const val koin                   = "2.2.0-rc-2"
        const val okhttp                 =  "4.8.1"
        const val ktor                   =  "1.4.0"
        const val sqlDelight             =  "1.3.0"
        const val corbind                =  "1.3.2"
        const val flowPrefs              =  "1.3.1"
        const val glide                  =  "4.11.0"
        const val leakCanary             =  "2.3"
        const val mpAndroidChart         =  "3.1.0"
        const val timber                 =  "4.7.1"
        const val flipper                =  "0.31.2"
        const val flipperSO              =  "0.8.0"
        const val serialization          = "1.0.0"
        const val serializationConverter = "0.7.0"
    }

    const val kotlinStdLib           = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    const val kotlinReflect          = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    const val coroutines             = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid      = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesReactive     = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.coroutines}"
    const val appCompat              = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val ktxCore                = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val activity               = "androidx.activity:activity-ktx:${Versions.activity}"
    const val fragment               = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    const val constraintLayout       = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val lifecycleCommon        = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val savedState             = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
    const val viewModel              = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val liveData               = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val navigation             = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUI           = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationDynamic      = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}"
    const val workManager            = "androidx.work:work-runtime-ktx:${Versions.workManager}"
    const val paging                 = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    const val recyclerView           = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val material               = "com.google.android.material:material:${Versions.material}"
    const val dagger                 = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler         = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val sqlDelight             = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    const val sqlDelightCoroutines   = "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
    const val assistInjectAnnot      = "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.assistedInject}"
    const val assistInjectCompiler   = "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.assistedInject}"
    const val koinAndroid            = "org.koin:koin-android:${Versions.koin}"
    const val koinScope              = "org.koin:koin-androidx-scope:${Versions.koin}"
    const val koinViewModel          = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
    const val koinWorkManager        = "org.koin:koin-androidx-workmanager:${Versions.koin}"
    const val koinFragmentFactory    = "org.koin:koin-androidx-fragment:${Versions.koin}"
    const val ktorOkHttp             = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
    const val ktorJson               = "io.ktor:ktor-client-json:${Versions.ktor}"
    const val ktorSerialization      = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
    const val ktorLogging            = "io.ktor:ktor-client-logging-jvm:${Versions.ktor}"
    const val ktorWebSockets         = "io.ktor:ktor-client-websockets:${Versions.ktor}"
    const val okhttp                 = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging          = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val leakCanary             = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val glide                  = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler          = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val mpAndroidChart         = "com.github.PhilJay:MPAndroidChart:v${Versions.mpAndroidChart}"
    const val flowPreferences        = "com.github.tfcporciuncula:flow-preferences:${Versions.flowPrefs}"
    const val corbind                = "ru.ldralighieri.corbind:corbind:${Versions.corbind}"
    const val corbindMaterial        = "ru.ldralighieri.corbind:corbind-material:${Versions.corbind}"
    const val corbindAppCompat       = "ru.ldralighieri.corbind:corbind-appcompat:${Versions.corbind}"
    const val corbindNavigation      = "ru.ldralighieri.corbind:corbind-navigation:${Versions.corbind}"
    const val timber                 = "com.jakewharton.timber:timber:${Versions.timber}"
    const val flipper                = "com.facebook.flipper:flipper:${Versions.flipper}"
    const val flipperNetwork         = "com.facebook.flipper:flipper-network-plugin:${Versions.flipper}"
    const val flipperSO              = "com.facebook.soloader:soloader:${Versions.flipperSO}"
    const val flipperNoOp            = "com.facebook.flipper:flipper-noop:${Versions.flipper}"
    const val serialization          = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
    const val serializationConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.serializationConverter}"
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
