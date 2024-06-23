package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.Companion.CHARGER_OFFSET
import java.lang.Float.parseFloat
import java.lang.String.format
import kotlin.math.roundToInt

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var buttonConnect: Button
    internal lateinit var checkMaxCharge: CheckBox
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var listRates: Spinner
    internal lateinit var textName: TextView
    internal lateinit var textRemainingTime: TextView
    internal lateinit var textVoltageRequired: TextView

    internal var parmWheelId: Long? = 0

    internal val parmRates: ArrayList<String> = ArrayList()
    internal var parmSelectedRate: Int = -1
    internal var parmVoltageDisconnectedFromCharger: Float? = 0f
    internal var wheel: WheelEntity? = null

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        setList(parmRates, arguments?.getStringArrayList(PARAMETER_RATES)!!)
        parmSelectedRate = arguments?.getInt(PARAMETER_SELECTED_RATE)!!
        parmVoltageDisconnectedFromCharger = arguments?.getFloat(PARAMETER_VOLTAGE)
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect_charge)
        checkMaxCharge = view.findViewById(R.id.check_maximum_charge)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        listRates = view.findViewById(R.id.view_wh_per_km)
        textName = view.findViewById(R.id.view_name)
        textRemainingTime = view.findViewById(R.id.view_remaining_time)
        textVoltageRequired = view.findViewById(R.id.view_voltage_required)

        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.setOnCheckedChangeListener(checkMaxCharge, onToggleMaxCharge())
        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.setOnItemSelectedListener(listRates, onChangeRate())
        widgets.stringListAdapter(listRates, view, parmRates)

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            fragments.runUI {
                textName.text = wheel!!.name
                widgets.setSelection(listRates, parmSelectedRate)
                displayVoltageActual()

                if (wheel!!.btName == null || wheel!!.btAddr == null) {
                    widgets.disable(buttonConnect)
                }

                checkMaxCharge.isChecked = true
            }
        }
    }

    fun onChangeRate(): (View?, Int, String) -> Unit = { view, position, text ->
        parmSelectedRate = position
        updateEstimates()
    }

    fun onConnect(): (View) -> Unit = {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr) {
                fragments.doneWaiting(it) {
                    parmVoltageDisconnectedFromCharger = round(it!!.voltage, NB_DECIMALS) - CHARGER_OFFSET
                    displayVoltageActual()
                    updateEstimates()
                }
            }
        }
    }

    fun onToggleMaxCharge() = { useMaxCharge: Boolean ->
        if (useMaxCharge) {
            widgets.hide(editKm)
            widgets.hide(listRates)

        } else {
            widgets.show(editKm)
            widgets.show(listRates)
        }

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
                parmVoltageDisconnectedFromCharger = parseFloat(voltage) - CHARGER_OFFSET
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
            checkMaxCharge.isChecked -> calculatorService.requiredMaxVoltage(wheel)
            else -> {
                val km = floatOf(widgets.getText(editKm))
                val whPerKm = floatOf(parmRates[parmSelectedRate])
                calculatorService.requiredVoltage(wheel!!, whPerKm, km)
            }
        }

//        parmVoltageDisconnectedFromCharger == null -> {
//            textVoltageRequired.text = ""
//            textRemainingTime.text = ""
//        }

        val requiredVoltage = requiredVoltageOnCharger - CHARGER_OFFSET
        if (requiredVoltage > parmVoltageDisconnectedFromCharger!!) {
            val diff = round(requiredVoltage - parmVoltageDisconnectedFromCharger!!, 1)
            val rawHours = diff / wheel!!.chargeRate

            textVoltageRequired.text = "${requiredVoltageOnCharger}V (+$diff)"
            textRemainingTime.text = timeDisplay(rawHours)

        } else {
            textVoltageRequired.text = "Go!"
            textRemainingTime.text = ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayVoltageActual() {
        editVoltageActual.setText("${parmVoltageDisconnectedFromCharger?.plus(CHARGER_OFFSET)}")
    }
}
