package quebec.virtualite.unirider.views

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

    companion object {
        const val PARAMETER_WHEEL_ID = "wheelID"
    }

    internal lateinit var buttonConnect: Button
    internal lateinit var buttonDelete: Button
    internal lateinit var buttonEdit: Button
    internal lateinit var textBattery: TextView
    internal lateinit var textBtName: TextView
    internal lateinit var textMileage: TextView
    internal lateinit var textName: TextView
    internal lateinit var editVoltage: EditText

    internal var wheel: WheelEntity? = null

    internal var parmWheelId: Long? = 0

    private var calculatorService = CalculatorService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        textBtName = view.findViewById(R.id.view_bt_name)
        textMileage = view.findViewById(R.id.view_mileage)
        editVoltage = view.findViewById(R.id.edit_voltage)
        textBattery = view.findViewById(R.id.view_battery)
        buttonConnect = view.findViewById(R.id.button_connect)
        buttonEdit = view.findViewById(R.id.button_edit)
        buttonDelete = view.findViewById(R.id.button_delete)

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            if (wheel != null) {
                widgets.addTextChangedListener(editVoltage, onUpdateVoltage())
                widgets.setOnClickListener(buttonConnect, onConnect())
                widgets.setOnClickListener(buttonEdit, onEdit())
                widgets.setOnLongClickListener(buttonDelete, onDelete())

                textName.text = wheel!!.name
                textBtName.text = wheel!!.btName
                textMileage.text = "${wheel!!.totalMileage()}"
            }
        }
    }

    fun onConnect(): (View) -> Unit = {
        if (wheel!!.btName == null) {
            goto(R.id.action_WheelViewFragment_to_WheelScanFragment)

        } else {
            fragments.runWithWait {
                external.connector().getDeviceInfo(wheel!!.btAddr) {
                    fragments.doneWaiting {
                        val newMileage = it.mileage.roundToInt()
                        val newVoltage = round(it.voltage, 1)

                        updateWheel(newMileage, newVoltage)
                    }
                }
            }
        }
    }

    fun onDelete(): (View) -> Unit = {
        goto(R.id.action_WheelViewFragment_to_WheelDeleteConfirmationFragment)
    }

    fun onEdit(): (View) -> Unit = {
        goto(R.id.action_WheelViewFragment_to_WheelEditFragment)
    }

    fun onUpdateVoltage() = { voltageParm: String ->
        val voltage = voltageParm.trim()
        textBattery.text = if (isEmpty(voltage)) "" else getPercentage(voltage)
    }

    private fun getPercentage(voltage: String): String {
        return when (val percentage = calculatorService.percentage(wheel, parseFloat(voltage))) {
            in 0f..100f -> "%.1f%%".format(ENGLISH, percentage)
            else -> ""
        }
    }

    private fun goto(id: Int) {
        fragments.navigateTo(id, Pair(PARAMETER_WHEEL_ID, wheel!!.id))
    }

    private fun updateWheel(newMileage: Int, newVoltage: Float) {
        wheel = WheelEntity(
            wheel!!.id, wheel!!.name, wheel!!.btName, wheel!!.btAddr,
            wheel!!.premileage, newMileage,
            wheel!!.voltageMin, wheel!!.voltageMax
        )

        external.runDB { it.saveWheel(wheel) }
        fragments.runUI {
            textMileage.text = "${wheel!!.totalMileage()}"
            editVoltage.setText("$newVoltage")
        }
    }
}
