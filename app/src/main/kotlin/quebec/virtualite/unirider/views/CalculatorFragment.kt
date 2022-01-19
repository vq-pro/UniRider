package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils

class CalculatorFragment : Fragment() {

    private var calculatorService = CalculatorService()
    private var widgets = WidgetUtils()

    internal lateinit var wheel: String
    internal lateinit var wheelBattery: TextView
    internal lateinit var wheelName: TextView
    internal lateinit var wheelVoltage: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        wheel = arguments?.getString("wheel")!!
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelBattery = view.findViewById(R.id.wheel_battery)

        wheelName = view.findViewById(R.id.wheel_name)
        wheelName.text = wheel

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        widgets.addTextChangedListener(wheelVoltage, onUpdateVoltage())
    }

    fun onUpdateVoltage() = { voltageString: String ->
        wheelBattery.text = calculatorService.batteryOn(wheel, voltageString)
    }
}
