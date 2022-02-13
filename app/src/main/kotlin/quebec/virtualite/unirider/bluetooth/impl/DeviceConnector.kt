package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import quebec.virtualite.unirider.bluetooth.Device

interface DeviceConnector {
    fun connect(device: Device, callback: (WheelData) -> Unit)
    fun init(activity: Activity)
}
