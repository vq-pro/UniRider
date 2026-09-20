package quebec.virtualite.unirider.test.fragments

import io.cucumber.datatable.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepUtils.assertThatField
import quebec.virtualite.unirider.commons.android.utils.StepUtils.assertThatPolling
import quebec.virtualite.unirider.commons.android.utils.StepUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepUtils.isEmpty
import quebec.virtualite.unirider.commons.android.utils.StepUtils.isEnabled
import quebec.virtualite.unirider.commons.android.utils.StepUtils.isInvisible
import quebec.virtualite.unirider.commons.android.utils.StepUtils.longClick
import quebec.virtualite.unirider.commons.android.utils.StepUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepUtils.tableRows
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.parseKm
import quebec.virtualite.unirider.test.domain.TestDomain.Companion.parseVoltage
import quebec.virtualite.unirider.views.WheelConfirmationDisconnectFragment
import quebec.virtualite.unirider.views.WheelRow
import quebec.virtualite.unirider.views.WheelViewFragment

class ViewFragmentObject(val app: TestApp, private val domain: TestDomain)
{
    private var expectedDeviceName: String = ""
    private val expectedLiveWheelMileage = HashMap<String, Int>()

    fun charge()
    {
        click(R.id.button_charge)
        // FIXME-1 Fix charge button that you have to click twice
//        assertThatPolling({ app.activeFragment() }, equalTo(WheelChargeFragment::class.java))
//        click(R.id.button_connect_charge)
    }

    fun connectAndAbort(deviceName: String, deviceAddr: String)
    {
        click(R.id.button_connect)
        assertThatField(R.id.devices, hasRow(BluetoothDevice(deviceName, deviceAddr)))

        app.back()
        validateView()
    }

    fun connectTo(deviceName: String)
    {
        click(R.id.button_connect)
        selectListViewItem(R.id.devices, deviceName)

        expectedDeviceName = deviceName
    }

    fun disconnectConfirmation()
    {
        assertThatPolling({ app.activeFragment() }, equalTo(WheelConfirmationDisconnectFragment::class.java))
        click(R.id.button_disconnect_confirmation)
    }

    fun disconnectWheel()
    {
        longClick(R.id.view_bt_name)
    }

    fun editWheel()
    {
        assertThatPolling({ app.activeFragment() }, equalTo(WheelViewFragment::class.java))
        click(R.id.button_edit)
    }

    fun reconnect()
    {
        click(R.id.button_connect)
    }

    fun setActualVoltageTo(voltage: String)
    {
        setText(R.id.edit_voltage_actual, parseVoltage(voltage))
    }

    fun setDistanceTo(km: String)
    {
        setText(R.id.edit_km, parseKm(km))
    }

    fun useTheseUpdateMileageValues(updatedMileages: DataTable)
    {
        for (row in tableRows(updatedMileages))
        {
            val wheelName = row[0]
            val expectedMileage = row[1].toInt() + domain.getWheel(wheelName)!!.premileage

            expectedLiveWheelMileage[wheelName] = expectedMileage
        }
    }

    fun validateBlankEstimates()
    {
        validateEstimates(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range"), listOf("", "")
                )
            )
        )
    }

    fun validateBluetoothName()
    {
        assertThatField(R.id.view_bt_name, hasText(expectedDeviceName))
    }

    fun validateCanCharge(strCanCharge: String)
    {
        val canCharge = canOrCannot(strCanCharge)
        val message = "Charge button should ${if (!canCharge) "not " else ""} be enabled"
        assertThatField(message, R.id.button_charge, isEnabled(canCharge))
    }

    fun validateCanSeeBluetoothSettings(strShow: String)
    {
        val show = canOrCannot(strShow)
        val message = "Bluetooth settings ${if (show) "shouldn't" else "should"} be empty"
        assertThatField(message, R.id.view_bt_name, isEmpty(!show))
        assertThatField(message, R.id.view_bt_addr, isEmpty(!show))
    }

    fun validateEstimates(expectedEstimates: DataTable)
    {
        expectedEstimates.diff(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range"), listOf(
                        getText(R.id.view_remaining_range), getText(R.id.view_total_range)
                    )
                )
            )
        )
    }

    fun validateKm(expectedKm: Float)
    {
        assertThatField(R.id.edit_km, hasText("$expectedKm"))
    }

    fun validateMileageUpdated(expectedMileage: String)
    {
        assertThatField(R.id.view_mileage, hasText(expectedMileage))
    }

    fun validateName(expectedName: String)
    {
        assertThatField(R.id.view_name, hasText(expectedName))
    }

    fun validatePercentage(expectedPercentage: String)
    {
        assertThatField(R.id.view_battery, hasText(expectedPercentage))
    }

    fun validateSold(name: String)
    {
        assertThatField("Wrong title", R.id.view_name, hasText("$name (Sold)"))
        assertThatField("Charge button should not appear", R.id.button_charge, isInvisible())
        assertThatField("Connect button should not appear", R.id.button_connect, isInvisible())
    }

    fun validateUnsold(selectedWheel: WheelEntity)
    {
        assertThatField(
            "The wheel is gone",
            R.id.wheels,
            hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage))
        )
    }

    fun validateUpToDateMileage(selectedWheel: WheelEntity)
    {
        assertThatField(R.id.view_mileage, hasText("${expectedLiveWheelMileage[selectedWheel.name]}"))
    }

    fun validateView()
    {
        assertThatPolling({ app.activeFragment() }, equalTo(WheelViewFragment::class.java))
    }

    fun validateViewing(wheel: WheelEntity)
    {
        assertThatPolling({ app.activeFragment() }, equalTo(WheelViewFragment::class.java))
        assertThatField(R.id.view_name, hasText(wheel.name))
    }

    fun validateVoltageAndBattery(expectedVoltage: Float, expectedBattery: Float)
    {
        assertThatField(R.id.edit_voltage_actual, hasText("$expectedVoltage"))
        assertThatField(R.id.view_battery, hasText("$expectedBattery"))
    }

    private fun canOrCannot(canOrCannot: String): Boolean = "can" == canOrCannot
}
