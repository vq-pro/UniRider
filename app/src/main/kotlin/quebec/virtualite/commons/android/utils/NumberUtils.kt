package quebec.virtualite.commons.android.utils

import org.apache.http.util.TextUtils
import org.apache.http.util.TextUtils.isBlank
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import kotlin.math.pow
import kotlin.math.roundToInt

object NumberUtils {

    fun floatOf(string: String): Float = if (isBlank(string.trim())) 0.0f else parseFloat(string.trim())

    fun intOf(string: String): Int = if (isBlank(string.trim())) 0 else parseInt(string.trim())

    fun isEmpty(value: String): Boolean {
        return TextUtils.isEmpty(value.trim())
    }

    fun isNumeric(value: String): Boolean {
        return value.trim().matches("^[0-9.]*$".toRegex())
    }

    fun isPositive(value: String): Boolean {
        return isNumeric(value) && floatOf(value) > 0f
    }

    fun round(value: Float, numDecimals: Int): Float {
        val factor = 10.0.pow(numDecimals.toDouble()).toFloat()
        return (value * factor).roundToInt() / factor
    }
}