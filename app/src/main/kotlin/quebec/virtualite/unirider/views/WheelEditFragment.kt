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
import quebec.virtualite.commons.android.utils.NumberUtils.safeFloatOf
import quebec.virtualite.commons.android.utils.NumberUtils.safeIntOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException

open class WheelEditFragment : BaseFragment() {

    private val NEW_WHEEL = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f, 0f, false)

    internal lateinit var buttonDelete: Button
    internal lateinit var buttonSave: Button
    internal lateinit var editChargeRate: EditText
    internal lateinit var editChargerOffset: EditText
    internal lateinit var editMileage: EditText
    internal lateinit var editName: EditText
    internal lateinit var editPreMileage: EditText
    internal lateinit var editVoltageFull: EditText
    internal lateinit var editVoltageMax: EditText
    internal lateinit var editVoltageMin: EditText
    internal lateinit var editVoltageReserve: EditText
    internal lateinit var editWh: EditText
    internal lateinit var swtichSold: SwitchMaterial

    internal lateinit var initialWheel: WheelEntity
    internal lateinit var updatedWheel: WheelEntity

    internal var parmWheelId: Long? = 0

    private var wheelValidator = WheelValidator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)!!
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swtichSold = view.findViewById(R.id.check_sold)
        editChargeRate = view.findViewById(R.id.edit_charge_rate)
        editChargerOffset = view.findViewById(R.id.edit_charger_offset)
        editName = view.findViewById(R.id.edit_name)
        editPreMileage = view.findViewById(R.id.edit_premileage)
        editMileage = view.findViewById(R.id.edit_mileage)
        editVoltageFull = view.findViewById(R.id.edit_voltage_full)
        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)
        editVoltageReserve = view.findViewById(R.id.edit_voltage_reserve)
        editWh = view.findViewById(R.id.edit_wh)
        buttonDelete = view.findViewById(R.id.button_delete)
        buttonSave = view.findViewById(R.id.button_save)

        widgets.setOnCheckedChangeListener(swtichSold, onToggleSold())
        widgets.addTextChangedListener(editChargeRate, onUpdateChargeRate())
        widgets.addTextChangedListener(editChargerOffset, onUpdateChargerOffset())
        widgets.addTextChangedListener(editName, onUpdateName())
        widgets.addTextChangedListener(editPreMileage, onUpdatePreMileage())
        widgets.addTextChangedListener(editMileage, onUpdateMileage())
        widgets.addTextChangedListener(editVoltageFull, onUpdateVoltageFull())
        widgets.addTextChangedListener(editVoltageMax, onUpdateVoltageMax())
        widgets.addTextChangedListener(editVoltageMin, onUpdateVoltageMin())
        widgets.addTextChangedListener(editVoltageReserve, onUpdateVoltageReserve())
        widgets.addTextChangedListener(editWh, onUpdateWh())
        widgets.setOnLongClickListener(buttonDelete, onDelete())
        widgets.setOnClickListener(buttonSave, onSave())

        external.runDB {
            if (parmWheelId != 0L) {
                initialWheel = it.getWheel(parmWheelId!!) ?: throw WheelNotFoundException()
                updatedWheel = initialWheel

                fragments.runUI {
                    swtichSold.setChecked(initialWheel.isSold)
                    editChargeRate.setText("${initialWheel.chargeRate}")
                    editChargerOffset.setText("${initialWheel.chargerOffset}")
                    editName.setText(initialWheel.name)
                    editVoltageFull.setText("${initialWheel.voltageFull}")
                    editVoltageMax.setText("${initialWheel.voltageMax}")
                    editVoltageMin.setText("${initialWheel.voltageMin}")
                    editVoltageReserve.setText("${initialWheel.voltageReserve}")
                    editWh.setText("${initialWheel.wh}")

                    if (initialWheel.premileage != 0) editPreMileage.setText("${initialWheel.premileage}")
                    if (initialWheel.mileage != 0) editMileage.setText("${initialWheel.mileage}")
                }

            } else {
                initialWheel = NEW_WHEEL
                updatedWheel = initialWheel
            }

            fragments.runUI {
                widgets.disable(buttonSave)
            }
        }
    }

    fun onDelete(): (View) -> Unit = {
        goto(R.id.action_WheelEditFragment_to_WheelDeleteConfirmationFragment, updatedWheel)
    }

    fun onSave(): (View) -> Unit = {
        external.runDB { it.saveWheel(updatedWheel) }
        fragments.navigateBack()
    }

    fun onToggleSold() = { newSold: Boolean ->
        updatedWheel = updatedWheel.copy(isSold = newSold)
        enableSaveIfChanged()
    }

    fun onUpdateChargeRate() = { newChargeRate: String ->
        updatedWheel = updatedWheel.copy(chargeRate = safeFloatOf(newChargeRate))
        enableSaveIfChanged()
    }

    fun onUpdateChargerOffset() = { newChargerOffset: String ->
        updatedWheel = updatedWheel.copy(chargerOffset = safeFloatOf(newChargerOffset))
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
        var newVoltageFull = safeFloatOf(newVoltage)
        if (newVoltageFull == 0f)
            newVoltageFull = floatOf(widgets.getText(editVoltageMax))

        updatedWheel = updatedWheel.copy(voltageFull = newVoltageFull)
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMax() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(voltageMax = safeFloatOf(newVoltage), voltageStart = safeFloatOf(newVoltage))
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMin() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(voltageMin = safeFloatOf(newVoltage))
        enableSaveIfChanged()
    }

    fun onUpdateVoltageReserve() = { newVoltage: String ->
        var newVoltageReserve = safeFloatOf(newVoltage)
        if (newVoltageReserve == 0f)
            newVoltageReserve = floatOf(widgets.getText(editVoltageMin))

        updatedWheel = updatedWheel.copy(voltageReserve = newVoltageReserve)
        enableSaveIfChanged()
    }

    fun onUpdateWh() = { newWh: String ->
        updatedWheel = updatedWheel.copy(wh = safeIntOf(newWh))
        enableSaveIfChanged()
    }

    internal open fun enableSaveIfChanged() {
        if (wheelValidator.canSave(updatedWheel, initialWheel)) {
            external.runDB {
                if (!it.findDuplicate(updatedWheel))
                    widgets.enable(buttonSave)
                else
                    widgets.disable(buttonSave)
            }
        } else
            widgets.disable(buttonSave)
    }
}
