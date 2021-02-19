package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils.addTextChangedListener
import quebec.virtualite.unirider.utils.WidgetUtils.onItemSelectedListener

class CalculatorFragment : Fragment() {

    private val calculatorService = CalculatorService()
    private val wheelNames: MutableList<String> = mutableListOf()

    private lateinit var wheelSelector: Spinner

    private lateinit var wheelBattery: TextView
    private lateinit var wheelVoltage: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelNames.add("<Select Model>")
        wheelNames.add("Gotway Nikola")
        wheelNames.add("KingSong 14D")

        wheelSelector = view.findViewById(R.id.wheel_selector)
        wheelSelector.adapter = ArrayAdapter(view.context, R.layout.wheel_item, wheelNames)
        wheelSelector.isEnabled = true
        wheelSelector.onItemSelectedListener = onItemSelectedListener(onSelectWheel())

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        wheelVoltage.addTextChangedListener(addTextChangedListener(onUpdateVoltage()))

        wheelBattery = view.findViewById(R.id.wheel_battery)
    }

    private fun onSelectWheel() = { index: Int ->
        when (index) {
            0 -> {
                wheelVoltage.isVisible = false
                wheelBattery.isVisible = false
            }
            else -> {
                wheelVoltage.isVisible = true
                wheelBattery.isVisible = true
            }
        }
    }

    private fun onUpdateVoltage() = { text: String ->
        wheelBattery.text = calculatorService.batteryOn(text)
    }
}
