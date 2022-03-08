package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.bluetooth.BluetoothDeviceConnector
import quebec.virtualite.commons.android.bluetooth.BluetoothDeviceScanner
import quebec.virtualite.commons.android.bluetooth.impl.BluetoothDeviceConnectorImpl
import quebec.virtualite.commons.android.bluetooth.impl.BluetoothDeviceScannerImpl
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.bluetooth.WheelInfo

open class WheelConnectorImpl(activity: Activity) : WheelConnector {

    private val connector: BluetoothDeviceConnector = BluetoothDeviceConnectorImpl(activity)
    private val scanner: BluetoothDeviceScanner = BluetoothDeviceScannerImpl(activity)

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((WheelInfo?) -> Unit)?) {
        scanner.stop()
        connector.connect(deviceAddress!!) {
            onGotInfo!!.invoke(it as WheelInfo?)
        }
    }

    override fun scan(onFound: ((BluetoothDevice) -> Unit)?) {
        scanner.scan(onFound!!)
    }

    override fun stopScanning() {
        scanner.stop()
    }
}
