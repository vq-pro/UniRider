package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString

private const val BLE = "*** BLE ***"

class DeviceConnectorImpl : DeviceConnector {

    private val DONT_AUTOCONNECT = false

    private var deviceAddress: String? = null
    private var deviceConnector: DeviceConnectorWheel? = null

    private var previous_gatt: BluetoothGatt? = null

    private lateinit var activity: Activity
    private lateinit var onDone: (WheelData?) -> Unit

    override fun connect(deviceAddress: String, onDone: (WheelData?) -> Unit) {
        previous_gatt?.disconnect()

        this.deviceAddress = deviceAddress
        this.onDone = onDone

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            ?: throw AssertionError("Impossible to connect to device")

        previous_gatt = bluetoothDevice.connectGatt(activity.baseContext, DONT_AUTOCONNECT, onBluetoothEvent(), TRANSPORT_LE)
    }

    override fun init(activity: Activity) {
        this.activity = activity
    }

    internal fun onBluetoothEvent(): BluetoothGattCallback {
        return object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)

                when (newState) {
                    STATE_CONNECTED -> {
                        Log.i(BLE, "Connected")
                        gatt.discoverServices()
                    }
                    STATE_DISCONNECTED -> {
                        val disconnectedDeviceAddress = gatt.device.address
                        gatt.close()

                        if (deviceConnector != null && deviceAddress.equals(disconnectedDeviceAddress)) {
                            val payload = deviceConnector?.wheelData
                            deviceConnector = null

                            Log.i(BLE, "Disconnected with results: $payload")
                            onDone.invoke(payload)
                        } else {
                            // Timeout while trying to establish a connection
                            Log.i(BLE, "Disconnected without results")
                            onDone.invoke(null)
                        }
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)

                Log.i(BLE, "onServicesDiscovered")

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    deviceConnector = DeviceConnectorWheelFactory.detectWheel(gatt)
                    deviceConnector!!.enableNotifications()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)

                Log.i(BLE, "onCharacteristicChanged - " + byteArrayToString(characteristic.value))

                deviceConnector!!.onCharacteristicChanged(characteristic)
            }
        }
    }
}
