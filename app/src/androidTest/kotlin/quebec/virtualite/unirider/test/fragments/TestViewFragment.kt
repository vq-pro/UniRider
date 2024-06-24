package quebec.virtualite.unirider.test.fragments

import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelViewFragment

class TestViewFragment(val app: TestApp) {

    fun charge() {
        click(R.id.button_charge)
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

    fun validateViewing(wheel: WheelEntity) {
        assertThat(app.activeFragment(), equalTo(WheelViewFragment::class.java))
        assertThat(R.id.view_name, hasText(wheel.name))
    }
}
