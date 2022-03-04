package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt2
import quebec.virtualite.commons.android.utils.ByteArrayUtils.byteArrayInt4

class DeviceConnectorVeteran(gatt: BluetoothGatt) : DeviceConnectorWheel(gatt) {

    override fun decode(data: ByteArray): Boolean {

        if (data.size < 20) {
            return false
        }

        if (byteArrayInt2(data[0], data[1]) != 0xDC5A)
            return false

        val voltage = byteArrayInt2(data[4], data[5]) / 100f
        val temperature = byteArrayInt2(data[12], data[13]) / 1000f
        val mileage = byteArrayInt4(data[14], data[15], data[12], data[13]) / 1000f

        done(WheelData(mileage, temperature, voltage))
        return true
    }
}
