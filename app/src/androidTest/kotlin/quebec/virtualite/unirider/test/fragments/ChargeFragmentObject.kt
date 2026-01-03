package quebec.virtualite.unirider.test.fragments

import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThatPolling
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelChargeFragment
import java.lang.Thread.sleep

class ChargeFragmentObject(val app: TestApp) {

    fun changeVoltageActualTo(voltage: String) {
        setText(R.id.edit_voltage_actual, parseVoltage(voltage))
    }

    fun changeVoltageRequired(voltage: String) {
        setText(R.id.edit_voltage_required, parseVoltage(voltage))
    }

    fun chargeFor(km: String) {
        assertThat("Full button is not enabled by default", R.id.check_full_charge, isChecked())

        if ("full" == km) {
            click(R.id.check_full_charge)   // Disable
            sleep(250)
            click(R.id.check_full_charge)   // Enable again

        } else {
            setText(R.id.edit_km, parseKm(km))
        }
    }

    fun reconnect() {
        click(R.id.button_connect_charge)
    }

    fun validateActualVoltage(expectedVoltage: String) {
        assertThat(R.id.edit_voltage_actual, hasText(parseVoltage(expectedVoltage)))
    }

    fun validateCannotConnect() {
        assertThat("Connect button is not disabled", R.id.button_connect_charge, isDisabled())
    }

    fun validateEmptyEstimates() {
        assertThat(getText(R.id.view_voltage_required), equalTo(""))
        assertThat(getText(R.id.view_voltage_required_diff), equalTo(""))
        assertThat(getText(R.id.view_voltage_target), equalTo(""))
        assertThat(getEstimatedTime(), equalTo(""))
    }

    fun validateEstimates(expectedEstimates: DataTable) {
        expectedEstimates.diff(
            DataTable.create(
                listOf(
                    listOf("required", "target", "time"),
                    listOf(
                        getRequired(),
                        formatV(getText(R.id.view_voltage_target)),
                        getEstimatedTime()
                    )
                )
            )
        )
    }

    fun validateFullChargeIndicatorOn() {
        assertThat("Full Charge should be on", R.id.check_full_charge, isChecked())
    }

    fun validateFullChargeIndicatorOff() {
        assertThat("Full Charge should be off", R.id.check_full_charge, isNotChecked())
    }

    fun validateView() {
        assertThatPolling({ app.activeFragment() }, equalTo(WheelChargeFragment::class.java))
    }

    private fun formatV(voltage: String): String = if (!voltage.isEmpty()) voltage + "V" else ""

    private fun getEstimatedTime(): String =
        (getText(R.id.view_estimated_time) + " " + getText(R.id.view_estimated_diff))
            .trim()

    private fun getRequired(): String =
        (formatV(getText(R.id.view_voltage_required)) + " " + getText(R.id.view_voltage_required_diff))
            .trim()

    private fun parseKm(km: String) = strip(km, " km")

    private fun parseVoltage(voltage: String) = if (voltage.contains('V')) voltage.substring(0, voltage.indexOf('V')) else voltage
}
