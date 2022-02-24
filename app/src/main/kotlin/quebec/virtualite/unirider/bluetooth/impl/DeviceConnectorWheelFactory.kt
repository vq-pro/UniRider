package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGatt
import java.util.stream.Collectors

object DeviceConnectorWheelFactory {

    private val KINGSONG_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )

    fun detectWheel(gatt: BluetoothGatt): DeviceConnectorWheel {

        val services = gatt.services
        if (services.size != KINGSONG_SERVICES.size)
            throw AssertionError("Not a KingSong wheel!")

        val serviceUUIDs = gatt.services.stream()
            .map { service -> "${service.uuid}" }
            .collect(Collectors.toList())

        if (!serviceUUIDs.equals(KINGSONG_SERVICES))
            throw AssertionError("Not a KingSong wheel!")

        return DeviceConnectorKingSong(gatt)
    }
}