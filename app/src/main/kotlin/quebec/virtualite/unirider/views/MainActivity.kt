package quebec.virtualite.unirider.views

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import quebec.virtualite.unirider.BuildConfig
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.DeviceScanner
import quebec.virtualite.unirider.services.DeviceScannerImpl

class MainActivity : AppCompatActivity() {

    companion object {
        var scanner: DeviceScanner = DeviceScannerImpl()

        fun scanner(): DeviceScanner = scanner
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->

            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(broadcastReceiver, filter)

            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery()
            }

            bluetoothAdapter.startDiscovery()

            val text = if (BuildConfig.BLUETOOTH_MOCKED) {
                "Bluetooth is mocked"
            } else {
                "Bluetooth is live"
            }

            Snackbar
                .make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    if (device == null || device.name == null) {
                        return
                    }

                    val deviceName = device.name
                    val deviceAddress = device.address

                    val b = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
