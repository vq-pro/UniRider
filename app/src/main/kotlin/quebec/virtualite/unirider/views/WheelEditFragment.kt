package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_NAME

open class WheelEditFragment : BaseFragment() {

    internal lateinit var buttonSave: Button
    internal lateinit var editMileage: EditText
    internal lateinit var editName: EditText
    internal lateinit var editVoltageMax: EditText
    internal lateinit var editVoltageMin: EditText

    internal lateinit var parmWheelName: String

    internal lateinit var initialWheel: WheelEntity
    internal lateinit var updatedWheel: WheelEntity

    private var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelName = arguments?.getString(PARAMETER_WHEEL_NAME)!!
        return inflater.inflate(R.layout.wheel_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.edit_name)
        editName.setText(parmWheelName)
        widgets.addTextChangedListener(editName, onUpdateName())

        editMileage = view.findViewById(R.id.edit_mileage)
        widgets.addTextChangedListener(editMileage, onUpdateMileage())

        editVoltageMax = view.findViewById(R.id.edit_voltage_max)
        editVoltageMin = view.findViewById(R.id.edit_voltage_min)

        buttonSave = view.findViewById(R.id.button_save)
        widgets.setOnClickListener(buttonSave, onSave())

        connectDb {
            initialWheel = db.findWheel(parmWheelName)
                ?: throw WheelNotFoundException()
            updatedWheel = initialWheel

            editMileage.setText("${initialWheel.mileage}")
            editVoltageMax.setText("${initialWheel.voltageMax}")
            editVoltageMin.setText("${initialWheel.voltageMin}")
        }
    }

    fun onSave() = { _: View ->
        runDb { db.saveWheel(updatedWheel) }
        navigateBack()
    }

    fun onUpdateMileage() = { newMileage: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            updatedWheel.name,
            intOf(newMileage),
            updatedWheel.voltageMin,
            updatedWheel.voltageMax
        )
    }

    fun onUpdateName() = { newName: String ->
        updatedWheel = WheelEntity(
            updatedWheel.id,
            newName.trim(),
            updatedWheel.mileage,
            updatedWheel.voltageMin,
            updatedWheel.voltageMax
        )
    }
}
