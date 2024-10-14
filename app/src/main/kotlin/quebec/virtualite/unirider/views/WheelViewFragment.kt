package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import quebec.virtualite.commons.android.utils.CollectionUtils.indexOf
import quebec.virtualite.commons.android.utils.CollectionUtils.serialize
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import kotlin.math.roundToInt

const val MAXIMUM_RATE_TRESHOLD = 200f
const val MINIMUM_RATE_TRESHOLD = 10f

open class WheelViewFragment : BaseFragment() {

    internal lateinit var buttonCharge: Button
    internal lateinit var buttonConnect: Button
    internal lateinit var buttonEdit: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var editVoltageStart: EditText
    internal lateinit var labelBattery: TextView
    internal lateinit var labelRate: TextView
    internal lateinit var labelRemainingRange: TextView
    internal lateinit var labelTotalRange: TextView
    internal lateinit var spinnerRate: Spinner
    internal lateinit var textBattery: TextView
    internal lateinit var textBtName: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var textRemainingRange: TextView
    internal lateinit var textTotalRange: TextView

    internal val listOfRates = ArrayList<String>()
    internal var selectedRate: Int? = null

    internal var estimates: EstimatedValues? = null

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCharge = view.findViewById(R.id.button_charge)
        buttonConnect = view.findViewById(R.id.button_connect_view)
        buttonEdit = view.findViewById(R.id.button_edit)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        editVoltageStart = view.findViewById(R.id.edit_voltage_start)
        labelBattery = view.findViewById(R.id.label_battery)
        labelRate = view.findViewById(R.id.label_rate)
        labelRemainingRange = view.findViewById(R.id.label_remaining_range)
        labelTotalRange = view.findViewById(R.id.label_total_range)
        textBattery = view.findViewById(R.id.view_battery)
        textBtName = view.findViewById(R.id.view_bt_name)
        textMileage = view.findViewById(R.id.view_mileage)
        textName = view.findViewById(R.id.view_name)
        textRemainingRange = view.findViewById(R.id.view_remaining_range)
        textTotalRange = view.findViewById(R.id.view_total_range)
        spinnerRate = view.findViewById(R.id.spinner_rate)

        widgets.addTextChangedListener(editVoltageStart, onUpdateVoltageStart())
        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.setOnClickListener(buttonCharge, onCharge())
        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.setOnClickListener(buttonEdit, onEdit())
        widgets.setOnItemSelectedListener(spinnerRate, onChangeRate())
        widgets.stringListAdapter(spinnerRate, view, listOfRates)

        external.runDB {
            fragments.runUI {
                if (!wheel2!!.isSold) {
                    editVoltageStart.setText("${wheel2!!.voltageStart}")
                    textName.text = wheel2!!.name
                    textBtName.text = wheel2!!.btName

                } else {
                    textName.text = "${wheel2!!.name} (${fragments.string(R.string.label_wheel_sold)})"

                    buttonCharge.visibility = GONE
                    buttonConnect.visibility = GONE
                }

                textMileage.text = textKm(wheel2!!.totalMileage())

                clearDisplay()
                refreshRates()
            }
        }
    }

    fun onChangeRate(): (View?, Int, String) -> Unit = { view, position, text ->
        selectedRate = position
        refreshDisplay(readVoltageStart(), readVoltageActual(), readKm())
    }

    fun onCharge(): (View) -> Unit = {
        with(fragments.sharedPreferences().edit()) {
            putString(PARAMETER_RATES, serialize(listOfRates))
            putInt(PARAMETER_SELECTED_RATE, selectedRate!!)
            putFloat(PARAMETER_VOLTAGE, readVoltageActual()!!)
            apply()
        }

        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelChargeFragment)
    }

    fun onConnect(): (View) -> Unit = {
        if (wheel2!!.btName == null) {
            fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelScanFragment)

        } else {
            fragments.runWithWait {
                external.bluetooth().getDeviceInfo(wheel2!!.btAddr) {
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
        selectedRate = null
        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelEditFragment)
    }

    fun onUpdateKm() = { rawKm: String ->
        selectedRate = null
        refreshDisplay(readVoltageStart(), readVoltageActual(), parseKm(rawKm))
        refreshRates()
    }

    fun onUpdateVoltageActual() = { voltageActual: String ->
        selectedRate = null
        refreshDisplay(readVoltageStart(), parseVoltage(voltageActual), readKm())
        refreshRates()
    }

    fun onUpdateVoltageStart() = { voltageStart: String ->
        var voltage = parseVoltage(voltageStart)
        when {
            voltage != null && isVoltageWithinRange(voltage) -> {
                wheel2 = wheel2!!.copy(voltageStart = voltage)
                external.runDB { db -> db.saveWheel(wheel2) }
            }

            else -> voltage = null
        }

        selectedRate = null
        refreshDisplay(voltage, readVoltageActual(), readKm())
        refreshRates()
    }

    private fun isVoltageWithinRange(voltage: Float): Boolean {
        return !(voltage < wheel2!!.voltageMin || (wheel2!!.voltageMax + 3f) < voltage)
    }

    @SuppressLint("SetTextI18n")
    private fun updateWheel(newKm: Float, newMileage: Int, newVoltage: Float) {
        wheel2 = wheel2!!.copy(mileage = newMileage)

        if (newKm < 0.1f) {
            wheel2 = wheel2!!.copy(voltageStart = newVoltage)
            fragments.runUI { editVoltageStart.setText("${wheel2!!.voltageStart}") }
        }

        external.runDB { it.saveWheel(wheel2) }

        fragments.runUI {
            textMileage.text = textKm(wheel2!!.totalMileage())
            editKm.setText("$newKm")
            editVoltageActual.setText("$newVoltage")
        }
    }

    internal open fun clearDisplay() {
        clearPercentage()
        clearEstimates()
    }

    internal open fun clearEstimates() {
        widgets.hide(
            spinnerRate, labelRate, textRemainingRange, labelRemainingRange, textTotalRange, labelTotalRange
        )
        widgets.disable(buttonCharge)
    }

    internal open fun clearPercentage() {
        widgets.hide(
            textBattery, labelBattery
        )
    }

    internal open fun parseKm(value: String): Float? = when {
        isNumeric(value) -> when {
            floatOf(value) == 0f -> null
            else -> floatOf(value)
        }

        else -> null
    }

    internal open fun parseVoltage(value: String): Float? = when {
        !isNumeric(value) -> null
        floatOf(value) < wheel2!!.voltageMin -> null
        else -> round(floatOf(value), NB_DECIMALS)
    }

    internal open fun readKm(): Float? {
        return parseKm(widgets.getText(editKm))
    }

    internal open fun readVoltageActual(): Float? {
        return parseVoltage(widgets.getText(editVoltageActual))
    }

    internal open fun readVoltageStart(): Float? {
        return parseVoltage(widgets.getText(editVoltageStart))
    }

    internal open fun refreshDisplay(voltageStart: Float?, voltageActual: Float?, km: Float?) {
        when (voltageActual) {
            null -> clearDisplay()
            else -> {
                updatePercentageFor(voltageActual)

                when {
                    voltageStart == null -> clearEstimates()
                    km == null -> clearEstimates()
                    else -> refreshEstimates(voltageActual, km)
                }
            }
        }
    }

    internal open fun refreshEstimates(voltage: Float, km: Float) {
        val rateOverride = if (selectedRate == null) null else floatOf(listOfRates[selectedRate!!])
        estimates = calculatorService.estimatedValues(wheel2!!, voltage, km, rateOverride)

        when {
            estimates!!.whPerKm < MINIMUM_RATE_TRESHOLD -> clearEstimates()
            estimates!!.whPerKm > MAXIMUM_RATE_TRESHOLD -> clearEstimates()
            else -> {
                textRemainingRange.text = textKmWithDecimal(estimates!!.remainingRange)
                textTotalRange.text = textKmWithDecimal(estimates!!.totalRange)

                widgets.show(
                    spinnerRate, labelRate, textRemainingRange, labelRemainingRange, textTotalRange, labelTotalRange
                )
                widgets.enable(buttonCharge)
            }
        }
    }

    internal open fun refreshRates() {
        val actualRate = estimates?.whPerKm

        selectedRate = null
        listOfRates.clear()

        if (actualRate == null) {
            widgets.clearSelection(spinnerRate)
            return
        }

        val displayActualRate = textWhPerKm(actualRate)
        listOfRates.add(displayActualRate)

        val stepDown = (actualRate / 5).toInt() * 5
        for (i in stepDown - 10..stepDown + 15 step 5) {
            if (i < 10) continue

            listOfRates.add(i.toString())
        }

        listOfRates.sort()

        selectedRate = indexOf(listOfRates, displayActualRate)
        widgets.setSelection(spinnerRate, selectedRate!!)
    }

    internal open fun updatePercentageFor(voltageActual: Float) {
        val percentage = calculatorService.percentage(wheel2!!, voltageActual)

        textBattery.text = textPercentageWithDecimal(percentage)
        widgets.show(textBattery, labelBattery)
    }
}
