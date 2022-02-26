package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt2
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt4
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString
import kotlin.experimental.and

class DeviceConnectorKingSong(gatt: BluetoothGatt) : DeviceConnectorWheel(gatt) {

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

    private var requestedKingSongNameData = false

    override fun decode(data: ByteArray): Boolean {

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
                val voltage = byteArrayInt2(data[3], data[2]) / 100f
                val speed = byteArrayInt2(data[5], data[4])
                val mileage = byteArrayInt4(data[7], data[6], data[9], data[8]) / 1000f
                val current = data[10] and (0xFF + data[11] * 256).toByte()
                val temperature = byteArrayInt2(data[13], data[12]) / 100f
                val pedalMode = PedalModeType.valueFrom(data[14])

                done(WheelData(mileage, temperature, voltage))
            }
            NOTIFICATION_DISTANCE_TIME_FAN -> {
                val distance = byteArrayInt4(data[3], data[2], data[5], data[4]) / 1000f
                val elapsedSecondsDeviceIsOn = byteArrayInt2(data[7], data[6])
                val topSpeed = byteArrayInt2(data[9], data[8]) / 100f
                val fanStatus = data[12]
            }
            NOTIFICATION_CHARGE_CPU -> {
                val chargeCPU = byteArrayInt2(data[15], data[14])
            }
            NOTIFICATION_TOP_SPEED -> {
                val absoluteSpeedLimitOnThisWheel = byteArrayInt2(data[3], data[2]) / 100f
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
}
