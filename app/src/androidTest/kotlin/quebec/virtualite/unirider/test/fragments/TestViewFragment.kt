package quebec.virtualite.unirider.test.fragments

import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.views.WheelViewFragment

class TestViewFragment(val app: TestApp, val domain: TestDomain) {

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

    fun connectTo(deviceName: String): String {
        click(R.id.button_connect_view)
        selectListViewItem(R.id.devices, deviceName)

        return deviceName
    }

    fun reconnect() {
        click(R.id.button_connect_view)
    }

    fun setActualVoltageTo(voltage: String) {
        setText(R.id.edit_voltage_actual, voltage)
    }

    fun setDistanceTo(km: String) {
        setText(R.id.edit_km, strip(km, "km"))
    }

    fun setStartingVoltageTo(voltage: String) {
        setText(R.id.edit_voltage_start, strip(voltage, "V"))
    }

    fun useTheseUpdateMileageValues(updatedMileages: DataTable) {
        for (row in updatedMileages.cells(1)) {
            val wheelName = row[0]
            val expectedMileage = row[1].toInt() + domain.getWheel(wheelName)!!.premileage

            expectedLiveWheelMileage[wheelName] = expectedMileage
        }
    }

    fun validateBluetootName(expectedDeviceName: String) {
        assertThat(R.id.view_bt_name, hasText(expectedDeviceName))
    }

    fun validateKm(expectedKm: Float) {
        assertThat(R.id.edit_km, hasText("$expectedKm"))
    }

    fun validateMileageUpdated(expectedMileage: Int) {
        assertThat(R.id.view_mileage, hasText("$expectedMileage"))
    }

    fun validateStartingVoltage(expectedStartingVoltage: Float) {
        assertThat(R.id.edit_voltage_start, hasText("$expectedStartingVoltage"))
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
}
