package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.apache.http.util.TextUtils.isEmpty
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.isPositive
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import java.util.Locale.ENGLISH
import kotlin.math.roundToInt

open class WheelViewFragment : BaseFragment() {

    private val NB_DECIMALS = 1
    private val READ_KM = null
    private val READ_VOLTAGE_ACTUAL = null
    private val READ_VOLTAGE_START = null

    internal lateinit var buttonConnect: Button
    internal lateinit var buttonEdit: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var editVoltageStart: EditText
    internal lateinit var textBattery: TextView
    internal lateinit var textBtName: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var textRemainingRange: TextView
    internal lateinit var textTotalRange: TextView
    internal lateinit var textWhPerKm: TextView

    internal var parmWheelId: Long? = 0
    internal var wheel: WheelEntity? = null

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect)
        buttonEdit = view.findViewById(R.id.button_edit)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        editVoltageStart = view.findViewById(R.id.edit_voltage_start)
        textBattery = view.findViewById(R.id.view_battery)
        textBtName = view.findViewById(R.id.view_bt_name)
        textMileage = view.findViewById(R.id.view_mileage)
        textName = view.findViewById(R.id.view_name)
        textRemainingRange = view.findViewById(R.id.view_remaining_range)
        textTotalRange = view.findViewById(R.id.view_total_range)
        textWhPerKm = view.findViewById(R.id.view_wh_per_km)

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            if (wheel != null) {
                widgets.addTextChangedListener(editVoltageStart, onUpdateVoltageStart())
                widgets.addTextChangedListener(editKm, onUpdateKm())
                widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
                widgets.setOnClickListener(buttonConnect, onConnect())
                widgets.setOnClickListener(buttonEdit, onEdit())

                if (wheel!!.voltageStart == null) {
                    wheel = wheel!!.copy(voltageStart = wheel!!.voltageMax)
                    it.saveWheel(wheel)
                }

                textName.text = wheel!!.name
                editVoltageStart.setText("${wheel!!.voltageStart}")
                textBtName.text = wheel!!.btName
                textMileage.text = textKm(wheel!!.totalMileage())

                updateCalculatedValues(READ_KM, READ_VOLTAGE_ACTUAL, READ_VOLTAGE_START)
            }
        }
    }

    fun onConnect(): (View) -> Unit = {
        if (wheel!!.btName == null) {
            goto(R.id.action_WheelViewFragment_to_WheelScanFragment, wheel!!)

        } else {
            fragments.runWithWait {
                external.bluetooth().getDeviceInfo(wheel!!.btAddr) {
                    fragments.doneWaiting(it) {
                        val newKm = round(it!!.km, NB_DECIMALS)
                        val newMileage = it.mileage.roundToInt()
                        val newVoltage = round(it.voltage, NB_DECIMALS)

                        updateWheel(newKm, newMileage, newVoltage)
                    }
                }
            }
        }
    }

    fun onEdit(): (View) -> Unit = {
        goto(R.id.action_WheelViewFragment_to_WheelEditFragment, wheel!!)
    }

    fun onUpdateKm() = { km: String ->
        updateCalculatedValues(km, READ_VOLTAGE_ACTUAL, READ_VOLTAGE_START)
    }

    fun onUpdateVoltageActual() = { voltageActual: String ->
        updateCalculatedValues(READ_KM, voltageActual, READ_VOLTAGE_START)
    }

    fun onUpdateVoltageStart() = { voltageStart: String ->
        if (isVoltageWithinRange(voltageStart)) {
            wheel = wheel!!.copy(voltageStart = floatOf(voltageStart))
            external.runDB { it.saveWheel(wheel) }
        }

        updateCalculatedValues(READ_KM, READ_VOLTAGE_ACTUAL, voltageStart)
    }

    private fun formatPercentage(voltage: Float): String {
        return when (val percentage = calculatorService.roundedPercentage(wheel, voltage)) {
            in 0f..100f -> "%.1f%%".format(ENGLISH, percentage)
            else -> ""
        }
    }

    private fun isVoltageWithinRange(voltageParm: String): Boolean {
        if (isEmpty(voltageParm))
            return false

        if (!isNumeric(voltageParm))
            return false

        val voltage = floatOf(voltageParm)
        if (voltage < wheel!!.voltageMin || wheel!!.voltageMax < voltage)
            return false

        return true
    }

    private fun textKm(value: Int): String {
        val labelKm = fragments.string(R.string.label_km)
        return "$value $labelKm"
    }

    private fun textKmWithDecimal(value: Float): String {
        val labelKm = fragments.string(R.string.label_km)
        return "$value $labelKm"
            .replace("0.0", "0")
    }

    private fun textWhPerKm(value: Float): String {
        val labelWhPerKm = fragments.string(R.string.label_wh_per_km)
        return "$value $labelWhPerKm"
    }

    private fun updateCalculatedValues(kmParm: String?, voltageActualParm: String?, voltageStartParm: String?) {
        val km = kmParm ?: widgets.text(editKm)
        val voltageActual = voltageActualParm ?: widgets.text(editVoltageActual)
        val voltageStart = voltageStartParm ?: widgets.text(editVoltageStart)

        updatePercentage(voltageActual)
        updateEstimatedValues(km, voltageActual, voltageStart)
    }

    private fun updateEstimatedValues(km: String, voltageActual: String, voltageStart: String) {
        if (!isEmpty(km) && isPositive(km)
            && isVoltageWithinRange(voltageActual)
            && isVoltageWithinRange(voltageStart)
        ) {
            val values = calculatorService.estimatedValues(wheel, floatOf(voltageActual), floatOf(km))

            textRemainingRange.text = textKmWithDecimal(if (values.remainingRange > 0) values.remainingRange else 0f)
            textTotalRange.text = textKmWithDecimal(values.totalRange)
            textWhPerKm.text = textWhPerKm(values.whPerKm)

        } else {
            textRemainingRange.text = ""
            textTotalRange.text = ""
            textWhPerKm.text = ""
        }
    }

    private fun updatePercentage(voltageActual: String) {
        textBattery.text = when {
            isVoltageWithinRange(voltageActual) -> formatPercentage(floatOf(voltageActual))
            else -> ""
        }
    }

    private fun updateWheel(newKm: Float, newMileage: Int, newVoltage: Float) {
        wheel = wheel!!.copy(mileage = newMileage)

        if (newKm < 0.1f) {
            wheel = wheel!!.copy(voltageStart = newVoltage)
            fragments.runUI { editVoltageStart.setText("${wheel!!.voltageStart}") }
        }

        external.runDB { it.saveWheel(wheel) }

        fragments.runUI {
            textMileage.text = textKm(wheel!!.totalMileage())
            editKm.setText("$newKm")
            editVoltageActual.setText("$newVoltage")
        }
    }
}
