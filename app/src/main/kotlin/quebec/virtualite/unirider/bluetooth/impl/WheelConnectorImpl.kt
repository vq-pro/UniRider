package quebec.virtualite.unirider.bluetooth.impl

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.views.MainActivity

open class WheelConnectorImpl(mainActivity: MainActivity) : WheelConnector {

    private val connector: DeviceConnector = DeviceConnectorImpl()
    private val scanner: DeviceScanner = DeviceScannerImpl()

    init {
        connector.init(mainActivity)
        scanner.init(mainActivity)
    }

    override fun getDeviceInfo(deviceAddress: String?, function: ((DeviceInfo) -> Unit)?) {
        scanner.stop()
        connector.connect(deviceAddress!!) { wheelData ->
            function!!.invoke(DeviceInfo(wheelData.mileage, wheelData.voltage))
        }
    }

    override fun scan(onFound: ((Device) -> Unit)?) {
        scanner.scan(onFound!!)
    }
}
