package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var editKm: EditText
    internal lateinit var textName: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var textWhPerKm: TextView

    internal var parmWheelId: Long? = 0
    internal var parmWhPerKm: Float? = 0f
    internal var wheel: WheelEntity? = null

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        parmWhPerKm = arguments?.getFloat(PARAMETER_WH_PER_KM)
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editKm = view.findViewById(R.id.edit_km)
        textName = view.findViewById(R.id.view_name)
        textVoltageRequired = view.findViewById(R.id.view_required_voltage)
        textWhPerKm = view.findViewById(R.id.view_wh_per_km)

        widgets.addTextChangedListener(editKm, onUpdateKm())

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            textName.text = wheel!!.name
            textWhPerKm.text = "$parmWhPerKm ${fragments.string(R.string.label_wh_per_km)}"
        }
    }

    @SuppressLint("SetTextI18n")
    fun onUpdateKm() = { km: String ->
        val voltage = calculatorService.requiredVoltage(wheel, parmWhPerKm!!, floatOf(km))

        textVoltageRequired.text = "$voltage"
    }
}
