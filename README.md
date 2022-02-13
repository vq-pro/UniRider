# Features

* [Main Screen](app/src/androidTest/assets/features/Main_Screen.feature)
* [Wheel Editing & Adding](app/src/androidTest/assets/features/Wheel_Editing_and_Adding.feature)
* [Wheel Viewing](app/src/androidTest/assets/features/Wheel_Viewing.feature)
* [Wheel Viewing - KingSong 14S](app/src/androidTest/assets/features/Wheel_Viewing_14S.feature)

# Bluetooth

* Go in phone and enable the "localization" permission in the app settings
* When uninstall the app on the phone:
  * Ensure to uninstall it for all users
  * Settings / Application / <app> / Uninstall for all users

# Directory Setup

* Ensure that activity classes specify the right package (on top)
* Define sourceSets in build.gradle corresponding to the right packages for androidTest, main and test.
* Classes in AndroidManifest.xml (and other xml) is relative to "applicationId" in build.gradle

# Test Run Configuration Setup in IntelliJ

## Unit

* Run Configuration
* Gradle
* Name = Unit Tests
* Run = :app:testIntegrationDebugUnitTest --tests "quebec.virtualite.unirider.*"

## Cucumber

* Run Configuration
* Android Instrumented Tests
* Name = Cucumber Tests
* Test = All in Module
* Instrumentation class = CucumberInstrumentationRunner
* Before launch =
  * Run Gradle task "app:cleanBuildCache"
  * Gradle-aware Make
  * Run 'Gradle Unit Tests'

## Cucumber (WIP)

* Same thing as previous
* Add an instrumentation argument:
  tags = @WIP

## To choose between E2E (global) and App (smaller operations)

* Menu Build
* Select Build Variant...
