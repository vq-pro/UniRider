package quebec.virtualite.unirider.test.fragments

import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isHidden
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.views.WheelRow
import quebec.virtualite.unirider.views.WheelViewFragment

class ViewFragmentObject(val app: TestApp, private val domain: TestDomain) {

    private var expectedDeviceName: String = ""
    private val expectedLiveWheelMileage = HashMap<String, Int>()

    fun charge() {
        click(R.id.button_charge)
    }

    fun connectAndAbort(deviceName: String, deviceAddr: String) {
        click(R.id.button_connect_view)
        assertThat(R.id.devices, hasRow(BluetoothDevice(deviceName, deviceAddr)))

        app.back()
        validateView()
    }

    fun connectTo(deviceName: String) {
        click(R.id.button_connect_view)
        selectListViewItem(R.id.devices, deviceName)

        expectedDeviceName = deviceName
    }

    fun editWheel() {
        click(R.id.button_edit)
    }

    fun reconnect() {
        click(R.id.button_connect_view)
    }

    fun setActualVoltageTo(voltage: String) {
        setText(R.id.edit_voltage_actual, stripV(voltage))
    }

    fun setDistanceTo(km: String) {
        setText(R.id.edit_km, strip(km, "km"))
    }

    fun useTheseUpdateMileageValues(updatedMileages: DataTable) {
        for (row in updatedMileages.cells(1)) {
            val wheelName = row[0]
            val expectedMileage = row[1].toInt() + domain.getWheel(wheelName)!!.premileage

            expectedLiveWheelMileage[wheelName] = expectedMileage
        }
    }

    fun validateBlankEstimates() {
        validateEstimates(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range"),
                    listOf("", "")
                )
            )
        )
    }

    fun validateBluetoothName() {
        assertThat(R.id.view_bt_name, hasText(expectedDeviceName))
    }

    fun validateCanCharge() {
        assertThat("Charge button is disabled", R.id.button_charge, isEnabled())
    }

    fun validateCannotCharge() {
        assertThat("Charge button is not disabled", R.id.button_charge, isDisabled())
    }

    fun validateEstimates(expectedEstimates: DataTable) {
        expectedEstimates.diff(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range"),
                    listOf(
                        getText(R.id.view_remaining_range),
                        getText(R.id.view_total_range)
                    )
                )
            )
        )
    }

    fun validateKm(expectedKm: Float) {
        assertThat(R.id.edit_km, hasText("$expectedKm"))
    }

    fun validateMileageUpdated(expectedMileage: String) {
        assertThat(R.id.view_mileage, hasText(expectedMileage))
    }

    fun validateName(expectedName: String) {
        assertThat(R.id.view_name, hasText(expectedName))
    }

    fun validatePercentage(expectedPercentage: String) {
        assertThat(R.id.view_battery, hasText(expectedPercentage))
    }

    fun validateSold(name: String) {
        assertThat("Wrong title", R.id.view_name, hasText("$name (Sold)"))
        assertThat("Charge button should not appear", R.id.button_charge, isHidden())
        assertThat("Connect button should not appear", R.id.button_connect_view, isHidden())
    }

    fun validateUnsold(selectedWheel: WheelEntity) {
        assertThat(
            "The wheel is gone", R.id.wheels,
            hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage))
        )
    }

    fun validateUpToDateMileage(selectedWheel: WheelEntity) {
        assertThat(R.id.view_mileage, hasText("${expectedLiveWheelMileage[selectedWheel.name]}"))
    }

    fun validateView() {
        assertThat(app.activeFragment(), equalTo(WheelViewFragment::class.java))
    }

    fun validateViewing(wheel: WheelEntity) {
        assertThat(app.activeFragment(), equalTo(WheelViewFragment::class.java))
        assertThat(R.id.view_name, hasText(wheel.name))
    }

    fun validateVoltageAndBattery(expectedVoltage: Float, expectedBattery: Float) {
        assertThat(R.id.edit_voltage_actual, hasText("$expectedVoltage"))
        assertThat(R.id.view_battery, hasText("$expectedBattery"))
    }

    private fun stripV(voltage: String): String = if (voltage.endsWith("V"))
        voltage.substring(0, voltage.length - 1)
    else
        voltage
}
