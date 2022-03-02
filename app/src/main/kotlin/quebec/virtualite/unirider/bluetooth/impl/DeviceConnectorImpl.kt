package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString

class DeviceConnectorImpl : DeviceConnector {

    private val DONT_AUTOCONNECT = false

    private var deviceAddress: String? = null
    private var deviceConnector: DeviceConnectorWheel? = null

    private lateinit var activity: Activity
    private lateinit var onDone: (WheelData?) -> Unit

    override fun connect(deviceAddress: String, onDone: (WheelData?) -> Unit) {

        this.deviceAddress = deviceAddress
        this.onDone = onDone

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            ?: throw AssertionError("Impossible to connect to device")

        bluetoothDevice.connectGatt(activity.baseContext, DONT_AUTOCONNECT, connectionCallback())
    }

    override fun init(activity: Activity) {
        this.activity = activity
    }

    internal fun connectionCallback(): BluetoothGattCallback {
        return object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)

                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        gatt.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        val disconnectedDeviceAddress = gatt.device.address
                        gatt.close()

                        if (deviceAddress == disconnectedDeviceAddress && deviceConnector != null) {
                            deviceConnector = null
                            onDone.invoke(deviceConnector?.wheelData)
                        } else {
                            // Timeout while trying to establish a connection
                            onDone.invoke(null)
                        }
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)

                Log.i("*** BLE ***", "onServicesDiscovered")

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    deviceConnector = DeviceConnectorWheelFactory.detectWheel(gatt)
                    deviceConnector!!.enableNotifications()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)

                Log.i("*** BLE ***", "onCharacteristicChanged - " + byteArrayToString(characteristic.value))

                deviceConnector!!.onCharacteristicChanged(characteristic)
            }
        }
    }
}
