package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt2
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt4
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString
import java.util.*
import kotlin.experimental.and

class DeviceConnectorKingSong(val gatt: BluetoothGatt) : DeviceConnectorWheel() {

    private val NOTIFICATION_CHARGE_CPU = 0xF5.toByte()
    private val NOTIFICATION_DISTANCE_TIME_FAN = 0xB9.toByte()
    private val NOTIFICATION_LIVE = 0xA9.toByte()
    private val NOTIFICATION_NAME_TYPE = 0xBB.toByte()
    private val NOTIFICATION_SERIAL_NUMBER = 0xB3.toByte()
    private val NOTIFICATION_TOP_SPEED = 0xF6.toByte()

    private val REQUEST_NAME_NOTIFICATION = byteArrayOf(
        0xAA.toByte(), 0x55.toByte(),
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x9B.toByte(), 0x14.toByte(),
        0x5A.toByte(), 0x5A.toByte()
    )

    private val REQUEST_SERIAL_NOTIFICATION = byteArrayOf(
        0xAA.toByte(), 0x55.toByte(),
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x63.toByte(), 0x14.toByte(),
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

        val dataS = byteArrayToString(data)

        when (data[16]) {
            NOTIFICATION_LIVE -> {
                val voltage = byteArrayInt2(data[2], data[3]) / 100f
                val speed = byteArrayInt2(data[4], data[5])
                val mileage = byteArrayInt4(data[6], data[7], data[8], data[9]) / 1000f
                val current = data[10] and (0xFF + data[11] * 256).toByte()
                val temperature = byteArrayInt2(data[12], data[13]) / 100f
                val pedalMode = PedalModeType.valueFrom(data[14])

                wheelData = WheelData(mileage, temperature, voltage)

                Log.i("*** BLE ***", "${wheelData.mileage}")
                if (!disconnected) {
                    gatt.disconnect()
                    disconnected = true
                }
            }
            NOTIFICATION_DISTANCE_TIME_FAN -> {
                val distance = byteArrayInt4(data[2], data[3], data[4], data[5]) / 1000f
                val elapsedSecondsDeviceIsOn = byteArrayInt2(data[6], data[7])
                val topSpeed = byteArrayInt2(data[8], data[9]) / 100f
                val fanStatus = data[12]
            }
            NOTIFICATION_CHARGE_CPU -> {
                val chargeCPU = byteArrayInt2(data[14], data[15])
            }
            NOTIFICATION_TOP_SPEED -> {
                val absoluteSpeedLimitOnThisWheel = byteArrayInt2(data[2], data[3]) / 100f
            }
            NOTIFICATION_NAME_TYPE -> {}
            NOTIFICATION_SERIAL_NUMBER -> {}
            else -> {}
        }

        return true
    }

    enum class PedalModeType(val value: Byte) {
        HARD(0),
        MEDIUM(1),
        SOFT(2);

        companion object {
            fun valueFrom(value: Byte): PedalModeType? {
                values().forEach { entry ->
                    if (entry.value == value)
                        return entry
                }
                return null
            }
        }
    }

    private fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(UUID_SERVICE)
        val characteristic = service.getCharacteristic(UUID_READ_CHARACTER)
        characteristic.value = cmd
        characteristic.writeType = 1
        gatt.writeCharacteristic(characteristic)
    }
}