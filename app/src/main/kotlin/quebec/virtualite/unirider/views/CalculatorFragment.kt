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
import quebec.virtualite.unirider.utils.WidgetUtils.addTextChangedListener

class CalculatorFragment : Fragment() {

    var calculatorService = CalculatorService()

    lateinit var wheel: TripFragment.Wheel
    lateinit var wheelBattery: TextView
    lateinit var wheelName: TextView
    lateinit var wheelVoltage: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        wheel = arguments?.getParcelable("wheel")!!
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelName = view.findViewById(R.id.wheel_name)
        wheelName.text = wheel.name

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        wheelVoltage.addTextChangedListener(addTextChangedListener(onUpdateVoltage()))

        wheelBattery = view.findViewById(R.id.wheel_battery)
    }

    fun onUpdateVoltage() = { text: String ->
        wheelBattery.text = calculatorService.batteryOn(text, wheel.highest, wheel.lowest)
    }
}
