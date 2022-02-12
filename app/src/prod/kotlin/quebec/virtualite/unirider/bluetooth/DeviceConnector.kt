package quebec.virtualite.unirider.bluetooth

import android.app.Activity

interface DeviceConnector {
    fun connect(device: Device, callback: (WheelData) -> Unit)
    fun init(activity: Activity)
}
