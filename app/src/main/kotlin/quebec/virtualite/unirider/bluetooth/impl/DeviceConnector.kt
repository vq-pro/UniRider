package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity

interface DeviceConnector {
    fun connect(deviceAddress: String, callback: (WheelData) -> Unit)
    fun init(activity: Activity)
}
