package quebec.virtualite.unirider.bluetooth.impl

import quebec.virtualite.unirider.bluetooth.WheelInfo

interface BluetoothDeviceConnector {
    fun connect(deviceAddress: String, onConnected: (WheelInfo?) -> Unit)
}
