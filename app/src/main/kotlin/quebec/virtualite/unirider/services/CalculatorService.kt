package quebec.virtualite.unirider.services

import org.apache.http.util.TextUtils.isEmpty
import java.util.Locale.ENGLISH

class CalculatorService {

    fun batteryOn(text: String): String {

        val voltage: String = text
        if (!isEmpty(voltage)) {
            val voltageF: Float = voltage.toFloat()
            val percentage = (voltageF - 79.2f) / (100.8f - 79.2f) * 100f

            if (0f <= percentage && percentage <= 100f) {
                return "%.1f%%"
                    .format(ENGLISH, percentage)
                    .replace(".0%", "%")
            }
        }

        return ""
    }
}
