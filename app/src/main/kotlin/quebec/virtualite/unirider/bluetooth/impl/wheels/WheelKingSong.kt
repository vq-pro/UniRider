package quebec.virtualite.unirider.bluetooth.impl.wheels

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt2
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt4
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayToString
import quebec.virtualite.unirider.bluetooth.WheelInfo
import kotlin.experimental.and

class WheelKingSong(gatt: BluetoothGatt) : WheelBase(gatt) {

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

    private var doneRequestingMoreNotifications = false

    private var km: Float? = null
    private var mileage: Float? = null
    private var temperature: Float? = null
    private var voltage: Float? = null

    override fun decode(data: ByteArray): Boolean {

        if (data.size < 20) {
            if (!doneRequestingMoreNotifications) {
                writeBluetoothGattCharacteristic(gatt, REQUEST_NAME_NOTIFICATION)
                doneRequestingMoreNotifications = true
            }
            return false
        }

        if (byteArrayInt2(data[0], data[1]) != 0xAA55)
            return false

        val dataS = byteArrayToString(data)

        when (data[16]) {
            NOTIFICATION_LIVE -> {
                voltage = byteArrayInt2(data[3], data[2]) / 100f
                val speed = byteArrayInt2(data[5], data[4])
                mileage = byteArrayInt4(data[7], data[6], data[9], data[8]) / 1000f
                val current = data[10] and (0xFF + data[11] * 256).toByte()
                temperature = byteArrayInt2(data[13], data[12]) / 100f
                val pedalMode = PedalModeType.valueFrom(data[14])
            }
            NOTIFICATION_DISTANCE_TIME_FAN -> {
                km = byteArrayInt4(data[3], data[2], data[5], data[4]) / 1000f
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

        if (km != null && mileage != null && temperature != null && voltage != null) {
            connected(WheelInfo(km!!, mileage!!, temperature!!, voltage!!))
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
