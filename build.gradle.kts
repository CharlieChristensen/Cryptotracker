import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    this.repositories {
        google()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath(BuildPlugins.androidGradlePlugin)
        classpath(BuildPlugins.kotlinGradlePlugin)
        classpath(BuildPlugins.safeArgsClasspath)
        classpath(BuildPlugins.sqlDelightClasspath)
        classpath(BuildPlugins.koinClasspath)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://ci.android.com/builds/submitted/6043188/androidx_snapshot/latest/repository/") }
    }
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
