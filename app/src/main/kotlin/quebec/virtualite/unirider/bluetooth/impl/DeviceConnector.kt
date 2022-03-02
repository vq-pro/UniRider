package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity

interface DeviceConnector {
    fun connect(deviceAddress: String, onDone: (WheelData?) -> Unit)
    fun init(activity: Activity)
}
