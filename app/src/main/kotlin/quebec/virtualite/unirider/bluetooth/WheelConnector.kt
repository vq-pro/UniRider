package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice

interface WheelConnector {
    fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((DeviceInfo?) -> Unit)?)
    fun scan(onFound: ((BluetoothDevice) -> Unit)?)
    fun stopScanning()
}
