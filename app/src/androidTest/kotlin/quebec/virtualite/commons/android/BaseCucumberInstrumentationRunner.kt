package quebec.virtualite.commons.android

import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
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

        grantPermissions()
    }

    private fun grantPermissions()
    {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val packageName = instrumentation.targetContext.packageName
        val permissions = getDeclaredPermissions()

        if (permissions.contains(BLUETOOTH_SCAN))
        {
            uiAutomation.executeShellCommand("cmd location set-location-enabled true")
            uiAutomation.executeShellCommand("settings put secure location_providers_allowed +gps,+network")
            uiAutomation.executeShellCommand("svc bluetooth enable")
        }

        permissions.forEach { permission ->
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant $packageName $permission"
            )
        }
    }

    private fun getDeclaredPermissions(): List<String>
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )

        return packageInfo.requestedPermissions?.toList()
            ?: emptyList()
    }
}
