// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        val agpVersion = "8.6.0"
        val kotlinVersion = "2.1.0"

        classpath ("com.android.tools.build:gradle:$agpVersion")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    // noinspection GradleDependency
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

//task clean(type: Exec) {
//    commandLine 'cmd', '/c', 'TASKKILL /F /IM java.exe'
//}
