package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.isEmpty
import quebec.virtualite.commons.android.utils.NumberUtils.isPositive
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.Companion.CHARGER_OFFSET
import java.lang.String.format
import kotlin.math.roundToInt

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var buttonConnect: Button
    internal lateinit var editKm: EditText
    internal lateinit var textName: TextView
    internal lateinit var textRemainingTime: TextView
    internal lateinit var textVoltageRequired: TextView
    internal lateinit var textWhPerKm: TextView

    internal var parmWheelId: Long? = 0

    internal var parmVoltageDisconnectedFromCharger: Float? = 0f
    internal var parmWhPerKm: Float? = 0f
    internal var wheel: WheelEntity? = null

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        parmVoltageDisconnectedFromCharger = arguments?.getFloat(PARAMETER_VOLTAGE)
        parmWhPerKm = arguments?.getFloat(PARAMETER_WH_PER_KM)
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConnect = view.findViewById(R.id.button_connect_charge)
        editKm = view.findViewById(R.id.edit_km)
        textName = view.findViewById(R.id.view_name)
        textRemainingTime = view.findViewById(R.id.view_remaining_time)
        textVoltageRequired = view.findViewById(R.id.view_required_voltage)
        textWhPerKm = view.findViewById(R.id.view_wh_per_km)

        widgets.setOnClickListener(buttonConnect, onConnect())
        widgets.addTextChangedListener(editKm, onUpdateKm())

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            textName.text = wheel!!.name
            textWhPerKm.text = textWhPerKm(parmWhPerKm)

            if (wheel!!.btName == null || wheel!!.btAddr == null) {
                widgets.disable(buttonConnect)
            }
        }
    }

    fun onConnect(): (View) -> Unit = {
        fragments.runWithWait {
            external.bluetooth().getDeviceInfo(wheel!!.btAddr) {
                fragments.doneWaiting(it) {
                    parmVoltageDisconnectedFromCharger = round(it!!.voltage, NB_DECIMALS) - CHARGER_OFFSET

                    onUpdateKm().invoke(widgets.text(editKm))
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun onUpdateKm() = { km: String ->
        if (isEmpty(km) || !isPositive(km)) {
            textVoltageRequired.text = ""
            textRemainingTime.text = ""

        } else {
            val requiredVoltageOnCharger = calculatorService.requiredVoltage(wheel, parmWhPerKm!!, floatOf(km))
            val requiredVoltage = requiredVoltageOnCharger - CHARGER_OFFSET
            val maxCharge = wheel!!.voltageMax - CHARGER_OFFSET
            val diff = round(requiredVoltage - parmVoltageDisconnectedFromCharger!!, 1)
            val rawHours = diff / wheel!!.chargeRate

            when {
                requiredVoltageOnCharger >= maxCharge -> {
                    textVoltageRequired.text = "Fill up!"
                    textRemainingTime.text = timeDisplay(rawHours)
                }

                requiredVoltage > parmVoltageDisconnectedFromCharger!! -> {
                    textVoltageRequired.text = "${requiredVoltageOnCharger}V (+$diff)"
                    textRemainingTime.text = timeDisplay(rawHours)
                }

                else -> {
                    textVoltageRequired.text = "Go!"
                    textRemainingTime.text = ""
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    internal fun timeDisplay(rawHours: Float): String {
        val hours = rawHours.toInt()
        val minutes = ((rawHours - hours) * 60).roundToInt()

        return if (hours > 0) "${hours}h${format("%02d", minutes)}" else "${minutes}m"
    }
}
