package quebec.virtualite.unirider.services

import org.apache.http.util.TextUtils.isEmpty
import quebec.virtualite.unirider.database.WheelEntity
import java.util.Locale.ENGLISH
import kotlin.math.roundToInt

open class CalculatorService {

    open fun batteryOn(wheel: WheelEntity, voltageS: String): String {
        if (!isEmpty(voltageS)) {

            val voltage = when {
                voltageS.indexOf('.') == -1 ->
                    voltageS.toInt() * 10
                voltageS.endsWith('.') ->
                    voltageS.replace(".", "0").toInt()
                else ->
                    voltageS.replace(".", "").toInt()
            }

            val highest = (wheel.voltageMax * 10).roundToInt()
            val lowest = (wheel.voltageMin * 10).roundToInt()
            val percentage: Float = (voltage - lowest) * 100f / (highest - lowest)

            if (percentage in 0f..100f) {
                return "%.1f%%"
                    .format(ENGLISH, percentage)
            }
        }

        return ""
    }
}
