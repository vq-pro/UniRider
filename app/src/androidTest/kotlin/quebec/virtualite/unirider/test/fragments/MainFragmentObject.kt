package quebec.virtualite.unirider.test.fragments

import io.cucumber.datatable.DataTable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepUtils.ListViewField
import quebec.virtualite.unirider.commons.android.utils.StepUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepUtils.assertThatField
import quebec.virtualite.unirider.commons.android.utils.StepUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepUtils.tableHeader
import quebec.virtualite.unirider.commons.android.utils.StepUtils.tableRows
import quebec.virtualite.unirider.commons.android.utils.StepUtils.throwAssert
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.formatKm
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.parseKmNumeric
import quebec.virtualite.unirider.views.MainFragment
import quebec.virtualite.unirider.views.WheelRow
import java.util.stream.Collectors.toList

class MainFragmentObject(val app: TestApp, private val domain: TestDomain)
{
    val FIELD_NAME = ListViewField(R.id.row_name, "name")
    val NEW_WHEEL_ENTRY = "<New>"
    val SOLD_WHEEL_ENTRY = "<Sold>"

    fun addWheel(): WheelEntity
    {
        selectListViewItem(R.id.wheels, FIELD_NAME, NEW_WHEEL_ENTRY)

        return WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f, false)
    }

    fun selectWheel(wheelName: String): WheelEntity
    {
        val selectedWheel = domain.locateWheel(wheelName)
            ?: throwAssert("$wheelName is not defined")

        if (selectedWheel.isSold)
        {
            selectListViewItem(R.id.wheels, FIELD_NAME, SOLD_WHEEL_ENTRY)
            selectListViewItem(R.id.wheels, FIELD_NAME, "- $wheelName")
        } else
        {
            selectListViewItem(R.id.wheels, FIELD_NAME, wheelName)
        }

        return selectedWheel
    }

    fun toggleSoldWheels()
    {
        selectListViewItem(R.id.wheels, FIELD_NAME, SOLD_WHEEL_ENTRY)
    }

    fun validateBluetoothDeviceUndefined(selectedWheel: WheelEntity)
    {
        assertThat(selectedWheel.isConnected(), equalTo(false))
    }

    fun validateTotalMileage()
    {
        var totalMileage = 0
        domain.forEachWheel { (_, wheel) -> totalMileage += (wheel.totalMileage()) }

        assertThatField(R.id.total_mileage, hasText(formatKm(totalMileage)))
    }

    fun validateView()
    {
        assertThat(app.activeFragment(), equalTo(MainFragment::class.java))
    }

    fun validateUpdatedNameAndMileage(expectedId: Long, expectedName: String, expectedMileage: Int)
    {
        validateView()

        assertThatField(R.id.wheels, hasRow(WheelRow(expectedId, expectedName, expectedMileage)))
    }

    fun validateWheelIsGone(selectedWheel: WheelEntity)
    {
        assertThatField(
            "The wheel is not gone", R.id.wheels,
            not(hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage)))
        )
    }

    fun validateWheels(expectedWheels: DataTable)
    {
        assertThat(tableHeader(expectedWheels), equalTo(listOf("Name", "Mileage")))

        val expectedRows = tableRows(expectedWheels)
            .stream()
            .map { row ->
                val name = row[0]
                val mileageWithUnits = row[1]

                when (name)
                {
                    NEW_WHEEL_ENTRY ->
                    {
                        assertThat(mileageWithUnits, equalTo(""))
                        WheelRow(0, name, 0)
                    }

                    SOLD_WHEEL_ENTRY -> WheelRow(0, name, parseKmNumeric(mileageWithUnits))

                    else ->
                    {
                        val wheel = domain.getWheel(name)
                        when (wheel)
                        {
                            null -> WheelRow(0, name, 0)
                            else -> WheelRow(wheel!!.id, name, parseKmNumeric(mileageWithUnits))
                        }
                    }
                }
            }
            .collect(toList())

        assertThatField(R.id.wheels, hasRows(expectedRows))
    }
}
