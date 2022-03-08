package quebec.virtualite.commons.android.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import java.util.*

abstract class CommonBluetoothDeviceImpl(val gatt: BluetoothGatt) {

    abstract fun decode(data: ByteArray): Boolean

    abstract fun setPayload(payload: Any)

    abstract fun getPayload(): Any

    abstract fun uuidDescriptor(): String
    abstract fun uuidReadCharacter(): String
    abstract fun uuidService(): String

    private var disconnected = false

    fun connected(payload: Any) {
        setPayload(payload)
        disconnect()
    }

    fun disableNotifications() {
        val service = gatt.getService(UUID.fromString(uuidService()))
        val notifyCharacteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, false))
            throw RuntimeException("Cannot request notifications")
    }

    fun enableNotifications() {
        val service = gatt.getService(UUID.fromString(uuidService()))
        val notifyCharacteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        if (!gatt.setCharacteristicNotification(notifyCharacteristic, true))
            throw RuntimeException("Cannot request notifications")

        val descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(uuidDescriptor()))
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        if (!gatt.writeDescriptor(descriptor))
            throw RuntimeException("Cannot request remote notifications")
    }

    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {
        if (characteristic.uuid.equals(UUID.fromString(uuidReadCharacter()))) {
            decode(characteristic.value)
        }
    }

    open fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(UUID.fromString(uuidService()))
        val characteristic = service.getCharacteristic(UUID.fromString(uuidReadCharacter()))
        characteristic.value = cmd
        characteristic.writeType = 1
        gatt.writeCharacteristic(characteristic)
    }

    private fun disconnect() {
        if (disconnected)
            return

        disableNotifications()
        gatt.disconnect()
        disconnected = true
    }
}
