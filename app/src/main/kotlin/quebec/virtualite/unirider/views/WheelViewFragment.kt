package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isNumeric
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import kotlin.math.roundToInt

open class WheelViewFragment : BaseFragment() {

    internal lateinit var buttonCharge: Button
    internal lateinit var buttonConnect: Button
    internal lateinit var buttonEdit: Button
    internal lateinit var editKm: EditText
    internal lateinit var editVoltageActual: EditText
    internal lateinit var labelBattery: TextView
    internal lateinit var labelRemainingRange: TextView
    internal lateinit var labelTotalRange: TextView
    internal lateinit var textBattery: TextView
    internal lateinit var textBtName: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var textRemainingRange: TextView
    internal lateinit var textTotalRange: TextView

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
        labelBattery = view.findViewById(R.id.label_battery)
        labelRemainingRange = view.findViewById(R.id.label_remaining_range)
        labelTotalRange = view.findViewById(R.id.label_total_range)
        textBattery = view.findViewById(R.id.view_battery)
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

        external.runDB {
            fragments.runUI {
                if (!wheel!!.isSold) {
                    textName.text = wheel!!.name
                    textBtName.text = wheel!!.btName

                } else {
                    textName.text = "${wheel!!.name} (${fragments.string(R.string.label_wheel_sold)})"

                    buttonCharge.visibility = GONE
                    buttonConnect.visibility = GONE
                }

                textMileage.text = textKm(wheel!!.totalMileage())

                refreshDisplay(readVoltageActual(), readKm())
            }
        }
    }

    fun onCharge(): (View) -> Unit = {
        chargeContext.km = readKm()!!
        chargeContext.voltage = readVoltageActual()!!

        fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelChargeFragment)
    }

    fun onConnect(): (View) -> Unit = {
        if (wheel!!.btName == null) {
            fragments.navigateTo(R.id.action_WheelViewFragment_to_WheelScanFragment)

        } else {
            fragments.runWithWait {
                external.bluetooth().getDeviceInfo(wheel!!.btAddr) {
                    fragments.doneWaiting(it) {
                        val newKm = it!!.km / wheel!!.distanceOffset
                        val newMileage = it.mileage.roundToInt()
                        val newVoltage = it.voltage

                        updateWheel(
                            round(newKm, NB_DECIMALS),
                            newMileage,
                            round(newVoltage, NB_DECIMALS)
                        )
                    }
                }
            }
        }
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

    @SuppressLint("SetTextI18n")
    private fun updateWheel(newKm: Float, newMileage: Int, newVoltage: Float) {
        wheel = wheel!!.copy(mileage = newMileage)
        external.runDB { db -> db.saveWheel(wheel) }

        fragments.runUI {
            textMileage.text = textKm(wheel!!.totalMileage())
            editKm.setText("$newKm")
            editVoltageActual.setText("$newVoltage")

            widgets.enable(buttonCharge)
        }
    }

    internal open fun clearDisplay() {
        clearPercentage()
        clearEstimates()
    }

    internal open fun clearEstimates() {
        fragments.runUI {
            widgets.hide(
                textRemainingRange, labelRemainingRange, textTotalRange, labelTotalRange
            )
            widgets.disable(buttonCharge)
        }
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
        floatOf(value) < wheel!!.voltageMin -> null
        else -> round(floatOf(value), NB_DECIMALS)
    }

    internal open fun readKm(): Float? {
        return parseKm(widgets.getText(editKm))
    }

    internal open fun readVoltageActual(): Float? {
        return parseVoltage(widgets.getText(editVoltageActual))
    }

    internal open fun refreshDisplay(voltageActual: Float?, km: Float?) {
        when (voltageActual) {
            null -> clearDisplay()
            else -> {
                updatePercentageFor(voltageActual)

                when {
                    km == null -> clearEstimates()
                    else -> refreshEstimates(voltageActual, km)
                }
            }
        }
    }

    internal open fun refreshEstimates(voltage: Float, km: Float) {
        fragments.runUI {
            estimates = calculatorService.estimatedValues(wheel!!, voltage, km)

            textRemainingRange.text = textKmWithDecimal(estimates!!.remainingRange)
            textTotalRange.text = textKmWithDecimal(estimates!!.totalRange)

            widgets.show(
                textRemainingRange, labelRemainingRange, textTotalRange, labelTotalRange
            )
            widgets.enable(buttonCharge)
        }
    }

    internal open fun updatePercentageFor(voltageActual: Float) {
        fragments.runUI {
            val percentage = calculatorService.percentage(wheel!!, voltageActual)

            textBattery.text = textPercentageWithDecimal(percentage)
            widgets.show(textBattery, labelBattery)
        }
    }
}
