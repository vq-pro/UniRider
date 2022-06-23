package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException

open class WheelEditFragment : BaseFragment() {

    private val NEW_WHEEL = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f)

    internal lateinit var buttonDelete: Button
    internal lateinit var buttonSave: Button
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

    private var saveComparator = SaveComparator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)!!
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.edit_name)
        editPreMileage = view.findViewById(R.id.edit_premileage)
        editMileage = view.findViewById(R.id.edit_mileage)
        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)
        editVoltageReserve = view.findViewById(R.id.edit_voltage_reserve)
        editWh = view.findViewById(R.id.edit_wh)
        buttonDelete = view.findViewById(R.id.button_delete)
        buttonSave = view.findViewById(R.id.button_save)

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
        if (saveComparator.canSave(updatedWheel, initialWheel)) {
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

    fun onUpdateMileage() = { newMileage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            intOf(newMileage),
            updatedWheel.wh,
            updatedWheel.voltageMax,
            updatedWheel.voltageMin,
            updatedWheel.voltageReserve,
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }

    fun onUpdateName() = { newName: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            newName.trim(),
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            updatedWheel.mileage,
            updatedWheel.wh,
            updatedWheel.voltageMax,
            updatedWheel.voltageMin,
            updatedWheel.voltageReserve,
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }

    fun onUpdatePreMileage() = { newPreMileage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            intOf(newPreMileage),
            updatedWheel.mileage,
            updatedWheel.wh,
            updatedWheel.voltageMax,
            updatedWheel.voltageMin,
            updatedWheel.voltageReserve,
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }

    // FIXME-1 Use copy instead of building a new object
    fun onUpdateVoltageMax() = { newVoltage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            updatedWheel.mileage,
            updatedWheel.wh,
            floatOf(newVoltage),
            updatedWheel.voltageMin,
            updatedWheel.voltageReserve,
            floatOf(newVoltage)
        )

        enableSaveIfChanged()
    }

    fun onUpdateVoltageMin() = { newVoltage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            updatedWheel.mileage,
            updatedWheel.wh,
            updatedWheel.voltageMax,
            floatOf(newVoltage),
            updatedWheel.voltageReserve,
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }

    fun onUpdateVoltageReserve() = { newVoltage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            updatedWheel.mileage,
            updatedWheel.wh,
            updatedWheel.voltageMax,
            updatedWheel.voltageMin,
            floatOf(newVoltage),
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }

    fun onUpdateWh() = { newWh: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.premileage,
            updatedWheel.mileage,
            intOf(newWh),
            updatedWheel.voltageMax,
            updatedWheel.voltageMin,
            updatedWheel.voltageReserve,
            updatedWheel.voltageStart
        )

        enableSaveIfChanged()
    }
}
