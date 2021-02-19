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
* Android JUnit
* Name = Unit Tests
* Test kind = All in directory
* Directory: ..\app\src\test\kotlin\quebec\virtualite\unirider

## Cucumber

* Run Configuration
* Android Instrumented Tests
* Name = Cucumber Tests
* Test = All in Module
* Instrumentation class = CucumberInstrumentationRunner
* Before launch = Unit Tests

## Cucumber (WIP)

* Same thing as previous
* Add an instrumentation argument: 
  tags = @WIP

## To choose between E2E (global) and App (smaller operations)

* Menu Build
* Select Build Variant...
