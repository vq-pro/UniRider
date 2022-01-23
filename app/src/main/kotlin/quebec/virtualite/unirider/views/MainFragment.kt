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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils
import quebec.virtualite.unirider.views.MainActivity.Companion.db

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

        val wheelList = ArrayList<String>()
        val wheels = view.findViewById(R.id.wheels) as ListView
        wheels.adapter = widgets.listAdapter(view, R.layout.wheels_item, wheelList)
        wheels.isVisible = true

        lifecycleScope.launch(Dispatchers.IO) {
            wheelList.clear()
            wheelList.addAll(db.getWheelList())
        }
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
}
