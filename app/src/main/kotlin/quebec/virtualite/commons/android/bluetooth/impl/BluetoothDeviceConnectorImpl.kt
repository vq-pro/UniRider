package quebec.virtualite.commons.android.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.BLUETOOTH_SERVICE
import quebec.virtualite.commons.android.bluetooth.BluetoothDeviceConnector
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString
import quebec.virtualite.unirider.bluetooth.impl.DeviceConnectorWheel
import quebec.virtualite.unirider.bluetooth.impl.DeviceConnectorWheelFactory

class BluetoothDeviceConnectorImpl(private val activity: Activity) : BluetoothDeviceConnector {

    companion object {
        private const val BLE = "*** BLE ***"

        private var deviceAddress: String? = null
        private var deviceConnector: DeviceConnectorWheel? = null

        private lateinit var onConnected: (Any?) -> Unit

        private fun onBluetoothEvent(): BluetoothGattCallback {
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
                                val payload = deviceConnector?.wheelInfo
                                deviceConnector = null

                                Log.i(BLE, "Disconnected with results: $payload")
                                onConnected.invoke(payload)
                            } else {
                                // Timeout while trying to establish a connection
                                Log.i(BLE, "Disconnected without results")
                                onConnected.invoke(null)
                            }
                        }
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)

                    Log.i(BLE, "onServicesDiscovered")

                    if (status == GATT_SUCCESS) {
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

    private val DONT_AUTOCONNECT = false

    private var previousGatt: BluetoothGatt? = null

    override fun connect(deviceAddress: String, onConnected: (Any?) -> Unit) {
        fasterConnectIfLastAttemptIsStillOngoing()

        Companion.deviceAddress = deviceAddress
        Companion.onConnected = onConnected

        val bluetoothManager = activity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothDevice = bluetoothManager.adapter.getRemoteDevice(Companion.deviceAddress)
            ?: throw AssertionError("Impossible to connect to device")

        previousGatt = bluetoothDevice.connectGatt(activity.baseContext, DONT_AUTOCONNECT, onBluetoothEvent(), TRANSPORT_LE)
    }

    private fun fasterConnectIfLastAttemptIsStillOngoing() {
        previousGatt?.disconnect()
    }
}
