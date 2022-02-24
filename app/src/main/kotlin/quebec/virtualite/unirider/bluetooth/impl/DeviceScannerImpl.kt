package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import quebec.virtualite.unirider.bluetooth.Device

class DeviceScannerImpl : DeviceScanner {

    private lateinit var activity: Activity
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun init(activity: Activity) {
        this.activity = activity

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    override fun isStopped(): Boolean {
        return !bluetoothAdapter.isDiscovering
    }

    override fun scan(whenDetecting: (Device) -> Unit) {

        val broadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        if (device == null || device.name == null) {
                            return
                        }

                        whenDetecting.invoke(Device(device.name, device.address))
                    }
                }
            }
        }

        stop()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(broadcastReceiver, filter)

        bluetoothAdapter.startDiscovery()
    }

    override fun stop() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery()
        }
    }
}
