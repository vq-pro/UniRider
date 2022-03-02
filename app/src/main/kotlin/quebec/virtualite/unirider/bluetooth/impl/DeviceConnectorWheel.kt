package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.util.*

abstract class DeviceConnectorWheel(val gatt: BluetoothGatt) {

    internal val UUID_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    internal val UUID_READ_CHARACTER: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    internal val UUID_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

    internal lateinit var wheelData: WheelData

    private var disconnected = false

    abstract fun decode(data: ByteArray): Boolean

    fun done(wheelData: WheelData) {

        Log.i("*** BLE ***", "${wheelData.mileage}")

        this.wheelData = wheelData

        if (!disconnected) {
            disableNotifications()
            gatt.disconnect()
            disconnected = true
        }
    }

    fun disableNotifications() {
        val service = gatt.getService(UUID_SERVICE)
        val notifyCharacteristic = service.getCharacteristic(UUID_READ_CHARACTER)
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, false))
            throw RuntimeException("Cannot request notifications")
    }

    fun enableNotifications() {
        val service = gatt.getService(UUID_SERVICE)
        val notifyCharacteristic = service.getCharacteristic(UUID_READ_CHARACTER)
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, true))
            throw RuntimeException("Cannot request notifications")

        val descriptor = notifyCharacteristic.getDescriptor(UUID_DESCRIPTOR)
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        if (!gatt.writeDescriptor(descriptor))
            throw RuntimeException("Cannot request remote notifications")
    }

    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {

        if (characteristic.uuid.equals(UUID_READ_CHARACTER)) {
            decode(characteristic.value)
        }
    }

    fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(UUID_SERVICE)
        val characteristic = service.getCharacteristic(UUID_READ_CHARACTER)
        characteristic.value = cmd
        characteristic.writeType = 1
        gatt.writeCharacteristic(characteristic)
    }
}