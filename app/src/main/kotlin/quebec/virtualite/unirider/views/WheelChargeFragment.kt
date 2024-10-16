package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import quebec.virtualite.commons.android.utils.CollectionUtils.setList
import quebec.virtualite.commons.android.utils.DateUtils
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import java.lang.Float.parseFloat
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val DTF = DateTimeFormatter.ofPattern("HH:mm")

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var buttonConnect: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var spinnerRate: Spinner
    internal lateinit var textName: TextView
    internal lateinit var textRemainingTime: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var switchFullCharge: SwitchMaterial

    internal val dateUtils = DateUtils()

    internal val parmRates: ArrayList<String> = ArrayList()
    internal var parmSelectedRate: Int = -1
    internal var parmVoltageDisconnectedFromCharger: Float? = 0f

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setList(parmRates, chargeContext.rates)
        parmSelectedRate = chargeContext.selectedRate
        parmVoltageDisconnectedFromCharger = chargeContext.voltage

        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect_charge)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        spinnerRate = view.findViewById(R.id.spinner_rate)
        textName = view.findViewById(R.id.view_name)
        textRemainingTime = view.findViewById(R.id.view_remaining_time)
        textVoltageRequired = view.findViewById(R.id.view_voltage_required)
        switchFullCharge = view.findViewById(R.id.check_full_charge)

        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.setOnItemSelectedListener(spinnerRate, onChangeRate())
        widgets.stringListAdapter(spinnerRate, view, parmRates)
        widgets.setOnCheckedChangeListener(switchFullCharge, onToggleFullCharge())

        fragments.runUI {
            textName.text = wheel!!.name
            widgets.setSelection(spinnerRate, parmSelectedRate)
            displayVoltageActual()

            if (wheel!!.btName == null || wheel!!.btAddr == null) {
                widgets.disable(buttonConnect)
            }

            switchFullCharge.isChecked = true
        }
    }

    fun onChangeRate(): (View?, Int, String) -> Unit = { view, position, text ->
        parmSelectedRate = position
        updateEstimates()
    }

    fun onConnect(): (View) -> Unit = {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr) { live ->
                fragments.doneWaiting(live) {
                    parmVoltageDisconnectedFromCharger = round(live!!.voltage - wheel!!.chargerOffset, NB_DECIMALS)
                    displayVoltageActual()
                    updateEstimates()
                }
            }
        }
    }

    fun onToggleFullCharge() = { useFullCharge: Boolean ->
        if (useFullCharge)
            widgets.disable(editKm, spinnerRate)
        else
            widgets.enable(editKm, spinnerRate)

        updateEstimates()
    }

    fun onUpdateKm() = { km: String ->
        when {
            isNumeric(km) -> updateEstimates()
            else -> blankEstimates()
        }
    }

    fun onUpdateVoltageActual() = { voltage: String ->
        when {
            isNumeric(voltage) && floatOf(voltage) >= wheel!!.voltageMin -> {
                parmVoltageDisconnectedFromCharger = round(parseFloat(voltage) - wheel!!.chargerOffset, NB_DECIMALS)
                updateEstimates()
            }

            else -> blankEstimates()
        }
    }

    @SuppressLint("SetTextI18n")
    internal open fun blankEstimates() {
        textRemainingTime.text = ""
        textVoltageRequired.text = ""
    }

    @SuppressLint("DefaultLocale")
    internal fun timeDisplay(rawHours: Float): String {
        val hours = rawHours.toInt()
        val minutes = ((rawHours - hours) * 60).roundToInt()

        val time = dateUtils.now()
            .plusHours(hours.toLong())
            .plusMinutes(minutes.toLong())

        return DTF.format(time)
    }

    @SuppressLint("SetTextI18n")
    internal open fun updateEstimates() {
        val requiredVoltageOnCharger = when {
            switchFullCharge.isChecked -> calculatorService.requiredFullVoltage(wheel)
            else -> {
                val km = floatOf(widgets.getText(editKm))
                if (km < 0.1f) {
                    blankEstimates()
                    return
                }

                val whPerKm = floatOf(parmRates[parmSelectedRate])
                calculatorService.requiredVoltage(wheel!!, whPerKm, km)
            }
        }

        val requiredVoltage = requiredVoltageOnCharger - (wheel?.chargerOffset ?: 0f)
        if (requiredVoltage > parmVoltageDisconnectedFromCharger!!) {
            val diff = round(requiredVoltage - parmVoltageDisconnectedFromCharger!!, NB_DECIMALS)
            val rawHours = diff / wheel!!.chargeRate

            textVoltageRequired.text = "${requiredVoltageOnCharger}V (+$diff)"
            textRemainingTime.text = timeDisplay(rawHours)

        } else {
            textVoltageRequired.text = "Go!"
            textRemainingTime.text = ""
        }
    }

    @SuppressLint("SetTextI18n")
    internal fun displayVoltageActual() {
        editVoltageActual.setText(
            "${round(parmVoltageDisconnectedFromCharger?.plus(wheel!!.chargerOffset)!!, NB_DECIMALS)}"
        )
    }
}
