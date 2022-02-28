package quebec.virtualite.unirider.bluetooth

interface WheelConnector {
    fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((DeviceInfo) -> Unit)?)
    fun scan(onFound: ((Device) -> Unit)?)
}
