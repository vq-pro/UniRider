package quebec.virtualite.commons.android.utils

import org.apache.http.util.TextUtils.isBlank
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import kotlin.math.pow
import kotlin.math.roundToInt

const val NB_DECIMALS = 1

object NumberUtils {

    fun floatOf(string: String): Float = if (isBlank(string.trim())) 0.0f else parseFloat(string.trim())

    fun intOf(string: String): Int = if (isBlank(string.trim())) 0 else parseInt(string.trim())

    fun isEmpty(value: String): Boolean {
        return isBlank(value.trim())
    }

    fun isNumeric(value: String): Boolean {
        return !isBlank(value.trim())
            && value.trim().matches("^[0-9.]*$".toRegex())
    }

    fun round(value: Float): Float {
        return round(value, NB_DECIMALS)
    }

    fun round(value: Float, numDecimals: Int): Float {
        val factor = 10.0.pow(numDecimals.toDouble()).toFloat()
        return (value * factor).roundToInt() / factor
    }

    fun safeFloatOf(string: String): Float {
        return if (isNumeric(string)) floatOf(string) else 0f
    }

    fun safeIntOf(string: String): Int {
        return if (isNumeric(string) && !string.contains('.')) intOf(string) else 0
    }
}