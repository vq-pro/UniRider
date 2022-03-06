package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.BLUETOOTH_SERVICE
import quebec.virtualite.unirider.bluetooth.Device

class DeviceScannerImpl(val activity: Activity) : DeviceScanner {

    companion object {
        private val mapAlreadyFoundDevices = HashSet<String>()

        private var onDetectedNotifyCaller: ((Device) -> Unit)? = null

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
                onDetectedNotifyCaller?.invoke(Device(deviceName, deviceAddress))
            }
        }
    }

    private var bluetoothScanner: BluetoothLeScanner? = null

    override fun isStopped(): Boolean {
        return bluetoothScanner == null
    }

    override fun scan(onDetected: (Device) -> Unit) {
        stop()

        onDetectedNotifyCaller = onDetected
        mapAlreadyFoundDevices.clear()

        val bluetoothManager = activity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothScanner = bluetoothManager.adapter.bluetoothLeScanner

        bluetoothScanner!!.startScan(onDetectedInternal())
    }

    override fun stop() {
        if (!isStopped()) {
            onDetectedNotifyCaller = null

            bluetoothScanner?.flushPendingScanResults(object : ScanCallback() {})
            bluetoothScanner?.stopScan(object : ScanCallback() {})

            bluetoothScanner = null
        }
    }
}
