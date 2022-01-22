package quebec.virtualite.commons.android

import android.os.Bundle
import androidx.test.runner.MonitoringInstrumentation
import cucumber.api.android.CucumberInstrumentationCore

open class BaseCucumberInstrumentationRunner : MonitoringInstrumentation() {
    private val instrumentationCore = CucumberInstrumentationCore(this)

    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)

        instrumentationCore.create(arguments)
        start()
    }

    override fun onStart() {
        super.onStart()

        waitForIdleSync()
        instrumentationCore.start()
    }
}