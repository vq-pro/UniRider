package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import java.util.stream.Collectors.toList

object DeviceConnectorWheelFactory {

    private val KINGSONG_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )

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

    private val VETERAN_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )

    fun detectWheel(gatt: BluetoothGatt): WheelConnector {

        val serviceUUIDs = gatt.services.stream()
            .map { service -> "${service.uuid}" }
            .collect(toList())

        when (serviceUUIDs) {
            KINGSONG_SERVICES ->
                return DeviceConnectorKingSong(gatt)

            INMOTION_SERVICES ->
                return DeviceConnectorInmotion(gatt)

            VETERAN_SERVICES ->
                return DeviceConnectorVeteran(gatt)
        }

        throw AssertionError("Unknown wheel!")
    }
}