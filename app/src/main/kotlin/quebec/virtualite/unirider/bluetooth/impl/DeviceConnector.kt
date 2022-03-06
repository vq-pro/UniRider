package quebec.virtualite.unirider.bluetooth.impl

interface DeviceConnector {
    fun connect(deviceAddress: String, onConnected: (WheelData?) -> Unit)
}
