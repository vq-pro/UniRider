package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector

open class WheelConnectorImpl(activity: Activity) : WheelConnector {

    private val connector: DeviceConnector = DeviceConnectorImpl(activity)
    private val scanner: DeviceScanner = DeviceScannerImpl(activity)

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((DeviceInfo?) -> Unit)?) {
        scanner.stop()
        connector.connect(deviceAddress!!) { wheelData ->
            onGotInfo!!.invoke(wheelData?.let { DeviceInfo(wheelData.mileage, wheelData.voltage) })
        }
    }

    override fun scan(onFound: ((Device) -> Unit)?) {
        scanner.scan(onFound!!)
    }

    override fun stopScanning() {
        scanner.stop()
    }
}
