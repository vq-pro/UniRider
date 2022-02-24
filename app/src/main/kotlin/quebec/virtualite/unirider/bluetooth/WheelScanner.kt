package quebec.virtualite.unirider.bluetooth

interface WheelScanner {
    fun scan(found: ((Device) -> Unit)?)
    fun getDeviceInfo(deviceAddress: String?, gotInfo: ((DeviceInfo) -> Unit)?)
}
