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
import quebec.virtualite.commons.android.utils.CollectionUtils.deserialize
import quebec.virtualite.commons.android.utils.CollectionUtils.setList
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import java.lang.Float.parseFloat
import java.lang.String.format
import kotlin.math.roundToInt

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var buttonConnect: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var spinnerRate: Spinner
    internal lateinit var textName: TextView
    internal lateinit var textRemainingTime: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var switchFullCharge: SwitchMaterial

    internal val parmRates: ArrayList<String> = ArrayList()
    internal var parmSelectedRate: Int = -1
    internal var parmVoltageDisconnectedFromCharger: Float? = 0f

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val sharedPreferences = fragments.sharedPreferences()
        setList(parmRates, deserialize(sharedPreferences.getString(PARAMETER_RATES, null)!!))
        parmSelectedRate = sharedPreferences.getInt(PARAMETER_SELECTED_RATE, 0)
        parmVoltageDisconnectedFromCharger = sharedPreferences.getFloat(PARAMETER_VOLTAGE, 0f)

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
            textName.text = wheel2!!.name
            widgets.setSelection(spinnerRate, parmSelectedRate)
            displayVoltageActual()

            if (wheel2!!.btName == null || wheel2!!.btAddr == null) {
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
            external.bluetooth().getDeviceInfo(wheel2!!.btAddr) { live ->
                fragments.doneWaiting(live) {
                    parmVoltageDisconnectedFromCharger = round(live!!.voltage - wheel2!!.chargerOffset, NB_DECIMALS)
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
            isNumeric(voltage) && floatOf(voltage) >= wheel2!!.voltageMin -> {
                parmVoltageDisconnectedFromCharger = round(parseFloat(voltage) - wheel2!!.chargerOffset, NB_DECIMALS)
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

        return if (hours > 0) "${hours}h${format("%02d", minutes)}" else "${minutes}m"
    }

    @SuppressLint("SetTextI18n")
    internal open fun updateEstimates() {
        val requiredVoltageOnCharger = when {
            switchFullCharge.isChecked -> calculatorService.requiredFullVoltage(wheel2)
            else -> {
                val km = floatOf(widgets.getText(editKm))
                if (km < 0.1f) {
                    blankEstimates()
                    return
                }

                val whPerKm = floatOf(parmRates[parmSelectedRate])
                calculatorService.requiredVoltage(wheel2!!, whPerKm, km)
            }
        }

        val requiredVoltage = requiredVoltageOnCharger - (wheel2?.chargerOffset ?: 0f)
        if (requiredVoltage > parmVoltageDisconnectedFromCharger!!) {
            val diff = round(requiredVoltage - parmVoltageDisconnectedFromCharger!!, NB_DECIMALS)
            val rawHours = diff / wheel2!!.chargeRate

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
            "${round(parmVoltageDisconnectedFromCharger?.plus(wheel2!!.chargerOffset)!!, NB_DECIMALS)}"
        )
    }
}
