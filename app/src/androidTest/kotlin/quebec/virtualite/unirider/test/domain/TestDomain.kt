package quebec.virtualite.unirider.test.domain

import android.content.Context
import cucumber.api.DataTable
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.unirider.bluetooth.sim.BluetoothServicesSim
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import java.lang.Integer.parseInt
import java.util.stream.Collectors.toList

class TestDomain(applicationContext: Context) {

    private val db = WheelDbImpl(applicationContext)
    private val wheels = HashMap<String, WheelEntity>()

    fun clear() {
        db.deleteAll()
    }

    fun forEachWheel(lambda: (Map.Entry<String, WheelEntity>) -> Unit) {
        wheels.forEach(lambda)
    }

    fun getWheel(name: String): WheelEntity? {
        return wheels[name]
    }

    fun getWheelId(name: String): Long {
        return if (wheels[name] != null)
            wheels[name]!!.id
        else
            wheels[name.substring(2)]!!.id
    }

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

    fun loadWheel(id: Long): WheelEntity? {
        return db.getWheel(id)
    }

    fun loadWheels(wheels: DataTable) {
        assertThat(
            wheels.topCells(),
            equalTo(listOf("Name", "Mileage", "Wh", "Voltage Min", "Voltage Reserve", "Voltage Max", "Charge Rate", "Sold"))
        )

        val wheelEntities = wheels.cells(1)
            .stream()
            .map { row ->
                var col = 0
                val name = row[col++]
                val mileage = parseInt(row[col++])
                val wh = parseInt(row[col++])
                val voltageMin = voltageOf(row[col++])
                val voltageReserve = voltageOf(row[col++])
                val voltageMax = voltageOf(row[col++])
                val chargeRate = voltsPerHourOf(row[col++])
                val isSold = parseYesNo(row[col++])

                WheelEntity(0, name, null, null, 0, mileage, wh, voltageMax, voltageMin, voltageReserve, voltageMax, chargeRate, isSold)
            }
            .collect(toList())

        db.saveWheels(wheelEntities)

        updateMapWheels()
    }

    fun locateWheel(name: String): WheelEntity? {
        return db.findWheel(name)
    }

    fun simulateDevice(device: DataTable) {
        assertThat(device.topCells(), equalTo(listOf("Bt Name", "Bt Address", "Km", "Mileage", "Voltage")))
        val deviceFields = device.cells(1)[0]

        BluetoothServicesSim
            .setDevice(BluetoothDevice(deviceFields[0], deviceFields[1]))
            .setKm(floatOf(deviceFields[2]))
            .setMileage(floatOf(deviceFields[3]))
            .setVoltage(voltageOf(deviceFields[4]))
    }

    fun updateWheelPreviousMileage(name: String, premileage: Int) {
        db.findWheel(name)?.let {
            db.saveWheel(it.copy(premileage = premileage))
            updateMapWheels()
        }
    }

    private fun floatOfWithSuffix(value: String, suffix: String): Float {
        assertThat(value, endsWith(suffix))
        return floatOf(value.substring(0, value.length - suffix.length))
    }

    private fun voltageOf(value: String): Float {
        return floatOfWithSuffix(value, "V")
    }

    private fun voltsPerHourOf(value: String): Float {
        return floatOfWithSuffix(value, "V/h")
    }

    private fun parseYesNo(value: String): Boolean {
        return "yes".equals(value, ignoreCase = true)
    }

    private fun updateMapWheels() {
        db.getWheels().forEach { wheel ->
            wheels[wheel.name] = wheel
        }
    }
}
