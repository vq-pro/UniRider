package quebec.virtualite.unirider.services

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import java.util.function.Consumer

class DeviceScannerImpl : DeviceScanner {

    private lateinit var activity: Activity

    override fun init(activity: Activity) {
        this.activity = activity
    }

    override fun scan(whenDetecting: Consumer<String>) {

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val broadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        if (device == null || device.name == null) {
                            return
                        }

                        whenDetecting.accept(device.name)

//                        val deviceName = device.name
//                        val deviceAddress = device.address
//
//                        val b = true
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(broadcastReceiver, filter)

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery()
        }

        bluetoothAdapter.startDiscovery()
    }
}