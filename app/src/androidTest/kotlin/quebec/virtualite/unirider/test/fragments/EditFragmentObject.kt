package quebec.virtualite.unirider.test.fragments

import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import cucumber.api.DataTable
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.longClick
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setChecked
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelDeleteConfirmationFragment
import quebec.virtualite.unirider.views.WheelEditFragment

class EditFragmentObject(val app: TestApp) {

    private val IS_NOT_SOLD = false
    private val IS_SOLD = true

    fun changeChargeRate(value: String) {
        setText(R.id.edit_charge_rate, value)
    }

    fun changeChargerOffset(value: String) {
        setText(R.id.edit_charger_offset, value)
    }

    fun changeDistanceOffset(value: String) {
        setText(R.id.edit_distance_offset, value)
    }

    fun changeFullVoltage(value: String) {
        setText(R.id.edit_voltage_full, value)
    }

    fun changeMileage(value: String) {
        setText(R.id.edit_mileage, value)
    }

    fun changeName(value: String) {
        setText(R.id.edit_name, value)
    }

    fun changePremileage(value: String) {
        setText(R.id.edit_premileage, value)
    }

    fun changeReserveVoltage(value: String) {
        setText(R.id.edit_voltage_reserve, value)
    }

    fun changeVoltageMax(value: String) {
        setText(R.id.edit_voltage_max, value)
    }

    fun changeVoltageMin(value: String) {
        setText(R.id.edit_voltage_min, value)
    }

    fun changeWh(value: String) {
        setText(R.id.edit_wh, value)
    }

    fun confirmDeletion() {
        assertThat(app.activeFragment(), equalTo(WheelDeleteConfirmationFragment::class.java))
        click(R.id.button_delete_confirmation)
    }

    fun deleteWheel() {
        longClick(R.id.button_delete)
    }

    fun enterNewWheel(newValues: DataTable, selectedWheel: WheelEntity): WheelEntity {

        val mapDetailToId = mapOf(
            Pair("Charge Rate", R.id.edit_charge_rate),
            Pair("Charger Offset", R.id.edit_charger_offset),
            Pair("Distance Offset", R.id.edit_distance_offset),
            Pair("Full Charge", R.id.edit_voltage_full),
            Pair("Mileage", R.id.edit_mileage),
            Pair("Name", R.id.edit_name),
            Pair("Previous Mileage", R.id.edit_premileage),
            Pair("Sold", R.id.check_sold),
            Pair("Voltage Min", R.id.edit_voltage_min),
            Pair("Voltage Max", R.id.edit_voltage_max),
            Pair("Voltage Reserve", R.id.edit_voltage_reserve),
            Pair("Wh", R.id.edit_wh),
        )

        val mapEntity = mutableMapOf<String, String>()
        newValues.cells(0).forEach { row ->
            val field = row[0]
            val value = row[1]
            mapEntity[field] = value

            val rawField = mapDetailToId[field]
            assertThat("Field '$field' is not defined", rawField, not(nullValue()))

            val key = rawField!!
            if ("Sold" == field)
                setChecked(key, "Yes" == value)
            else
                setText(key, value)
        }

        val updatedWheel = selectedWheel.copy(
            chargeRate = floatOf(mapEntity["Charge Rate"]!!),
            chargerOffset = floatOf(mapEntity["Charger Offset"]!!),
            distanceOffset = floatOf(mapEntity["Distance Offset"]!!),
            isSold = "yes".equals(mapEntity["Sold"]!!),
            mileage = intOf(mapEntity["Mileage"]!!),
            name = mapEntity["Name"]!!,
            premileage = intOf(mapEntity["Previous Mileage"]!!),
            voltageFull = floatOf(mapEntity["Full Charge"]!!),
            voltageMax = floatOf(mapEntity["Voltage Max"]!!),
            voltageMin = floatOf(mapEntity["Voltage Min"]!!),
            voltageReserve = floatOf(mapEntity["Voltage Reserve"]!!),
            wh = intOf(mapEntity["Wh"]!!)
        )

        click(R.id.button_save)

        return updatedWheel
    }

    fun getVoltageFull(): Float {
        return floatOf(getText(R.id.edit_voltage_full))
    }

    fun getVoltageMax(): Float {
        return floatOf(getText(R.id.edit_voltage_max))
    }

    fun getVoltageMin(): Float {
        return floatOf(getText(R.id.edit_voltage_min))
    }

    fun getVoltageReserve(): Float {
        return floatOf(getText(R.id.edit_voltage_reserve))
    }

    fun markAsSold() {
        setChecked(R.id.check_sold, IS_SOLD)
    }

    fun markAsUnsold() {
        setChecked(R.id.check_sold, IS_NOT_SOLD)
    }

    fun save() {
        click(R.id.button_save)
    }

    fun validateCanSave() {
        assertThat("Save button should be enabled", R.id.button_save, isEnabled())
    }

    fun validateCannotSave() {
        assertThat("Save button should be disabled", R.id.button_save, isDisabled())
    }

    fun validateView() {
        assertThat(app.activeFragment(), equalTo(WheelEditFragment::class.java))
    }
}
