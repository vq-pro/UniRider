package quebec.virtualite.unirider.test.fragments

import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.throwAssert
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.views.MainFragment
import quebec.virtualite.unirider.views.WheelRow
import java.util.stream.Collectors.toList

class TestMainFragment(val app: TestApp, val domain: TestDomain) {

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

    fun validateTotalMileage() {
        var totalMileage = 0
        domain.forEachWheel { (_, wheel) -> totalMileage += (wheel.totalMileage()) }

        assertThat(R.id.total_mileage, hasText("$totalMileage"))
    }

    fun validateView() {
        assertThat(app.activeFragment(), equalTo(MainFragment::class.java))
    }

    fun validateWheels(expectedWheels: DataTable) {
        assertThat(expectedWheels.topCells(), equalTo(listOf("Name", "Mileage")))

        val expectedRows = expectedWheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileage = intOf(row[1])
                val id = when (name) {
                    SOLD_WHEEL_ENTRY,
                    NEW_WHEEL_ENTRY -> 0

                    else -> domain.getWheelId(name)
                }

                WheelRow(id, name, mileage)
            }
            .collect(toList())

        assertThat(R.id.wheels, hasRows(expectedRows))
    }
}
