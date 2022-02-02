package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

open class WheelFragment : BaseFragment() {

    companion object {
        const val PARAMETER_WHEEL_NAME = "wheelName"
    }

    internal lateinit var parmWheelName: String
    internal lateinit var wheel: WheelEntity
    internal lateinit var wheelBattery: TextView
    internal lateinit var wheelDistance: EditText
    internal lateinit var wheelName: TextView
    internal lateinit var wheelVoltage: EditText

    private var calculatorService = CalculatorService()
    private var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelName = arguments?.getString(PARAMETER_WHEEL_NAME)!!
        return inflater.inflate(R.layout.wheel_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelName = view.findViewById(R.id.wheel_name)
        wheelName.text = parmWheelName

        wheelDistance = view.findViewById(R.id.wheel_distance)
        widgets.addTextChangedListener(wheelDistance, onUpdateDistance())

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        widgets.addTextChangedListener(wheelVoltage, onUpdateVoltage())

        wheelBattery = view.findViewById(R.id.wheel_battery)

        connectDb {
            wheel = db.findWheel(parmWheelName)
                ?: throw WheelNotFoundException()

            wheelDistance.setText(wheel.distance.toString())
        }
    }

    fun onUpdateDistance() = { newDistance: String ->
        runDb {
            val updatedWheel =
                WheelEntity(wheel.id, wheel.name, newDistance.trim().toInt(), wheel.voltageMax, wheel.voltageMin)

            db.saveWheels(listOf(updatedWheel))
        }
    }

    fun onUpdateVoltage() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        wheelBattery.text = if (isEmpty(voltage)) "" else getPercentage(voltage)
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
