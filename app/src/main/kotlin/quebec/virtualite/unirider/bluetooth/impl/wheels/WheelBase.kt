package quebec.virtualite.unirider.bluetooth.impl.wheels

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.bluetooth.impl.CommonBluetoothDeviceImpl
import quebec.virtualite.unirider.bluetooth.WheelInfo

private const val UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb"
private const val UUID_READ_CHARACTER = "0000ffe1-0000-1000-8000-00805f9b34fb"
private const val UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb"

abstract class WheelBase(gatt: BluetoothGatt) : CommonBluetoothDeviceImpl(gatt) {

    internal lateinit var wheelInfo: WheelInfo

    override fun getPayload(): Any {
        return wheelInfo
    }

    override fun setPayload(payload: Any) {
        this.wheelInfo = payload as WheelInfo
    }

    override fun uuidDescriptor(): String {
        return UUID_DESCRIPTOR
    }

    override fun uuidReadCharacter(): String {
        return UUID_READ_CHARACTER
    }

    override fun uuidService(): String {
        return UUID_SERVICE
    }
}
