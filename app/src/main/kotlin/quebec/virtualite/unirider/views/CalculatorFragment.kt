package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R

class CalculatorFragment : Fragment() {

    private val wheelNames: MutableList<String> = mutableListOf()

    private lateinit var wheelAdapter: ArrayAdapter<String>
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

        wheelAdapter = ArrayAdapter(view.context, R.layout.wheel_item, wheelNames)
        wheelSelector = view.findViewById(R.id.wheel_selector)
        wheelSelector.adapter = wheelAdapter
        wheelSelector.isEnabled = true
        wheelSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        wheelVoltage.isVisible = false
                        wheelBattery.isVisible = false
                    }
                    else -> {
                        wheelVoltage.isVisible = true
                        wheelBattery.isVisible = true

                        wheelBattery.text = "79.6"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        wheelBattery = view.findViewById(R.id.wheel_battery)
        wheelVoltage = view.findViewById(R.id.wheel_voltage)
    }

    internal fun onSelectWheel(): (View) -> Unit {
        return {

        }
    }
}
