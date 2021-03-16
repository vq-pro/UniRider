package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.utils.WidgetUtils.onClickListener
import quebec.virtualite.unirider.utils.WidgetUtils.onItemSelectedListener

class TripFragment : Fragment() {

    private val wheels = listOf(
        Wheel("<Select Model>", 0f, 0f),
        Wheel("Gotway Nikola+", 100.8f, 78.0f),
        Wheel("Inmotion V10F", 84f, 68f),
        Wheel("KingSong 14D", 67.2f, 48.0f),
        Wheel("Veteran Sherman", 100.8f, 75.6f)
    )

    lateinit var buttonCalc: Button
    lateinit var spinnerWheel: Spinner

    private var wheel: Wheel = Wheel("", 0f, 0f)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.trip_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCalc = view.findViewById(R.id.button_calculator)
        buttonCalc.setOnClickListener(onClickListener(onGoCalculator()))
        buttonCalc.isEnabled = false

        spinnerWheel = view.findViewById(R.id.wheel_selector)
        spinnerWheel.adapter = ArrayAdapter(view.context, R.layout.wheel_item, wheels.map { wheel -> wheel.name })
        spinnerWheel.isEnabled = true
        spinnerWheel.onItemSelectedListener = onItemSelectedListener(onSelectWheel())
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
        wheel = wheels.get(index)
    }
}
