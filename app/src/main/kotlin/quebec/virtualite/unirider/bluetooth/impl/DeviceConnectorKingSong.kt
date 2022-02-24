package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import quebec.virtualite.commons.android.utils.ByteArrayUtils
import java.util.*

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

    override fun enableNotifications() {

        val service = gatt.getService(UUID_SERVICE)
        val notifyCharacteristic = service.getCharacteristic(UUID_READ_CHARACTER)
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, true))
            throw RuntimeException("Cannot request local notifications")

        val descriptor = notifyCharacteristic.getDescriptor(UUID_DESCRIPTOR)
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        if (!gatt.writeDescriptor(descriptor))
            throw RuntimeException("Cannot request remote notifications")
    }

    override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {

        if (characteristic.uuid.equals(UUID_READ_CHARACTER)) {
            decode(characteristic.value)
        }
    }

    private fun decode(data: ByteArray): Boolean {

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

                val voltage = ByteArrayUtils.byteArrayInt2(data[2], data[3])
                val totalDistance = ByteArrayUtils.byteArrayInt4(data[6], data[7], data[8], data[9])
                val mileage = totalDistance / 1000.0f

                wheelData = WheelData(mileage, 0.0f, 0.0f)

                Log.i("*** BLE ***", mileage.toString())
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