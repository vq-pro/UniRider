package quebec.virtualite.unirider.bluetooth

interface WheelConnector {
    fun getDeviceInfo(deviceAddress: String?, function: ((DeviceInfo) -> Unit)?)
    fun scan(found: ((Device) -> Unit)?)
}
