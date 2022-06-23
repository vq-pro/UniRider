package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.apache.http.util.TextUtils.isEmpty
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import java.lang.Float.parseFloat
import java.util.Locale.ENGLISH
import kotlin.math.roundToInt

open class WheelViewFragment : BaseFragment() {

    private val NB_DECIMALS = 1

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

                textName.text = wheel!!.name
                editVoltageStart.setText("${wheel!!.voltageStart ?: wheel!!.voltageMax}")
                textBtName.text = wheel!!.btName
                textMileage.text = "${wheel!!.totalMileage()}"

                val km = widgets.text(editKm)
                val voltageCurrent = widgets.text(editVoltageActual)

                updateEstimatedValues(km, voltageCurrent)
                updatePercentage(voltageCurrent)
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
        updateEstimatedValues(km.trim(), widgets.text(editVoltageActual))
    }

    fun onUpdateVoltageActual() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        updateEstimatedValues(widgets.text(editKm), voltage)
        updatePercentage(voltage)
    }

    fun onUpdateVoltageStart() = { voltageParm: String ->
        val voltage = voltageParm.trim()

        updateEstimatedValues(widgets.text(editKm), voltage)
        updatePercentage(voltage)

        if (isVoltageWithinRange(voltage)) {
            wheel = wheel!!.copy(voltageStart = parseFloat(voltage))
            external.runDB { it.saveWheel(wheel) }
        }
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

        val voltage = parseFloat(voltageParm)
        if (voltage < wheel!!.voltageMin || wheel!!.voltageMax < voltage)
            return false

        return true
    }

    @SuppressLint("SetTextI18n")
    private fun updateEstimatedValues(km: String, voltage: String) {
        if (isEmpty(km) || !isPositive(km) || !isVoltageWithinRange(voltage)) {
            textRemainingRange.text = ""
            textTotalRange.text = ""
            textWhPerKm.text = ""
            return
        }

        val values = calculatorService
            .estimatedValues(wheel, parseFloat(voltage), parseFloat(km))

        val labelKm = fragments.string(R.string.label_km)
        val labelWhPerKm = fragments.string(R.string.label_wh_per_km)

        textRemainingRange.text =
            "${if (values.remainingRange > 0) values.remainingRange else 0} $labelKm"
        textTotalRange.text =
            "${values.totalRange} $labelKm"
        textWhPerKm.text =
            "${values.whPerKm} $labelWhPerKm"
    }

    private fun isNumeric(value: String): Boolean {
        return value.matches("^[0-9.]*$".toRegex())
    }

    private fun isPositive(value: String): Boolean {
        return isNumeric(value) && parseFloat(value) > 0f
    }

    private fun updatePercentage(voltage: String) {
        textBattery.text = if (isVoltageWithinRange(voltage))
            formatPercentage(parseFloat(voltage)) else ""
    }

    private fun updateWheel(newKm: Float, newMileage: Int, newVoltage: Float) {
        wheel = wheel!!.copy(mileage = newMileage)
        external.runDB { it.saveWheel(wheel) }

        fragments.runUI {
            textMileage.text = "${wheel!!.totalMileage()}"
            editKm.setText("$newKm")
            editVoltageActual.setText("$newVoltage")
        }
    }
}
