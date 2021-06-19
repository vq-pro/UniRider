package quebec.virtualite.unirider.services

import org.apache.http.util.TextUtils.isEmpty
import java.util.Locale.ENGLISH
import kotlin.math.roundToInt

open class CalculatorService {

    open fun batteryOn(text: String, highestF: Float, lowestF: Float): String {

        if (!isEmpty(text)) {

            val voltage = when {
                text.indexOf('.') == -1 ->
                    text.toInt() * 10
                text.endsWith('.') ->
                    text.replace(".", "0").toInt()
                else ->
                    text.replace(".", "").toInt()
            }

            val highest = (highestF * 10).roundToInt()
            val lowest = (lowestF * 10).roundToInt()
            val percentage: Float = (voltage - lowest) * 100f / (highest - lowest)

            if (0f <= percentage && percentage <= 100f) {
                return "%.1f%%"
                    .format(ENGLISH, percentage)
                    .replace(".0%", "%")
            }
        }

        return ""
    }

    open fun wheels(): List<Wheel> {
        return listOf(
            Wheel("Gotway Nikola+", 100.8f, 78.0f),
            Wheel("Inmotion V10F", 84f, 68f),
            Wheel("KingSong 14S", 67.2f, 48.0f),
            Wheel("KingSong S18", 84.5f, 63.0f),
            Wheel("Veteran Sherman", 100.8f, 75.6f)
        )
    }
}
