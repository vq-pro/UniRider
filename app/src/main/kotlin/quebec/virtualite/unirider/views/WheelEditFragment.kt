package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_NAME

open class WheelEditFragment : BaseFragment() {

    internal lateinit var fieldMileage: EditText
    internal lateinit var fieldName: EditText
    internal lateinit var fieldVoltageMax: EditText
    internal lateinit var fieldVoltageMin: EditText

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

        fieldName = view.findViewById(R.id.wheel_name_edit)
        fieldName.setText(parmWheelName)
        widgets.addTextChangedListener(fieldName, onUpdateName())

        fieldMileage = view.findViewById(R.id.wheel_mileage)

        fieldVoltageMax = view.findViewById(R.id.wheel_voltage_max)
        fieldVoltageMin = view.findViewById(R.id.wheel_voltage_min)

        connectDb {
            initialWheel = db.findWheel(parmWheelName)
                ?: throw WheelNotFoundException()
            updatedWheel = initialWheel

            fieldMileage.setText("${initialWheel.mileage}")
            fieldVoltageMax.setText("${initialWheel.voltageMax}")
            fieldVoltageMin.setText("${initialWheel.voltageMin}")
        }
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
