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
import quebec.virtualite.commons.android.bluetooth.CommonBluetoothDeviceFactory
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString
import java.util.stream.Collectors

class BluetoothDeviceConnectorImpl(
    private val activity: Activity,
    factory: CommonBluetoothDeviceFactory

) : BluetoothDeviceConnector {

    companion object {
        private const val BLE = "*** BLE ***"
        private val DONT_AUTOCONNECT = false

        private var deviceAddress: String? = null
        private var deviceDriver: CommonBluetoothDeviceImpl? = null
        private var previousGatt: BluetoothGatt? = null

        private lateinit var factory: CommonBluetoothDeviceFactory
        private lateinit var onConnected: (Any?) -> Unit

        private fun fasterConnectIfLastAttemptIsStillOngoing() {
            previousGatt?.disconnect()
        }

        private fun getServices(gatt: BluetoothGatt): List<String> {
            return gatt.services.stream()
                .map { service -> "${service.uuid}" }
                .collect(Collectors.toList())
        }

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

                            if (deviceDriver != null && deviceAddress.equals(disconnectedDeviceAddress)) {
                                val payload = deviceDriver?.getPayload()
                                deviceDriver = null

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
                        deviceDriver = factory.getConnector(gatt, getServices(gatt))
                        deviceDriver!!.enableNotifications()
                    }
                }

                override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                    super.onCharacteristicChanged(gatt, characteristic)

                    Log.i(BLE, "onCharacteristicChanged - " + byteArrayToString(characteristic.value))

                    deviceDriver!!.onCharacteristicChanged(characteristic)
                }
            }
        }
    }

    init {
        Companion.factory = factory
    }

    override fun connect(deviceAddress: String, onConnected: (Any?) -> Unit) {
        fasterConnectIfLastAttemptIsStillOngoing()

        Companion.deviceAddress = deviceAddress
        Companion.onConnected = onConnected

        val bluetoothManager = activity.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothDevice = bluetoothManager.adapter.getRemoteDevice(Companion.deviceAddress)
            ?: throw AssertionError("Impossible to connect to device")

        previousGatt = bluetoothDevice.connectGatt(activity.baseContext, DONT_AUTOCONNECT, onBluetoothEvent(), TRANSPORT_LE)
    }
}
