package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.apache.http.util.TextUtils.isEmpty
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.services.CalculatorService
import java.lang.Float.parseFloat
import java.util.Locale.ENGLISH

open class WheelViewFragment : BaseFragment() {

    companion object {
        const val PARAMETER_WHEEL_NAME = "wheelName"
    }

    internal lateinit var buttonEdit: Button
    internal lateinit var fieldBattery: TextView
    internal lateinit var fieldMileage: TextView
    internal lateinit var fieldName: TextView
    internal lateinit var fieldVoltage: EditText

    internal lateinit var parmWheelName: String

    internal lateinit var wheel: WheelEntity

    private var calculatorService = CalculatorService()
    private var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelName = arguments?.getString(PARAMETER_WHEEL_NAME)!!
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldName = view.findViewById(R.id.wheel_name_view)
        fieldName.text = parmWheelName

        fieldMileage = view.findViewById(R.id.wheel_mileage)

        fieldVoltage = view.findViewById(R.id.wheel_voltage)
        widgets.addTextChangedListener(fieldVoltage, onUpdateVoltage())

        fieldBattery = view.findViewById(R.id.wheel_battery)

        buttonEdit = view.findViewById(R.id.action_edit_wheel)
        widgets.setOnClickListener(buttonEdit, onEdit())

        connectDb {
            wheel = db.findWheel(parmWheelName)
                ?: throw WheelNotFoundException()

            fieldMileage.setText(wheel.mileage.toString())
        }
    }

//    fun onUpdateMileage() = { newMileage: String ->
//
//        val updatedWheel =
//            WheelEntity(wheel.id, wheel.name, intOf(newMileage), wheel.voltageMin, wheel.voltageMax)
//
//        runDb {
//            db.saveWheels(listOf(updatedWheel))
//        }
//    }

    fun onEdit() = { _: View ->
        navigateTo(
            R.id.action_WheelViewFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_NAME, wheel.name)
        )
    }

    fun onUpdateVoltage() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        fieldBattery.text = if (isEmpty(voltage)) "" else getPercentage(voltage)
    }

    private fun getPercentage(voltage: String): String {
        val percentage = calculatorService.percentage(wheel, parseFloat(voltage))

        when (percentage) {
            in 0f..100f -> {
                return "%.1f%%".format(ENGLISH, percentage)
            }
            else -> return ""
        }
    }
}
