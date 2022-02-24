package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.stream.Collectors.toList

class DeviceConnectorImpl : DeviceConnector {

    private val AUTOCONNECT = false

    private lateinit var activity: Activity
    private lateinit var callback: (WheelData) -> Unit
    private lateinit var wheel: DeviceConnectorWheel

    override fun connect(deviceAddress: String, callback: (WheelData) -> Unit) {

        this.callback = callback

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            ?: throw AssertionError("Impossible to connect to device")

        bluetoothDevice.connectGatt(activity.baseContext, AUTOCONNECT, connectionCallback())
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
                        gatt.close()
                        callback.invoke(wheel.wheelData)
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)

                Log.i("*** BLE ***", "onServicesDiscovered")

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    wheel = DeviceConnectorWheelFactory.detectWheel(gatt)
                    wheel.enableNotifications()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)

                wheel.onCharacteristicChanged(characteristic)
            }
        }
    }

    object DeviceConnectorWheelFactory {

        private val KINGSONG_SERVICES: List<String> = listOf(
            "00001800-0000-1000-8000-00805f9b34fb",
            "00001801-0000-1000-8000-00805f9b34fb",
            "0000180a-0000-1000-8000-00805f9b34fb",
            "0000fff0-0000-1000-8000-00805f9b34fb",
            "0000ffe0-0000-1000-8000-00805f9b34fb"
        )

        fun detectWheel(gatt: BluetoothGatt): DeviceConnectorWheel {

            val services = gatt.services
            if (services.size != KINGSONG_SERVICES.size)
                throw AssertionError("Not a KingSong wheel!")

            val serviceUUIDs = gatt.services.stream()
                .map { service -> "${service.uuid}" }
                .collect(toList())

            if (!serviceUUIDs.equals(KINGSONG_SERVICES))
                throw AssertionError("Not a KingSong wheel!")

            return DeviceConnectorKingSong(gatt)
        }
    }

    abstract class DeviceConnectorWheel {

        lateinit var wheelData: WheelData

        abstract fun enableNotifications()
        abstract fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic)

        internal fun byteArrayInt2(low: Byte, high: Byte): Int {

            return ((high.toUByte() * 0x100u) + low.toUByte()).toInt()
        }

        internal fun byteArrayInt4(low1: Byte, high1: Byte, low2: Byte, high2: Byte): Long {

            return (byteArrayInt2(low1, high1).toULong() * 0x10000u
                    + byteArrayInt2(low2, high2).toULong()).toLong()
        }

        internal fun byteArrayToString(data: ByteArray): String {
            val string = StringBuilder()
            for (byte in data) {
                if (string.isNotEmpty())
                    string.append("-")
                val value = byte.toUByte().toString(16).uppercase()
                string.append(if (value.length == 1) "0$value" else value)
            }

            return string.toString()
        }
    }

    class DeviceConnectorKingSong(val gatt: BluetoothGatt) : DeviceConnectorWheel() {

        private val NOTIFICATION_DISTANCE_TIME_FAN = 0xB9.toByte()
        private val NOTIFICATION_LIVE = 0xA9.toByte()
        private val NOTIFICATION_NAME_TYPE = 0xBB.toByte()
        private val NOTIFICATION_SERIAL_NUMBER = 0xB3.toByte()

        private val REQUEST_NAME_NOTIFICATION = byteArrayOf(
            0xAA.toByte(), 0x55.toByte(),
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x9B.toByte(), 0x14.toByte(),
            0x5A.toByte(), 0x5A.toByte()
        )

        private val UUID_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val UUID_READ_CHARACTER: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
        private val UUID_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

        private var disconnected = false
        private var requestedKingSongNameData = false

        private var mVoltage: Int = 0
        private var mTotalDistance: Long = 0

        override fun enableNotifications() {

            val service = gatt.getService(UUID_SERVICE)
            val notifyCharacteristic = service.getCharacteristic(UUID_READ_CHARACTER)
            if (!gatt.setCharacteristicNotification(notifyCharacteristic, true))
                throw RuntimeException("Cannot request local notifications")

            val descriptor = notifyCharacteristic.getDescriptor(UUID_DESCRIPTOR)
            descriptor.setValue(ENABLE_NOTIFICATION_VALUE)
            if (!gatt.writeDescriptor(descriptor))
                throw RuntimeException("Cannot request remote notifications")
        }

        override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {

            if (characteristic.uuid.equals(UUID_READ_CHARACTER)) {
                decode(characteristic.value)
            }
        }

        private fun decode(data: ByteArray): Boolean {

            Log.i("*** decodeKingSong - ", byteArrayToString(data))

            if (data.size < 20) {
                if (!requestedKingSongNameData) {
                    writeBluetoothGattCharacteristic(gatt, REQUEST_NAME_NOTIFICATION)
                    requestedKingSongNameData = true
                }
                return false
            }

            if (data[0] != 0xAA.toByte() || data[1] != 0x55.toByte())
                return false

            when (data[16]) {
                NOTIFICATION_LIVE -> {

                    mVoltage = byteArrayInt2(data[2], data[3])
                    mTotalDistance = byteArrayInt4(data[6], data[7], data[8], data[9])
                    val mileage = mTotalDistance / 1000.0f

                    wheelData = WheelData(mileage, 0.0f, 0.0f)

                    Log.i("*** decodeKingSong 2 - ", mileage.toString())
                    if (!disconnected) {
                        gatt.disconnect()
                        disconnected = true
                    }
                }

                NOTIFICATION_DISTANCE_TIME_FAN -> {}
                NOTIFICATION_NAME_TYPE -> {}
                NOTIFICATION_SERIAL_NUMBER -> {}
                else -> {}
            }

            return true
        }

        private fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
            val service = gatt.getService(UUID_SERVICE)
            val characteristic = service.getCharacteristic(UUID_READ_CHARACTER)
            characteristic.value = cmd
            characteristic.writeType = 1
            gatt.writeCharacteristic(characteristic)
        }
    }
}
