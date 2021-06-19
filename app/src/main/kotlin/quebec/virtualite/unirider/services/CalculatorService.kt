package quebec.virtualite.unirider.services

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.apache.http.util.TextUtils.isEmpty
import java.util.Locale.ENGLISH
import kotlin.math.roundToInt

open class CalculatorService {

    @Parcelize
    private data class Wheel(

        val name: String,
        val highest: Float,
        val lowest: Float

    ) : Parcelable

    private val WHEELS = listOf(
        Wheel("Gotway Nikola+", 100.8f, 78.0f),
        Wheel("Inmotion V10F", 84f, 68f),
        Wheel("KingSong 14S", 67.2f, 48.0f),
        Wheel("KingSong S18", 84.5f, 63.0f),
        Wheel("Veteran Sherman", 100.8f, 75.6f)
    )

    open fun batteryOn(wheelName: String, voltageS: String): String {
        val wheel: Wheel = findWheel(wheelName)

        if (!isEmpty(voltageS)) {

            val voltage = when {
                voltageS.indexOf('.') == -1 ->
                    voltageS.toInt() * 10
                voltageS.endsWith('.') ->
                    voltageS.replace(".", "0").toInt()
                else ->
                    voltageS.replace(".", "").toInt()
            }

            val highest = (wheel.highest * 10).roundToInt()
            val lowest = (wheel.lowest * 10).roundToInt()
            val percentage: Float = (voltage - lowest) * 100f / (highest - lowest)

            if (0f <= percentage && percentage <= 100f) {
                return "%.1f%%"
                    .format(ENGLISH, percentage)
                    .replace(".0%", "%")
            }
        }

        return ""
    }

    open fun wheels(): List<String> {
        return WHEELS.map { wheel -> wheel.name }
    }

    private fun findWheel(wheelName: String): Wheel {
        WHEELS.forEach { wheel ->
            if (wheel.name.equals(wheelName))
                return wheel
        }

        throw RuntimeException()
    }
}
