package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils

class MainFragment : Fragment() {

    private val SELECT_WHEEL = "<Select Model>"

    var calculatorService = CalculatorService()

    lateinit var buttonCalc: Button
    lateinit var spinnerWheel: Spinner

    var wheel = SELECT_WHEEL
    var widgets = WidgetUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCalc = view.findViewById(R.id.button_calculator)
        buttonCalc.setOnClickListener(widgets.onClickListener(onGoCalculator()))
        buttonCalc.isEnabled = false

        val spinnerList = listOf(SELECT_WHEEL) + calculatorService.wheels()

        spinnerWheel = view.findViewById(R.id.wheel_selector)
        spinnerWheel.adapter = widgets.spinnerAdapter(view, R.layout.wheel_item, spinnerList)
        spinnerWheel.isEnabled = true
        spinnerWheel.onItemSelectedListener = widgets.onItemSelectedListener(onSelectWheel())

        val wheels = view.findViewById(R.id.wheels) as ListView
        wheels.adapter = widgets.listAdapter(view, R.layout.wheels_item, getWheelList())
        wheels.isVisible = true
    }

    fun onGoCalculator() = { _: View ->
        findNavController()
            .navigate(
                R.id.action_MainFragment_to_CalculatorFragment,
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

    private fun getWheelList(): List<String> {
//        val dao = db.wheelDao()
//        val list = dao.getAllWheels()
        val list = listOf("A", "B", "C")
        return list
    }
}
