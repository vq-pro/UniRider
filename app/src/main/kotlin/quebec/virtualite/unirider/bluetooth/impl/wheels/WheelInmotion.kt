package quebec.virtualite.unirider.bluetooth.impl.wheels

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt2
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt4
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.bluetooth.impl.wheels.Tester.intFromBytes
import java.util.*

private const val UUID_READ_CHARACTER = "0000ffe4-0000-1000-8000-00805f9b34fb"
private const val UUID_WRITE_CHARACTER = "0000ffe9-0000-1000-8000-00805f9b34fb"
private const val UUID_WRITE_SERVICE = "0000ffe5-0000-1000-8000-00805f9b34fb"

class WheelInmotion(gatt: BluetoothGatt) : WheelBase(gatt) {

    override fun decode(data: ByteArray): Boolean {

        if (data.size < 20) {
            return false
        }

        // FIXME-2 Complete Inmotion data decoding

        val data2 = byteArrayOf(
            0xAA.toByte(), 0xAA.toByte(),
            0x01, 0x01, 0x06, 0x0F, 0x12, 0, 0, 0, 0, 0, 0, 0, 0xFE.toByte(), 2, 1, 0, 6, 8,
            0xC0.toByte(), 0x65, 0, 0xD1.toByte(), 0xF8.toByte(), 0x5E, 0x2A, 0x70, 0x14, 0x2F, 0, 0, 0, 0, 0, 0, 0x61, 0x55, 0x55
        )

        for (i in 2..30) {
            val value = intFromBytes(data2, i).toUInt()
            val b = true
        }

        if (data[0] != 0xDC.toByte() || data[1] != 0x5A.toByte())
            return false

        val voltage = byteArrayInt2(data[4], data[5]) / 100f
        val temperature = byteArrayInt2(data[12], data[13]) / 1000f
        val mileage = byteArrayInt4(data[14], data[15], data[12], data[13]) / 1000f

        connected(WheelInfo(mileage, temperature, voltage))
        return true
    }

    override fun uuidReadCharacter(): String {
        return UUID_READ_CHARACTER
    }

    override fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(UUID.fromString(UUID_WRITE_SERVICE))
        val characteristic = service.getCharacteristic(UUID.fromString(UUID_WRITE_CHARACTER))

        val data = ByteArray(20)
        val i2 = cmd.size / 20
        val i3 = cmd.size - i2 * 20
        for (i4 in 0..i2) {
            System.arraycopy(cmd, i4 * 20, data, 0, 20)
            characteristic.value = data
            gatt.writeCharacteristic(characteristic)
        }
        if (i3 > 0) {
            System.arraycopy(cmd, i2 * 20, data, 0, i3)
            gatt.writeCharacteristic(characteristic)
        }
    }
}
