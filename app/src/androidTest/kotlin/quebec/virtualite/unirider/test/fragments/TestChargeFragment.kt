package quebec.virtualite.unirider.test.fragments

import androidx.test.espresso.matcher.ViewMatchers.isChecked
import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectSpinnerItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelChargeFragment

class TestChargeFragment(val app: TestApp) {

    fun changeRateTo(newRate: Int) {
        selectSpinnerItem(R.id.spinner_rate, "$newRate")
    }

    fun changeVoltageTo(voltage: String) {
        setText(R.id.edit_voltage_actual, strip(voltage, "V"))
    }

    fun chargeFor(km: String) {
        assertThat("Full button is not enabled by default", R.id.check_full_charge, isChecked())

        if ("full" != km) {
            click(R.id.check_full_charge)
            setText(R.id.edit_km, strip(km, "km"))
        }
    }

    fun reconnect() {
        click(R.id.button_connect_charge)
    }

    fun validateActualVoltage(expectedVoltage: String) {
        assertThat(R.id.edit_voltage_actual, hasText(strip(expectedVoltage, "V")))
    }

    fun validateCannotConnect() {
        assertThat("Connect button is not disabled", R.id.button_connect_charge, isDisabled())
    }

    fun validateEstimates(expectedEstimates: DataTable) {
        expectedEstimates.diff(
            DataTable.create(
                listOf(
                    listOf("required voltage", "time"),
                    listOf(
                        getText(R.id.view_voltage_required),
                        getText(R.id.view_remaining_time)
                    )
                )
            )
        )
    }

    fun validateView() {
        assertThat(app.activeFragment(), equalTo(WheelChargeFragment::class.java))
    }
}
