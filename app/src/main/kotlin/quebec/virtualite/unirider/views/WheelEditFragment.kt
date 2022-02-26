package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

open class WheelEditFragment : BaseFragment() {

    private val NEW_WHEEL = WheelEntity(0L, "", null, null, 0, 0f, 0f)

    internal lateinit var buttonSave: Button
    internal lateinit var editMileage: EditText
    internal lateinit var editName: EditText
    internal lateinit var editVoltageMax: EditText
    internal lateinit var editVoltageMin: EditText

    internal lateinit var initialWheel: WheelEntity
    internal lateinit var updatedWheel: WheelEntity

    internal var parmWheelId: Long? = 0

    private var saveComparator = SaveComparator()
    private var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)!!
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.edit_name)
        editMileage = view.findViewById(R.id.edit_mileage)
        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)
        buttonSave = view.findViewById(R.id.button_save)

        widgets.addTextChangedListener(editName, onUpdateName())
        widgets.addTextChangedListener(editMileage, onUpdateMileage())
        widgets.addTextChangedListener(editVoltageMax, onUpdateVoltageMax())
        widgets.addTextChangedListener(editVoltageMin, onUpdateVoltageMin())
        widgets.setOnClickListener(buttonSave, onSave())

        initDB {
            initialWheel = if (parmWheelId == 0L)
                NEW_WHEEL
            else
                db.getWheel(parmWheelId!!) ?: throw WheelNotFoundException()

            updatedWheel = initialWheel

            editName.setText(initialWheel.name)
            editMileage.setText("${initialWheel.mileage}")
            editVoltageMax.setText("${initialWheel.voltageMax}")
            editVoltageMin.setText("${initialWheel.voltageMin}")

            widgets.disable(buttonSave)
        }
    }

    fun enableSaveIfChanged() {
        if (saveComparator.canSave(updatedWheel, initialWheel)) {
            runDB {
                if (!db.findDuplicate(updatedWheel))
                    widgets.enable(buttonSave)
                else
                    widgets.disable(buttonSave)
            }
        } else
            widgets.disable(buttonSave)
    }

    fun onSave() = { _: View ->
        runDB { db.saveWheel(updatedWheel) }
        navigateBack()
    }

    fun onUpdateMileage() = { newMileage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            intOf(newMileage),
            updatedWheel.voltageMin,
            updatedWheel.voltageMax
        )

        enableSaveIfChanged()
    }

    fun onUpdateName() = { newName: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            newName.trim(),
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.mileage,
            updatedWheel.voltageMin,
            updatedWheel.voltageMax
        )

        enableSaveIfChanged()
    }

    fun onUpdateVoltageMax() = { newVoltage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            updatedWheel.btName,
            updatedWheel.btAddr,
            updatedWheel.mileage,
            updatedWheel.voltageMin,
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
            updatedWheel.mileage,
            floatOf(newVoltage),
            updatedWheel.voltageMax
        )

        enableSaveIfChanged()
    }
}
