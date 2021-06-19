package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.Wheel
import quebec.virtualite.unirider.utils.WidgetUtils

class TripFragment : Fragment() {

    private val SELECT_WHEEL_TITLE = "<Select Model>"
    private val SELECT_WHEEL = Wheel(SELECT_WHEEL_TITLE, 0f, 0f)

    var calculatorService = CalculatorService()

    lateinit var buttonCalc: Button
    lateinit var spinnerWheel: Spinner

    var wheel: Wheel = SELECT_WHEEL
    var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.trip_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCalc = view.findViewById(R.id.button_calculator)
        buttonCalc.setOnClickListener(widgets.onClickListener(onGoCalculator()))
        buttonCalc.isEnabled = false

        val wheelList = listOf(SELECT_WHEEL_TITLE) + calculatorService.wheels().map { wheel -> wheel.name }

        spinnerWheel = view.findViewById(R.id.wheel_selector)
        spinnerWheel.adapter = widgets.arrayAdapter(view.context, R.layout.wheel_item, wheelList)
        spinnerWheel.isEnabled = true
        spinnerWheel.onItemSelectedListener = widgets.onItemSelectedListener(onSelectWheel())
    }

    fun onGoCalculator() = { _: View ->
        findNavController()
            .navigate(
                R.id.action_TripFragment_to_CalculatorFragment,
                bundleOf("wheel" to wheel)
            )
    }

    fun onSelectWheel() = { index: Int ->
        buttonCalc.isEnabled = (index != 0)
        wheel = if (index == 0)
            SELECT_WHEEL
        else
            calculatorService.wheels().get(index - 1)
    }
}
