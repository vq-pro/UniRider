package quebec.virtualite.unirider.services

import android.app.Activity

interface DeviceConnector {
    fun connect(device: Device, callback: (WheelData) -> Unit)
    fun init(activity: Activity)
}
