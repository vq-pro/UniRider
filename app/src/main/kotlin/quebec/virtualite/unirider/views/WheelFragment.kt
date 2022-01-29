package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.apache.http.util.TextUtils.isEmpty
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils
import java.lang.Float.parseFloat
import java.util.Locale.ENGLISH

open class WheelFragment : BaseFragment() {

    companion object {
        const val PARAMETER_WHEEL_NAME = "wheelName"
    }

    internal lateinit var parmWheelName: String
    internal lateinit var wheel: WheelEntity
    internal lateinit var wheelBattery: TextView
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

        connectDb()

        subThread {
            wheel = db.findWheel(parmWheelName)
                ?: throw WheelNotFoundException()
        }

        wheelBattery = view.findViewById(R.id.wheel_battery)

        wheelName = view.findViewById(R.id.wheel_name)
        wheelName.text = parmWheelName

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        widgets.addTextChangedListener(wheelVoltage, onUpdateVoltage())
    }

    fun onUpdateVoltage() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        wheelBattery.text =
            if (isEmpty(voltage)) "" else getPercentage(voltage)
    }

    fun getPercentage(voltageString: String): String {
        val percentage = calculatorService.batteryOn(wheel, parseFloat(voltageString))

        when (percentage) {
            in 0f..100f -> {
                return "%.1f%%".format(ENGLISH, percentage)
            }
            else -> return ""
        }
    }
}
