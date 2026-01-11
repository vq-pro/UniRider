package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.switchmaterial.SwitchMaterial
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.commons.android.utils.NumberUtils.safeFloatOf
import quebec.virtualite.commons.android.utils.NumberUtils.safeIntOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity

open class WheelEditFragment : BaseFragment() {

    private val NEW_WHEEL = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f, false)

    internal lateinit var buttonDelete: Button
    internal lateinit var buttonSave: Button
    internal lateinit var editChargeAmperage: EditText
    internal lateinit var editChargeRate: EditText
    internal lateinit var editDistanceOffset: EditText
    internal lateinit var editMileage: EditText
    internal lateinit var editName: EditText
    internal lateinit var editPreMileage: EditText
    internal lateinit var editVoltageFull: EditText
    internal lateinit var editVoltageMax: EditText
    internal lateinit var editVoltageMin: EditText
    internal lateinit var editWh: EditText
    internal lateinit var switchSold: SwitchMaterial

    internal lateinit var initialWheel: WheelEntity
    internal lateinit var updatedWheel: WheelEntity

    internal var wheelValidator = WheelValidator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonDelete = view.findViewById(R.id.button_delete)
        buttonSave = view.findViewById(R.id.button_save)
        editChargeAmperage = view.findViewById(R.id.edit_charge_amperage)
        editChargeRate = view.findViewById(R.id.edit_charge_rate)
        editDistanceOffset = view.findViewById(R.id.edit_distance_offset)
        editName = view.findViewById(R.id.edit_name)
        editPreMileage = view.findViewById(R.id.edit_premileage)
        editMileage = view.findViewById(R.id.edit_mileage)
        editVoltageFull = view.findViewById(R.id.edit_voltage_full)
        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)
        editWh = view.findViewById(R.id.edit_wh)
        switchSold = view.findViewById(R.id.check_sold)

        widgets.setOnLongClickListener(buttonDelete, onDelete())
        widgets.setOnClickListener(buttonSave, onSave())
        widgets.addTextChangedListener(editChargeAmperage, onUpdateChargeAmperage())
        widgets.addTextChangedListener(editChargeRate, onUpdateChargeRate())
        widgets.addTextChangedListener(editDistanceOffset, onUpdateDistanceOffset())
        widgets.addTextChangedListener(editName, onUpdateName())
        widgets.addTextChangedListener(editPreMileage, onUpdatePreMileage())
        widgets.addTextChangedListener(editMileage, onUpdateMileage())
        widgets.addTextChangedListener(editVoltageFull, onUpdateVoltageFull())
        widgets.addTextChangedListener(editVoltageMax, onUpdateVoltageMax())
        widgets.addTextChangedListener(editVoltageMin, onUpdateVoltageMin())
        widgets.addTextChangedListener(editWh, onUpdateWh())
        widgets.setOnCheckedChangeListener(switchSold, onToggleSold())

        if (wheel != null) {
            initialWheel = wheel!!
            updatedWheel = initialWheel

            fragments.runUI {
                editChargeAmperage.setText("${initialWheel.chargeAmperage}")
                editChargeRate.setText("${initialWheel.chargeRate}")
                editDistanceOffset.setText("${initialWheel.distanceOffset}")
                editName.setText(initialWheel.name)
                editVoltageFull.setText("${initialWheel.voltageFull}")
                editVoltageMax.setText("${initialWheel.voltageMax}")
                editVoltageMin.setText("${initialWheel.voltageMin}")
                editWh.setText("${initialWheel.wh}")
                switchSold.setChecked(initialWheel.isSold)

                if (initialWheel.premileage != 0)
                    editPreMileage.setText("${initialWheel.premileage}")

                if (initialWheel.mileage != 0)
                    editMileage.setText("${initialWheel.mileage}")
            }
        } else {
            initialWheel = NEW_WHEEL
            updatedWheel = initialWheel
        }

        fragments.runUI {
            widgets.disable(buttonSave)
        }
    }

    fun onDelete(): (View) -> Unit = {
        fragments.navigateTo(R.id.action_WheelEditFragment_to_WheelConfirmationDeleteFragment)
    }

    fun onSave(): (View) -> Unit = {
        external.runDB { db ->
            wheel = updatedWheel
            db.saveWheel(wheel)
        }
        fragments.navigateBack()
    }

    fun onToggleSold() = { newSold: Boolean ->
        updatedWheel = updatedWheel.copy(isSold = newSold)
        enableSaveIfChanged()
    }

    fun onUpdateChargeAmperage() = { newChargeAmperage: String ->
        updatedWheel = updatedWheel.copy(
            chargeAmperage = round(safeFloatOf(newChargeAmperage))
        )
        enableSaveIfChanged()
    }

    fun onUpdateChargeRate() = { newChargeRate: String ->
        updatedWheel = updatedWheel.copy(
            chargeRate = round(safeFloatOf(newChargeRate))
        )
        enableSaveIfChanged()
    }

    fun onUpdateDistanceOffset() = { newDistanceOffset: String ->
        updatedWheel = updatedWheel.copy(
            distanceOffset = round(safeFloatOf(newDistanceOffset), 4)
        )
        enableSaveIfChanged()
    }

    fun onUpdateMileage() = { newMileage: String ->
        updatedWheel = updatedWheel.copy(mileage = safeIntOf(newMileage))
        enableSaveIfChanged()
    }

    fun onUpdateName() = { newName: String ->
        updatedWheel = updatedWheel.copy(name = newName.trim())
        enableSaveIfChanged()
    }

    fun onUpdatePreMileage() = { newPreMileage: String ->
        updatedWheel = updatedWheel.copy(premileage = safeIntOf(newPreMileage))
        enableSaveIfChanged()
    }

    fun onUpdateVoltageFull() = { newVoltage: String ->
        var newVoltageFull = round(safeFloatOf(newVoltage))
        if (newVoltageFull == 0f) newVoltageFull = floatOf(widgets.getText(editVoltageMax))

        updatedWheel = updatedWheel.copy(voltageFull = newVoltageFull)
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMax() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(
            voltageMax = round(safeFloatOf(newVoltage))
        )
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMin() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(
            voltageMin = round(safeFloatOf(newVoltage))
        )
        enableSaveIfChanged()
    }

    fun onUpdateWh() = { newWh: String ->
        updatedWheel = updatedWheel.copy(wh = safeIntOf(newWh))
        enableSaveIfChanged()
    }

    internal open fun enableSaveIfChanged() {
        if (wheelValidator.canSave(updatedWheel, initialWheel)) {
            external.runDB { db ->
                if (db.findDuplicate(updatedWheel)) widgets.disable(buttonSave)
                else widgets.enable(buttonSave)
            }
        } else widgets.disable(buttonSave)
    }
}
