package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import android.util.Log
import quebec.virtualite.commons.android.bluetooth.impl.CommonBluetoothDeviceConnector
import quebec.virtualite.unirider.bluetooth.WheelInfo

private const val UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
private const val UUID_READ_CHARACTER = "0000ffe1-0000-1000-8000-00805f9b34fb"
private const val UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb"

abstract class WheelConnector(gatt: BluetoothGatt) : CommonBluetoothDeviceConnector(gatt) {

    override fun uuidDescriptor(): String {
        return UUID_DESCRIPTOR
    }

    override fun uuidReadCharacter(): String {
        return UUID_READ_CHARACTER
    }

    override fun uuidService(): String {
        return UUID_SERVICE
    }

    internal lateinit var wheelInfo: WheelInfo

    private var disconnected = false

    fun done(wheelInfo: WheelInfo) {

        Log.i("*** BLE ***", "${wheelInfo.mileage}")

        this.wheelInfo = wheelInfo

        if (!disconnected) {
            disableNotifications()
            gatt.disconnect()
            disconnected = true
        }
    }

    override fun payload(): Any {
        return wheelInfo
    }
}
