import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    buildFeatures {
        buildConfig = true
    }

    //noinspection GradleDependency
    compileSdk = 35

    compileOptions {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }

    defaultConfig {
        minSdk = 28
        //noinspection EditedTargetSdkVersion,OldTargetApi
        targetSdk = 35

        applicationId = "quebec.virtualite.unirider"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testApplicationId = "quebec.virtualite.unirider.test"
        testInstrumentationRunner = "quebec.virtualite.unirider.test.CucumberInstrumentationRunner"

//        javaCompileOptions {
//            annotationProcessorOptions {
//                arguments += ["room.schemaLocation": "$projectDir/schemas".toString()]
//            }
//        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }

    flavorDimensions.add("version")

    productFlavors {
        create("end2end") {
            buildConfigField("String", "SCENARIOS", "\"@End2End\"")

            buildConfigField("Boolean", "BLUETOOTH_ACTUAL", "true")

            // Calls the backend
            // Equivalent to localhost, which doesn't work with unmocked Retrofit2 request
            buildConfigField("String", "SERVER_BASE_URL", "\"http://10.0.2.2:8080\"")
            buildConfigField("Boolean", "SERVER_ACTUAL", "true")
        }

        create("integration") {
            buildConfigField("String", "SCENARIOS", "\"~@End2End\"")

            buildConfigField("Boolean", "BLUETOOTH_ACTUAL", "false")

            // Backend is mocked
            buildConfigField("String", "SERVER_BASE_URL", "\"http://localhost:8080\"")
            buildConfigField("Boolean", "SERVER_ACTUAL", "false")
        }
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs("src/androidTest/assets")
            java.srcDirs("src/androidTest/kotlin")
        }

        getByName("main") {
            assets.srcDirs("src/main/res")
            java.srcDirs("src/main/kotlin")
        }

        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
    }

    lint {
        abortOnError = false
    }

    packaging {
        resources.excludes.add("META-INF/*.md")
        resources.excludes.add("META-INF/*.txt")
        resources.merges.add("META-INF/services/*")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }

        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    namespace = "quebec.virtualite.unirider"
    testNamespace = "quebec.virtualite.unirider.test"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    val androidMaterialVersion = "1.14.0"
    val androidxAnnotationVersion = "1.11.0"
    val androidxNavigationVersion = "2.10.2"
    val assertJVersion = "3.27.7"
    val coreVersion = "1.7.0"
    val cucumberVersion = "4.1.1"
    val cucumberPicoVersion = "4.1.1"
    val espressoVersion = "3.7.0"
    val extJUnitVersion = "1.3.0"
    val jacksonVersion = "2.22.3"
    val jacksonAnnotationVersion = "2.22"
    val junitVersion = "6.1.3"
    val mockitoInlineVersion = "5.1.1"
    val mockitoKotlinVersion = "4.1.0"
    val roomVersion = "2.8.5"
    val rulesVersion = "1.7.0"
    val runnerVersion = "1.7.0"
    val supportAnnotationsVersion = "28.0.0"

    // App dependencies
    implementation("androidx.annotation:annotation:$androidxAnnotationVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$androidxNavigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationVersion")
    implementation("com.google.android.material:material:$androidMaterialVersion")

    // Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Unit Tests
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-engine:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")

    testImplementation("org.assertj:assertj-core:$assertJVersion")

    testImplementation("org.mockito:mockito-inline:$mockitoInlineVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoInlineVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    // Integration Tests
    androidTestImplementation(platform("org.junit:junit-bom:$junitVersion"))
    androidTestImplementation("org.junit.jupiter:junit-jupiter")

    androidTestImplementation("org.assertj:assertj-core:$assertJVersion")

    androidTestImplementation("androidx.test:core:$coreVersion")
    androidTestImplementation("androidx.test:core-ktx:$coreVersion")
    androidTestImplementation("androidx.test.ext:junit:$extJUnitVersion")
    androidTestImplementation("androidx.test.ext:junit-ktx:$extJUnitVersion")
    androidTestImplementation("androidx.test:runner:$runnerVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("com.android.support:support-annotations:$supportAnnotationsVersion")
    androidTestImplementation("androidx.test:rules:$rulesVersion")
//    androidTestImplementation 'com.android.support.test:rules:1.0.2'

    // Cucumber
    androidTestImplementation("io.cucumber:cucumber-android:$cucumberVersion")
    androidTestImplementation("io.cucumber:cucumber-picocontainer:$cucumberPicoVersion")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationVersion")
}
