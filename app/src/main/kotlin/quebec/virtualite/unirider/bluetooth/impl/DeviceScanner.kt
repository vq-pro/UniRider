package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import quebec.virtualite.unirider.bluetooth.Device

interface DeviceScanner {

    fun init(activity: Activity)

    fun isStopped(): Boolean
    fun scan(whenDetecting: (Device) -> Unit)
    fun stop()
}
