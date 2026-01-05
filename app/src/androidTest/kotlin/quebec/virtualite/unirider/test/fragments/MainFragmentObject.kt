package quebec.virtualite.unirider.test.fragments

import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.throwAssert
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.formatKm
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.parseKmNumeric
import quebec.virtualite.unirider.views.MainFragment
import quebec.virtualite.unirider.views.WheelRow
import java.util.stream.Collectors.toList

class MainFragmentObject(val app: TestApp, private val domain: TestDomain) {

    val NEW_WHEEL_ENTRY = "<New>"
    val SOLD_WHEEL_ENTRY = "<Sold>"

    fun addWheel(): WheelEntity {
        val newWheel = WheelEntity(0L, "", "", "", 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false)
        selectListViewItem(R.id.wheels, "name", NEW_WHEEL_ENTRY)

        return newWheel
    }

    fun selectWheel(wheelName: String): WheelEntity {
        val selectedWheel = domain.locateWheel(wheelName)
            ?: throwAssert("$wheelName is not defined")

        if (selectedWheel.isSold) {
            selectListViewItem(R.id.wheels, "name", SOLD_WHEEL_ENTRY)
            selectListViewItem(R.id.wheels, "name", "- $wheelName")
        } else {
            selectListViewItem(R.id.wheels, "name", wheelName)
        }

        return selectedWheel
    }

    fun toggleSoldWheels() {
        selectListViewItem(R.id.wheels, "name", SOLD_WHEEL_ENTRY)
    }

    fun validateBluetoothDeviceUndefined(selectedWheel: WheelEntity) {
        assertThat(selectedWheel.isConnected(), equalTo(false))
    }

    fun validateTotalMileage() {
        var totalMileage = 0
        domain.forEachWheel { (_, wheel) -> totalMileage += (wheel.totalMileage()) }

        assertThat(R.id.total_mileage, hasText(formatKm(totalMileage)))
    }

    fun validateView() {
        assertThat(app.activeFragment(), equalTo(MainFragment::class.java))
    }

    fun validateUpdatedNameAndMileage(expectedId: Long, expectedName: String, expectedMileage: Int) {
        validateView()
        assertThat(R.id.wheels, hasRow(WheelRow(expectedId, expectedName, expectedMileage)))
    }

    fun validateWheelIsGone(selectedWheel: WheelEntity) {
        assertThat(
            "The wheel is not gone", R.id.wheels,
            not(hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage)))
        )
    }

    fun validateWheels(expectedWheels: DataTable) {
        assertThat(expectedWheels.topCells(), equalTo(listOf("Name", "Mileage")))

        val expectedRows = expectedWheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileageWithUnits = row[1]

                when (name) {
                    NEW_WHEEL_ENTRY -> {
                        assertThat(mileageWithUnits, equalTo(""))
                        WheelRow(0, name, 0)
                    }

                    SOLD_WHEEL_ENTRY -> WheelRow(0, name, parseKmNumeric(mileageWithUnits))

                    else -> WheelRow(domain.getWheel(name)!!.id, name, parseKmNumeric(mileageWithUnits))
                }
            }
            .collect(toList())

        assertThat(R.id.wheels, hasRows(expectedRows))
    }
}
