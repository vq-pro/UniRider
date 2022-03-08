package quebec.virtualite.commons.android.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.BLUETOOTH_SERVICE
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.bluetooth.BluetoothDeviceScanner

class BluetoothDeviceScannerImpl(private val activity: Activity) : BluetoothDeviceScanner {

    companion object {
        private val mapAlreadyFoundDevices = HashSet<String>()

        private var bluetoothScanner: BluetoothLeScanner? = null
        private var onDetected: ((BluetoothDevice) -> Unit)? = null

        private fun isUnique(deviceAddress: String): Boolean {
            if (mapAlreadyFoundDevices.contains(deviceAddress))
                return false

            mapAlreadyFoundDevices.add(deviceAddress)
            return true
        }

        private fun onDetectedInternal() = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                val deviceName = result?.device?.name
                val deviceAddress = result?.device?.address

                if (deviceName == null || deviceAddress == null)
                    return

                if (!isUnique(deviceAddress))
                    return

                Log.i(this.javaClass.simpleName, "Got device $deviceName - $deviceAddress")

                // Must use a class property for this, since the callback will never be updated from the first time we call startScan()
                onDetected?.invoke(BluetoothDevice(deviceName, deviceAddress))
            }
        }
    }

    override fun isStopped(): Boolean {
        return bluetoothScanner == null
    }

    override fun scan(onDetected: (BluetoothDevice) -> Unit) {
        stop()

        Companion.onDetected = onDetected
        mapAlreadyFoundDevices.clear()

        val bluetoothManager = activity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothScanner = bluetoothManager.adapter.bluetoothLeScanner

        bluetoothScanner!!.startScan(onDetectedInternal())
    }

    override fun stop() {
        if (!isStopped()) {
            onDetected = null

            bluetoothScanner?.flushPendingScanResults(object : ScanCallback() {})
            bluetoothScanner?.stopScan(object : ScanCallback() {})

            bluetoothScanner = null
        }
    }
}
