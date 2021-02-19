package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils.addTextChangedListener
import quebec.virtualite.unirider.utils.WidgetUtils.onItemSelectedListener

class CalculatorFragment : Fragment() {

    data class Wheel(val name: String, val highest: Float, val lowest: Float)

    private val calculatorService = CalculatorService()
    private val wheels = listOf(
        Wheel("<Select Model>", 0f, 0f),
        Wheel("Gotway Nikola", 100.8f, 79.2f),
        Wheel("Inmotion V10F", 84f, 68f),
        Wheel("KingSong 14D", 67.2f, 48.0f),
        Wheel("Veteran Sherman", 100.8f, 75.6f)
    )

    private lateinit var wheelSelector: Spinner

    private lateinit var wheelBattery: TextView
    private lateinit var wheelVoltage: EditText

    private var wheel: Wheel = Wheel("", 0f, 0f)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelSelector = view.findViewById(R.id.wheel_selector)
        wheelSelector.adapter = ArrayAdapter(view.context, R.layout.wheel_item, wheels.map { wheel -> wheel.name })
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

        wheel = wheels.get(index)
        wheelVoltage.text.clear()
        wheelBattery.text = ""
    }

    private fun onUpdateVoltage() = { text: String ->
        wheelBattery.text = calculatorService.batteryOn(text, wheel.highest, wheel.lowest)
    }
}
