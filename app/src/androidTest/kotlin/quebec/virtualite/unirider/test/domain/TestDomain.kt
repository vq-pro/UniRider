package quebec.virtualite.unirider.test.domain

import android.content.Context
import cucumber.api.DataTable
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.bluetooth.sim.BluetoothServicesSim
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import java.lang.Integer.parseInt
import java.util.stream.Collectors.toList

private const val SOLD_PREFIX = "- "
private const val SUFFIX_KM = " km"
private const val SUFFIX_VOLTAGE = "V"

class TestDomain(applicationContext: Context) {

    companion object {

        fun formatKm(km: Int): String =
            "$km$SUFFIX_KM"

        fun formatVoltage(voltage: Float): String =
            "$voltage$SUFFIX_VOLTAGE"

        fun formatVoltage(voltage: String): String =
            if (!voltage.isEmpty())
                formatVoltage(floatOf(voltage))
            else
                ""

        fun parseKm(km: String): String =
            km.substringBefore(SUFFIX_KM)

        fun parseKmNumeric(km: String): Int =
            if (km.endsWith(SUFFIX_KM))
                intOf(km.substringBefore(SUFFIX_KM))
            else {
                assertThat(km, equalTo(""))
                0
            }

        fun parseVoltage(voltage: String): String =
            if (voltage.contains(SUFFIX_VOLTAGE))
                voltage.substringBefore(SUFFIX_VOLTAGE)
            else
                voltage
    }

    private val db = WheelDbImpl(applicationContext)
    private val wheels = HashMap<String, WheelEntity>()

    fun clear() {
        db.deleteAll()
    }

    private fun commaSeparatedList(itemsInAString: String) = itemsInAString.split(',').map { item -> item.trim() }

    fun forEachWheel(lambda: (Map.Entry<String, WheelEntity>) -> Unit) {
        wheels.forEach(lambda)
    }

    fun getWheel(name: String): WheelEntity? =
        if (name.startsWith(SOLD_PREFIX))
            wheels[name.substring(SOLD_PREFIX.length)]
        else
            wheels[name]

    fun loadConnectedWheels(wheels: DataTable) {
        assertThat(wheels.topCells(), equalTo(listOf("Name", "Bt Name", "Bt Address")))

        wheels.cells(1)
            .forEach { row ->
                val name = row[0]
                val btName = row[1]
                val btAddress = row[2]

                db.findWheel(name)?.let {
                    db.saveWheel(
                        it.copy(
                            name = name,
                            btName = btName,
                            btAddr = btAddress
                        )
                    )
                }
            }

        updateMapWheels()
    }

    fun loadWheel(id: Long): WheelEntity? =
        db.getWheel(id)

    fun loadWheels(wheels: DataTable) {
        assertThat(
            wheels.topCells(),
            equalTo(
                listOf(
                    "Name",
                    "Mileage",
                    "Wh",
                    "Voltage Min",
                    "Voltage Max",
                    "Charge Rate",
                    "Full Charge",
                    "Charger Offset",
                    "Distance Offset",
                    "Sold"
                )
            )
        )

        val wheelEntities = wheels.cells(1)
            .stream()
            .map { row ->
                var col = 0
                val name = row[col++]
                val mileage = parseInt(row[col++])
                val wh = parseInt(row[col++])
                val voltageMin = voltageOf(row[col++])
                val voltageMax = voltageOf(row[col++])
                val chargeRate = voltsPerHourOf(row[col++])
                val voltageFull = voltageOf(row[col++])
                val chargerOffset = voltageOf(row[col++])
                val distanceOffset = floatOf(row[col++])
                val isSold = parseYesNo(row[col++])

                WheelEntity(
                    0, name, null, null,
                    0, mileage, wh,
                    voltageMax, voltageMin,
                    chargeRate, voltageFull, chargerOffset, distanceOffset, isSold
                )
            }
            .collect(toList())

        db.saveWheels(wheelEntities)

        updateMapWheels()
    }

    fun locateWheel(name: String): WheelEntity? =
        db.findWheel(name)

    fun simulateDevice(device: DataTable) {
        assertThat(device.topCells(), equalTo(listOf("Bt Name", "Bt Address", "Km", "Mileage", "Voltage")))
        val deviceFields = device.cells(1)[0]

        BluetoothServicesSim
            .setDevice(BluetoothDevice(deviceFields[0], deviceFields[1]))
            .setKm(floatOf(deviceFields[2]))
            .setMileage(floatOf(deviceFields[3]))
            .setVoltages(voltagesOf(deviceFields[4]))
    }

    fun updateWheelPreviousMileage(name: String, premileage: Int) {
        db.findWheel(name)?.let {
            db.saveWheel(it.copy(premileage = premileage))
            updateMapWheels()
        }
    }

    private fun floatOfWithSuffix(value: String, suffix: String): Float {
        assertThat(value, endsWith(suffix))
        return floatOf(value.dropLast(suffix.length))
    }

    private fun voltageOf(value: String): Float =
        floatOfWithSuffix(value, SUFFIX_VOLTAGE)

    private fun voltagesOf(voltagesInAString: String): List<Float> =
        commaSeparatedList(voltagesInAString)
            .map { voltage -> floatOfWithSuffix(voltage, SUFFIX_VOLTAGE) }

    private fun voltsPerHourOf(value: String): Float =
        floatOfWithSuffix(value, "V/h")

    private fun parseYesNo(value: String): Boolean =
        "yes".equals(value, ignoreCase = true)

    private fun updateMapWheels() {
        db.getWheels().forEach { wheel ->
            wheels[wheel.name] = wheel
        }
    }
}
