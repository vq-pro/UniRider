package quebec.virtualite.commons.android.bluetooth

interface BluetoothDeviceConnector {
    fun connect(deviceAddress: String, onConnected: (Any?) -> Unit)
}
