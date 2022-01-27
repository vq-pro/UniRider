package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils

open class CalculatorFragment : Fragment() {

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
    private lateinit var db: WheelDb

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelName = arguments?.getString(PARAMETER_WHEEL_NAME)!!
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = connectDb()

        subThread {
            wheel = db.findWheel(parmWheelName)
                ?: throw RuntimeException()
        }

        wheelBattery = view.findViewById(R.id.wheel_battery)

        wheelName = view.findViewById(R.id.wheel_name)
        wheelName.text = parmWheelName

        wheelVoltage = view.findViewById(R.id.wheel_voltage)
        widgets.addTextChangedListener(wheelVoltage, onUpdateVoltage())
    }

    fun onUpdateVoltage() = { voltageString: String ->
        wheelBattery.text = calculatorService.batteryOn(wheel, voltageString)
    }

    // FIXME 2 Généraliser
    internal open fun connectDb(): WheelDb {
        return MainActivity.db
    }

    internal open fun subThread(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }
}
