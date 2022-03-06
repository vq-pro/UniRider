package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.appcompat.app.AppCompatActivity.BLUETOOTH_SERVICE
import quebec.virtualite.unirider.bluetooth.Device

class DeviceScannerImpl : DeviceScanner {

    private lateinit var activity: Activity
    private lateinit var bluetoothScanner: BluetoothLeScanner
    private val mapFoundDevices = HashSet<String>()
    private var scanning = false

    // FIXME-1 Change this to a constructor?
    override fun init(activity: Activity) {
        this.activity = activity

        val bluetoothManager = activity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothScanner = bluetoothManager.adapter.bluetoothLeScanner
    }

    override fun isStopped(): Boolean {
        return scanning
    }

    // FIXME-1 This won't work if localization services are disabled (by default). Find a way to detect that and popup
    override fun scan(onDetected: (Device) -> Unit) {
        stop()

        scanning = true
        bluetoothScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                val deviceName = result?.device?.name
                val deviceAddress = result?.device?.address

                if (deviceName == null || deviceAddress == null)
                    return

                if (mapFoundDevices.contains(deviceAddress))
                    return

                onDetected.invoke(Device(deviceName, deviceAddress))
                mapFoundDevices.add(deviceAddress)
            }
        })
    }

    override fun stop() {
        if (scanning) {
            bluetoothScanner.stopScan(object : ScanCallback() {})
            scanning = false
        }
    }
}
