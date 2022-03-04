package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.util.*

private const val UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
private const val UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb"

abstract class DeviceConnectorWheel(val gatt: BluetoothGatt) {

    // Override these
    abstract fun decode(data: ByteArray): Boolean

    open fun uuidReadCharacter(): String {
        return "0000ffe1-0000-1000-8000-00805f9b34fb"
    }

    open fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(UUID.fromString(UUID_SERVICE))
        val characteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        characteristic.value = cmd
        characteristic.writeType = 1
        gatt.writeCharacteristic(characteristic)
    }
    // Override end

    internal lateinit var wheelData: WheelData

    private var disconnected = false

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
        val service = gatt.getService(UUID.fromString(UUID_SERVICE))
        val notifyCharacteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, false))
            throw RuntimeException("Cannot request notifications")
    }

    fun enableNotifications() {
        val service = gatt.getService(UUID.fromString(UUID_SERVICE))
        val notifyCharacteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, true))
            throw RuntimeException("Cannot request notifications")

        val descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(UUID_DESCRIPTOR))
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        if (!gatt.writeDescriptor(descriptor))
            throw RuntimeException("Cannot request remote notifications")
    }

    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {
        if (characteristic.uuid.equals(UUID.fromString(uuidReadCharacter()))) {
            decode(characteristic.value)
        }
    }
}