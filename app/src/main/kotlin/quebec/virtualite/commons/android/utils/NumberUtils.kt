package quebec.virtualite.commons.android.utils

import org.apache.http.util.TextUtils.isBlank
import kotlin.math.pow
import kotlin.math.roundToInt

object NumberUtils {

    fun floatOf(string: String): Float = if (isBlank(string.trim())) 0.0f else string.trim().toFloat()

    fun intOf(string: String): Int = if (isBlank(string.trim())) 0 else string.trim().toInt()

    fun round(value: Float, numDecimals: Int): Float {
        val factor = 10.0.pow(numDecimals.toDouble()).toFloat()
        return (value * factor).roundToInt() / factor
    }
}