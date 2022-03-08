package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.bluetooth.CommonBluetoothDeviceFactory
import quebec.virtualite.commons.android.bluetooth.impl.CommonBluetoothDeviceImpl
import quebec.virtualite.unirider.bluetooth.impl.wheels.WheelInmotion
import quebec.virtualite.unirider.bluetooth.impl.wheels.WheelKingSong
import quebec.virtualite.unirider.bluetooth.impl.wheels.WheelVeteran

private val INMOTION_SERVICES: List<String> = listOf(
    "0000180a-0000-1000-8000-00805f9b34fb",
    "0000180f-0000-1000-8000-00805f9b34fb",
    "0000ffe0-0000-1000-8000-00805f9b34fb",
    "0000ffe5-0000-1000-8000-00805f9b34fb",
    "0000fff0-0000-1000-8000-00805f9b34fb",
    "0000ffd0-0000-1000-8000-00805f9b34fb",
    "0000ffc0-0000-1000-8000-00805f9b34fb",
    "0000ffb0-0000-1000-8000-00805f9b34fb",
    "0000ffa0-0000-1000-8000-00805f9b34fb",
    "0000ff90-0000-1000-8000-00805f9b34fb",
    "0000fc60-0000-1000-8000-00805f9b34fb",
    "0000fe00-0000-1000-8000-00805f9b34fb"
)

private val KINGSONG_SERVICES: List<String> = listOf(
    "00001800-0000-1000-8000-00805f9b34fb",
    "00001801-0000-1000-8000-00805f9b34fb",
    "0000180a-0000-1000-8000-00805f9b34fb",
    "0000fff0-0000-1000-8000-00805f9b34fb",
    "0000ffe0-0000-1000-8000-00805f9b34fb"
)

private val VETERAN_SERVICES: List<String> = listOf(
    "00001800-0000-1000-8000-00805f9b34fb",
    "00001801-0000-1000-8000-00805f9b34fb",
    "0000180a-0000-1000-8000-00805f9b34fb",
    "0000ffe0-0000-1000-8000-00805f9b34fb"
)

class WheelFactory : CommonBluetoothDeviceFactory {

    override fun getConnector(gatt: BluetoothGatt, services: List<String>): CommonBluetoothDeviceImpl {
        when (services) {
            INMOTION_SERVICES ->
                return WheelInmotion(gatt)

            KINGSONG_SERVICES ->
                return WheelKingSong(gatt)

            VETERAN_SERVICES ->
                return WheelVeteran(gatt)
        }

        throw AssertionError("Unknown wheel!")
    }
}
