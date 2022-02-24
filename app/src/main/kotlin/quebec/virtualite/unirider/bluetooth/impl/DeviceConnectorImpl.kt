package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.stream.Collectors.toList

class DeviceConnectorImpl : DeviceConnector {

    private val LIVE_DATA = 0xA9.toByte()
    private val DISTANCE_TIME_FAN_DATA = 0xB9.toByte()
    private val NAME_TYPE_DATA = 0xBB.toByte()
    private val SERIAL_NUMBER_DATA = 0xB3.toByte()

    private val KING_SONG_NAME_DATA = byteArrayOf(
        0xAA.toByte(), 0x55.toByte(),
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x9B.toByte(), 0x14.toByte(),
        0x5A.toByte(), 0x5A.toByte()
    )

    private val KINGSONG_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )
    private val KINGSONG_DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private val KINGSONG_READ_CHARACTER: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val KINGSONG_SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

    private val AUTOCONNECT = false

    private var mVoltage: Int = 0
    private var mTotalDistance: Long = 0

    private lateinit var activity: Activity
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothGatt: BluetoothGatt
    private lateinit var wheelData: WheelData
    private lateinit var callback: (WheelData) -> Unit

    override fun connect(deviceAddress: String, callback: (WheelData) -> Unit) {

        this.callback = callback

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (bluetoothDevice == null) {
            throw AssertionError("Impossible to connect to device")
        }

        bluetoothGatt = bluetoothDevice.connectGatt(activity.baseContext, AUTOCONNECT, connectionCallback())
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

                        callback.invoke(wheelData)
                    }
                }
            }

            private fun detectWheel(gatt: BluetoothGatt): Boolean {
                val services = gatt.services
                if (services.size != KINGSONG_SERVICES.size)
                    return false

                val serviceUUIDs = services.stream()
                    .map { service -> "${service.uuid}" }
                    .collect(toList())
                return serviceUUIDs.equals(KINGSONG_SERVICES)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    val detected = detectWheel(gatt)

                    val service = gatt.getService(KINGSONG_SERVICE_UUID)
                    val notifyCharacteristic = service.getCharacteristic(KINGSONG_READ_CHARACTER)

                    val succes = gatt.setCharacteristicNotification(notifyCharacteristic, true)

                    val descriptor =
                        notifyCharacteristic.getDescriptor(KINGSONG_DESCRIPTOR_UUID)
                    descriptor.setValue(ENABLE_NOTIFICATION_VALUE)
                    val succesb = gatt.writeDescriptor(descriptor)

                    Log.i("onServicesDiscovered", "")
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)

                val data: ByteArray = characteristic.value

                if (characteristic.uuid.equals(KINGSONG_READ_CHARACTER)) {

                    val wheelData = decodeKingSong(gatt, data)
                }
                val b = true
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)

                val data: ByteArray = characteristic.value

                if (characteristic.uuid.equals(KINGSONG_READ_CHARACTER)) {

                    val wheelData = decodeKingSong(gatt, data)

                    var b = true
                }
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                super.onDescriptorWrite(gatt, descriptor, status)
            }

            override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
                super.onDescriptorRead(gatt, descriptor, status)
            }
        }
    }

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

    private var disconnected = false
    private var requestedKingSongNameData = false

    private fun decodeKingSong(gatt: BluetoothGatt, data: ByteArray): Boolean {

        Log.i("*** decodeKingSong - ", byteArrayToString(data))

        if (data.size < 20) {
            if (!requestedKingSongNameData) {
                writeBluetoothGattCharacteristic(gatt, KING_SONG_NAME_DATA)
                requestedKingSongNameData = true
            }
            return false
        }

        if (data[0] != 0xAA.toByte() || data[1] != 0x55.toByte())
            return false

        when (data[16]) {
            LIVE_DATA -> {

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

            DISTANCE_TIME_FAN_DATA -> {}
            NAME_TYPE_DATA -> {}
            SERIAL_NUMBER_DATA -> {}
            else -> {}
        }

        return true
    }

    private fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(KINGSONG_SERVICE_UUID)
        val characteristic = service.getCharacteristic(KINGSONG_READ_CHARACTER)
        characteristic.value = cmd
        characteristic.writeType = 1
        gatt.writeCharacteristic(characteristic)
    }
}
