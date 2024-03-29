package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException

open class WheelEditFragment : BaseFragment() {

    private val NEW_WHEEL = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false)

    internal lateinit var buttonDelete: Button
    internal lateinit var buttonSave: Button
    internal lateinit var checkSold: CheckBox
    internal lateinit var editChargeRate: EditText
    internal lateinit var editMileage: EditText
    internal lateinit var editName: EditText
    internal lateinit var editPreMileage: EditText
    internal lateinit var editVoltageMax: EditText
    internal lateinit var editVoltageMin: EditText
    internal lateinit var editVoltageReserve: EditText
    internal lateinit var editWh: EditText

    internal lateinit var initialWheel: WheelEntity
    internal lateinit var updatedWheel: WheelEntity

    internal var parmWheelId: Long? = 0

    private var wheelValidator = WheelValidator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)!!
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSold = view.findViewById(R.id.check_sold)
        editChargeRate = view.findViewById(R.id.edit_charge_rate)
        editName = view.findViewById(R.id.edit_name)
        editPreMileage = view.findViewById(R.id.edit_premileage)
        editMileage = view.findViewById(R.id.edit_mileage)
        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)
        editVoltageReserve = view.findViewById(R.id.edit_voltage_reserve)
        editWh = view.findViewById(R.id.edit_wh)
        buttonDelete = view.findViewById(R.id.button_delete)
        buttonSave = view.findViewById(R.id.button_save)

        widgets.setOnCheckedChangeListener(checkSold, onUpdateSold())
        widgets.addTextChangedListener(editChargeRate, onUpdateChargeRate())
        widgets.addTextChangedListener(editName, onUpdateName())
        widgets.addTextChangedListener(editPreMileage, onUpdatePreMileage())
        widgets.addTextChangedListener(editMileage, onUpdateMileage())
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

                checkSold.setChecked(initialWheel.isSold)
                editChargeRate.setText("${initialWheel.chargeRate}")
                editName.setText(initialWheel.name)
                editVoltageMax.setText("${initialWheel.voltageMax}")
                editVoltageMin.setText("${initialWheel.voltageMin}")
                editVoltageReserve.setText("${initialWheel.voltageReserve}")
                editWh.setText("${initialWheel.wh}")

                if (initialWheel.premileage != 0)
                    editPreMileage.setText("${initialWheel.premileage}")
                if (initialWheel.mileage != 0)
                    editMileage.setText("${initialWheel.mileage}")

            } else {
                initialWheel = NEW_WHEEL
                updatedWheel = initialWheel
            }

            widgets.disable(buttonSave)
        }
    }

    fun enableSaveIfChanged() {
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

    fun onDelete(): (View) -> Unit = {
        goto(R.id.action_WheelEditFragment_to_WheelDeleteConfirmationFragment, updatedWheel)
    }

    fun onSave(): (View) -> Unit = {
        external.runDB { it.saveWheel(updatedWheel) }
        fragments.navigateBack()
    }

    fun onUpdateChargeRate() = { newChargeRate: String ->
        updatedWheel = updatedWheel.copy(chargeRate = floatOf(newChargeRate))
        enableSaveIfChanged()
    }

    fun onUpdateMileage() = { newMileage: String ->
        updatedWheel = updatedWheel.copy(mileage = intOf(newMileage))
        enableSaveIfChanged()
    }

    fun onUpdateName() = { newName: String ->
        updatedWheel = updatedWheel.copy(name = newName.trim())
        enableSaveIfChanged()
    }

    fun onUpdatePreMileage() = { newPreMileage: String ->
        updatedWheel = updatedWheel.copy(premileage = intOf(newPreMileage))
        enableSaveIfChanged()
    }

    fun onUpdateSold() = { newSold: Boolean ->
        updatedWheel = updatedWheel.copy(isSold = newSold)
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMax() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(voltageMax = floatOf(newVoltage), voltageStart = floatOf(newVoltage))
        enableSaveIfChanged()
    }

    fun onUpdateVoltageMin() = { newVoltage: String ->
        updatedWheel = updatedWheel.copy(voltageMin = floatOf(newVoltage))
        enableSaveIfChanged()
    }

    fun onUpdateVoltageReserve() = { newVoltage: String ->
        var newVoltageReserve = floatOf(newVoltage)
        if (newVoltageReserve == 0f)
            newVoltageReserve = floatOf(widgets.text(editVoltageMin))

        updatedWheel = updatedWheel.copy(voltageReserve = newVoltageReserve)
        enableSaveIfChanged()
    }

    fun onUpdateWh() = { newWh: String ->
        updatedWheel = updatedWheel.copy(wh = intOf(newWh))
        enableSaveIfChanged()
    }
}
