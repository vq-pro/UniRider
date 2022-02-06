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
        const val PARAMETER_WHEEL_ID = "wheelID"
        const val PARAMETER_WHEEL_NAME = "wheelName"
    }

    internal lateinit var buttonEdit: Button
    internal lateinit var textBattery: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var editVoltage: EditText

    internal lateinit var wheel: WheelEntity

    internal var parmWheelId: Long? = 0

    private var calculatorService = CalculatorService()
    private var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        textMileage = view.findViewById(R.id.view_mileage)
        editVoltage = view.findViewById(R.id.edit_voltage)
        textBattery = view.findViewById(R.id.view_battery)
        buttonEdit = view.findViewById(R.id.button_edit)

        widgets.addTextChangedListener(editVoltage, onUpdateVoltage())
        widgets.setOnClickListener(buttonEdit, onEdit())

        connectDb {
            wheel = db.getWheel(parmWheelId!!)
                ?: throw WheelNotFoundException()

            textName.setText(wheel.name)
            textMileage.setText(wheel.mileage.toString())
        }
    }

    fun onEdit() = { _: View ->
        navigateTo(
            R.id.action_WheelViewFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, wheel.id)
        )
    }

    fun onUpdateVoltage() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        textBattery.text = if (isEmpty(voltage)) "" else getPercentage(voltage)
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
