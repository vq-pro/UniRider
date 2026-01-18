package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import kotlin.math.roundToInt

class WheelViewFragment : BaseFragment() {

    internal lateinit var buttonCharge: Button
    internal lateinit var buttonConnect: Button
    internal lateinit var buttonEdit: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var labelBattery: TextView
    internal lateinit var labelBtName: TextView
    internal lateinit var labelRemainingRange: TextView
    internal lateinit var labelTotalRange: TextView
    internal lateinit var textBattery: TextView
    internal lateinit var textBtAddr: TextView
    internal lateinit var textBtName: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var textRemainingRange: TextView
    internal lateinit var textTotalRange: TextView

    internal lateinit var estimates: EstimatedValues

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCharge = view.findViewById(R.id.button_charge)
        buttonConnect = view.findViewById(R.id.button_connect)
        buttonEdit = view.findViewById(R.id.button_edit)
        editKm = view.findViewById(R.id.edit_km)
        editVoltageActual = view.findViewById(R.id.edit_voltage_actual)
        labelBattery = view.findViewById(R.id.label_battery)
        labelBtName = view.findViewById(R.id.label_bt_name)
        labelRemainingRange = view.findViewById(R.id.label_remaining_range)
        labelTotalRange = view.findViewById(R.id.label_total_range)
        textBattery = view.findViewById(R.id.view_battery)
        textBtAddr = view.findViewById(R.id.view_bt_addr)
        textBtName = view.findViewById(R.id.view_bt_name)
        textMileage = view.findViewById(R.id.view_mileage)
        textName = view.findViewById(R.id.view_name)
        textRemainingRange = view.findViewById(R.id.view_remaining_range)
        textTotalRange = view.findViewById(R.id.view_total_range)

        widgets.addTextChangedListener(editKm, onUpdateKm())
        widgets.addTextChangedListener(editVoltageActual, onUpdateVoltageActual())
        widgets.setOnClickListener(buttonCharge, onCharge())
        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.setOnClickListener(buttonEdit, onEdit())
        widgets.setOnLongClickListener(textBtName, onDisconnect())

        external.runDB {
            fragments.runUI {
                if (!wheel!!.isSold) initialDisplayWheel()
                else initialDisplaySoldWheel()

                textMileage.text = textKm(wheel!!.totalMileage())
            }
        }
    }

    fun onCharge(): (View) -> Unit = {
        if (wheel!!.isConnected()) reconnect { startCharging() }
        else startCharging()
    }

    fun onConnect(): (View) -> Unit = {
        if (wheel!!.isConnected()) reconnect()
        else scan()
    }

    fun onDisconnect(): (View) -> Unit = {
        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelConfirmationDisconnectFragment)
    }

    fun onEdit(): (View) -> Unit = {
        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelEditFragment)
    }

    fun onUpdateKm() = { rawKm: String ->
        refreshDisplay(readVoltageActual(), parseKm(rawKm))
    }

    fun onUpdateVoltageActual() = { voltageActual: String ->
        refreshDisplay(parseVoltage(voltageActual), readKm())
    }

    internal fun clearDisplay() {
        clearPercentage()
        clearEstimates()
    }

    internal fun clearEstimates() {
        fragments.runUI {
            hide(textRemainingRange)
            hide(textTotalRange)
            widgets.disable(buttonCharge)
        }
    }

    internal fun clearPercentage() {
        hide(textBattery)
    }

    internal fun initialDisplayBluetoothSettings() {
        show(textBtName, wheel!!.btName!!)
        show(textBtAddr, wheel!!.btAddr!!)
    }

    @SuppressLint("SetTextI18n")
    internal fun initialDisplaySoldWheel() {
        textName.text = "${wheel!!.name} (${fragments.string(R.string.label_wheel_sold)})"

        buttonCharge.isVisible = false
        buttonConnect.isVisible = false
    }

    internal fun initialDisplayWheel() {
        textName.text = wheel!!.name

        if (wheel!!.isConnected())
            initialDisplayBluetoothSettings()

        refreshDisplay(readVoltageActual(), readKm())
    }

    internal fun parseKm(value: String): Float? =
        if (!isNumeric(value)) null
        else if (floatOf(value) == 0f) null
        else floatOf(value)

    internal fun parseVoltage(value: String): Float? =
        when {
            !isNumeric(value) -> null
            floatOf(value) < wheel!!.voltageMin -> null
            else -> round(floatOf(value))
        }

    internal fun readKm() =
        parseKm(widgets.getText(editKm))

    internal fun readVoltageActual() =
        parseVoltage(widgets.getText(editVoltageActual))

    internal fun reconnect(execution: () -> Unit = {}) {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr!!) {
                fragments.doneWaiting(it) {
                    val newKm = round(it.km / wheel!!.distanceOffset)
                    val newMileage = it.mileage.roundToInt()
                    val newVoltage = round(it.voltage)

                    updateWheel(newKm, newMileage, newVoltage)

                    fragments.runUI { execution.invoke() }
                }
            }
        }
    }

    internal fun refreshDisplay(voltageActual: Float?, km: Float?) {
        if (voltageActual == null) clearDisplay()
        else {
            updatePercentageFor(voltageActual)

            if (km == null) clearEstimates()
            else refreshEstimates(voltageActual, km)
        }
    }

    internal fun refreshEstimates(voltage: Float, km: Float) {
        fragments.runUI {
            estimates = calculatorService.estimatedValues(wheel!!, voltage, km)

            show(textRemainingRange, textKmWithDecimal(estimates.remainingRange))
            show(textTotalRange, textKmWithDecimal(estimates.totalRange))
            widgets.enable(buttonCharge)
        }
    }

    internal fun scan() {
        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelScanFragment)
    }

    internal fun startCharging() {
        chargeContext.km = readKm()!!
        chargeContext.voltage = readVoltageActual()!!

        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelChargeFragment)
    }

    internal fun updatePercentageFor(voltageActual: Float) {
        fragments.runUI {
            val percentage = calculatorService.percentage(wheel!!, voltageActual)
            show(textBattery, textPercentageWithDecimal(percentage))
        }
    }

    private fun hide(field: TextView) {
        show(field, false)
    }

    private fun show(field: TextView, display: Boolean) {
        field.isVisible = display

        when (field) {
            textBtName,
            textBtAddr -> labelBtName.isVisible = display

            textBattery -> labelBattery.isVisible = display
            textRemainingRange -> labelRemainingRange.isVisible = display
            textTotalRange -> labelTotalRange.isVisible = display
        }
    }

    private fun show(field: TextView, value: String) {
        field.text = value
        show(field, true)
    }

    @SuppressLint("SetTextI18n")
    private fun updateWheel(newKm: Float, newMileage: Int, newVoltage: Float) {
        wheel = wheel!!.copy(mileage = newMileage)
        external.runDB { db -> db.saveWheel(wheel) }

        fragments.runUI {
            textMileage.text = textKm(wheel!!.totalMileage())
            editKm.setText("$newKm")
            editVoltageActual.setText("$newVoltage")
        }
    }
}
