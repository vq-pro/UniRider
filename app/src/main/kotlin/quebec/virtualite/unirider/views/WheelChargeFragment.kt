package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import quebec.virtualite.commons.android.utils.DateUtils
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

private val DTF = DateTimeFormatter.ofPattern("HH:mm")

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var buttonConnect: Button
    internal lateinit var editAmperage: EditText
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var editVoltageRequired: EditText
    internal lateinit var switchFullCharge: SwitchMaterial
    internal lateinit var textChargeWarning: TextView
    internal lateinit var textEstimatedDiff: TextView
    internal lateinit var textEstimatedTime: TextView
    internal lateinit var textName: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var textVoltageRequiredDiff: TextView
    internal lateinit var textVoltageTarget: TextView
    internal lateinit var textVoltageTargetDiff: TextView

    internal var cacheVoltageActual: Float? = null
    internal var chargerOffset: Float? = null

    private val dateUtils = DateUtils()
    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect_charge)
        editAmperage = view.findViewById(R.id.edit_charge_amperage)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        editVoltageRequired = view.findViewById(R.id.edit_voltage_required)
        switchFullCharge = view.findViewById(R.id.check_full_charge)
        textChargeWarning = view.findViewById(R.id.view_charge_warning)
        textEstimatedDiff = view.findViewById(R.id.view_estimated_diff)
        textEstimatedTime = view.findViewById(R.id.view_estimated_time)
        textName = view.findViewById(R.id.view_name)
        textVoltageRequired = view.findViewById(R.id.view_voltage_required)
        textVoltageRequiredDiff = view.findViewById(R.id.view_voltage_required_diff)
        textVoltageTarget = view.findViewById(R.id.view_voltage_target)
        textVoltageTargetDiff = view.findViewById(R.id.view_voltage_target_diff)

        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.addTextChangedListener(editAmperage, onUpdateAmperage())
        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.addTextChangedListener(editVoltageRequired, onUpdateVoltageRequired())
        widgets.setOnCheckedChangeListener(switchFullCharge, onToggleFullCharge())

        fragments.runUI {
            textName.text = wheel!!.name
            editAmperage.setText("${wheel!!.chargeAmperage}")

            cacheVoltageActual = chargeContext.voltage
            chargerOffset = null

            if (!wheel!!.isConnected())
                widgets.disable(buttonConnect)

            switchFullCharge.isChecked = true
        }
    }

    fun onConnect(): (View) -> Unit = {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr) { msg ->
                fragments.doneWaiting(msg) {
                    editVoltageActual.setText("${round(msg!!.voltage)}")
                    display()
                }
            }
        }
    }

    fun onToggleFullCharge() = { useFullCharge: Boolean ->
        display()
    }

    fun onUpdateAmperage() = { amperage: String ->
        if (!isNumeric(amperage)) displayBlanks()
        else display()
    }

    fun onUpdateKm() = { km: String ->
        if (!isNumeric(km)) displayBlanks()
        else {
            switchFullCharge.isChecked = false
            display()
        }
    }

    fun onUpdateVoltageActual() = { voltage: String ->
        if (isNumeric(voltage) && floatOf(voltage) >= wheel!!.voltageMin) {
            updateVoltageActual(floatOf(voltage))

            if (switchFullCharge.isChecked
                || !widgets.getText(editKm).isEmpty()
                || !widgets.getText(editVoltageRequired).isEmpty()
            )

                display()
            else
                displayBlanks()

        } else
            displayBlanks()
    }

    fun onUpdateVoltageRequired() = { voltage: String ->
        when {
            isNumeric(voltage) -> {
                switchFullCharge.isChecked = false
                editKm.setText("")

                display()
            }

            else -> displayBlanks()
        }
    }

    internal open fun display() {
        if (chargerOffset == null)
            return

        val voltageTarget = onCharge(getVoltageRequired())
        when {
            voltageTarget > cacheVoltageActual!! -> {

                val diff = round(voltageTarget - cacheVoltageActual!!)
                val chargeRateMultiplier = getAmperage() / wheel!!.chargeAmperage
                val rawHours = diff / (chargeRateMultiplier * wheel!!.chargeRate)

                displayEstimates(diff, rawHours)
            }

            else -> displayGo()
        }
    }

    internal open fun displayBlanks() {
        textEstimatedDiff.text = ""
        textEstimatedTime.text = ""
        textVoltageRequired.text = ""
        textVoltageRequiredDiff.text = ""
        textVoltageTarget.text = ""
        textVoltageTargetDiff.text = ""
    }

    @SuppressLint("SetTextI18n")
    internal open fun displayEstimates(voltageDiff: Float, rawHours: Float) {
        textVoltageTargetDiff.text = "V (+$voltageDiff)"
        textEstimatedDiff.text = estimatedDiff(rawHours)
        textEstimatedTime.text = estimatedTime(rawHours)
    }

    @SuppressLint("SetTextI18n")
    internal open fun displayGo() {
        textVoltageTargetDiff.text = "V"
        textEstimatedTime.text = "Go!"
        textEstimatedDiff.text = ""
    }

    @SuppressLint("DefaultLocale")
    internal open fun estimatedDiff(rawHours: Float): String {
        val hours = rawHours.toInt()
        val minutes = ((rawHours - hours) * 60).roundToInt()

        val remainingDuration = when {
            hours == 0 && minutes == 0 -> ""
            hours == 0 -> "(${minutes}m)"
            minutes == 0 -> "(${hours}h)"
            else -> "(${hours}h${minutes}m)"
        }

        return remainingDuration
    }

    @SuppressLint("DefaultLocale")
    internal open fun estimatedTime(rawHours: Float): String {
        val hours = rawHours.toInt()
        val minutes = ((rawHours - hours) * 60).roundToInt()

        val time = dateUtils.now().plusHours(hours.toLong()).plusMinutes(minutes.toLong())

        return DTF.format(time)
    }

    internal open fun getAmperage(): Float {
        var amperageString = widgets.getText(editAmperage)

        if (amperageString.isEmpty()) {
            amperageString = "${wheel?.chargeAmperage}"
            editAmperage.setText(amperageString)
        }

        return round(floatOf(amperageString))
    }

    internal open fun getVoltageRequired(): Float {
        val fieldVoltageRequired = widgets.getText(editVoltageRequired)
        val voltageRequired = when {
            switchFullCharge.isChecked -> {
                offCharge(calculatorService.requiredVoltageFull(wheel))
            }

            !fieldVoltageRequired.isEmpty() -> {
                round(floatOf(fieldVoltageRequired))
            }

            else -> {
                val km = floatOf(widgets.getText(editKm))
                val voltageRequired = calculatorService.requiredVoltageOffCharger(wheel!!, chargeContext.voltage, chargeContext.km, km)

                min(voltageRequired, offCharge(wheel!!.voltageFull))
            }
        }

        displayVoltageTargetAndRequired(voltageRequired)

        return voltageRequired
    }

    internal open fun updateVoltageActual(voltage: Float) {
        cacheVoltageActual = voltage

        if (chargerOffset == null) {
            fragments.runUI {
                widgets.hide(textChargeWarning)
            }

            chargerOffset = round(cacheVoltageActual!! - chargeContext.voltage)
        }
    }

    private fun displayVoltageTargetAndRequired(voltageRequired: Float) {
        textVoltageTarget.setText("${onCharge(voltageRequired)}")
        textVoltageRequired.setText("$voltageRequired")
        textVoltageRequiredDiff.setText("V (-$chargerOffset)")
    }

    private fun offCharge(voltage: Float) = round(voltage - chargerOffset!!)

    private fun onCharge(voltage: Float) = round(voltage + chargerOffset!!)
}
