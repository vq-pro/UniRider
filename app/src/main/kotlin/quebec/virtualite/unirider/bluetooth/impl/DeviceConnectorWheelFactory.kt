package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import java.util.stream.Collectors.toList

object DeviceConnectorWheelFactory {

    private val VETERAN_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )

    private val KINGSONG_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )

    fun detectWheel(gatt: BluetoothGatt): DeviceConnectorWheel {

        val serviceUUIDs = gatt.services.stream()
            .map { service -> "${service.uuid}" }
            .collect(toList())

        when (serviceUUIDs) {
            KINGSONG_SERVICES -> {
                return DeviceConnectorKingSong(gatt)
            }
            VETERAN_SERVICES -> {
                return DeviceConnectorVeteran(gatt)
            }
        }

        throw AssertionError("Unknown wheel!")
    }
}