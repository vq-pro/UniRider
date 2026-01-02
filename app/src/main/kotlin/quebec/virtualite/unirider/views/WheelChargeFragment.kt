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
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var editVoltageRequired: EditText
    internal lateinit var textDiff: TextView
    internal lateinit var textEstimatedDiff: TextView
    internal lateinit var textEstimatedTime: TextView
    internal lateinit var textName: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var switchFullCharge: SwitchMaterial

    internal var cacheVoltageActual: Float? = 0f

    private val dateUtils = DateUtils()
    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect_charge)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        editVoltageRequired = view.findViewById(R.id.edit_voltage_required)
        textDiff = view.findViewById(R.id.view_diff)
        textEstimatedDiff = view.findViewById(R.id.view_estimated_diff)
        textEstimatedTime = view.findViewById(R.id.view_estimated_time)
        textName = view.findViewById(R.id.view_name)
        textVoltageRequired = view.findViewById(R.id.view_voltage_required)
        switchFullCharge = view.findViewById(R.id.check_full_charge)

        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.addTextChangedListener(editVoltageRequired, onUpdateVoltageRequired())
        widgets.setOnCheckedChangeListener(switchFullCharge, onToggleFullCharge())

        fragments.runUI {
            textName.text = wheel!!.name

            cacheVoltageActual = chargeContext.voltage + wheel!!.chargerOffset
            editVoltageActual.setText("$cacheVoltageActual")

            if (wheel!!.btName == null || wheel!!.btAddr == null) {
                widgets.disable(buttonConnect)
            }

            switchFullCharge.isChecked = true
        }
    }

    fun onConnect(): (View) -> Unit = {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr) { msg ->
                fragments.doneWaiting(msg) {
                    cacheVoltageActual = round(msg!!.voltage, NB_DECIMALS)
                    editVoltageActual.setText("$cacheVoltageActual")

                    display()
                }
            }
        }
    }

    fun onToggleFullCharge() = { useFullCharge: Boolean ->
        display()
    }

    fun onUpdateKm() = { km: String ->
        when {
            isNumeric(km) -> {
                switchFullCharge.isChecked = false
                display()
            }

            else -> displayBlanks()
        }
    }

    fun onUpdateVoltageActual() = { voltage: String ->
        when {
            isNumeric(voltage) && floatOf(voltage) >= wheel!!.voltageMin -> {
                this.cacheVoltageActual = floatOf(voltage)

                if (switchFullCharge.isChecked
                    || !widgets.getText(editKm).isEmpty()
                    || !widgets.getText(editVoltageRequired).isEmpty()
                )
                    display()
                else
                    displayBlanks()
            }

            else -> displayBlanks()
        }
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
        val voltageRequired = getVoltageRequired()

        when {
            voltageRequired > cacheVoltageActual!! -> {

                val diff = round(voltageRequired - cacheVoltageActual!!, NB_DECIMALS)
                val rawHours = diff / wheel!!.chargeRate

                displayEstimates(diff, rawHours)
            }

            else -> displayGo()
        }
    }

    internal open fun displayBlanks() {
        textDiff.text = ""
        textEstimatedDiff.text = ""
        textEstimatedTime.text = ""
        textVoltageRequired.text = ""
    }

    @SuppressLint("SetTextI18n")
    internal open fun displayEstimates(voltageDiff: Float, rawHours: Float) {
        textDiff.text = "(+$voltageDiff)"
        textEstimatedDiff.text = estimatedDiff(rawHours)
        textEstimatedTime.text = estimatedTime(rawHours)
    }

    @SuppressLint("SetTextI18n")
    internal open fun displayGo() {
        textDiff.text = "Go!"
        textEstimatedDiff.text = ""
        textEstimatedTime.text = ""
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

    internal open fun getVoltageRequired(): Float {
        val fieldVoltageRequired = widgets.getText(editVoltageRequired)
        val voltageRequired = when {
            !fieldVoltageRequired.isEmpty() -> {
                floatOf(fieldVoltageRequired)
            }

            switchFullCharge.isChecked -> {
                calculatorService.requiredVoltageFull(wheel)
            }

            else -> {
                val km = floatOf(widgets.getText(editKm))
                val requiredVoltageOffCharger = calculatorService.requiredVoltageOffCharger(wheel!!, chargeContext.voltage, chargeContext.km, km)

                min(requiredVoltageOffCharger + wheel!!.chargerOffset, wheel!!.voltageFull)
            }
        }

        textVoltageRequired.setText("$voltageRequired")
        return voltageRequired
    }
}
