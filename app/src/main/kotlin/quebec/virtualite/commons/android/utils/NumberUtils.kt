package quebec.virtualite.commons.android.utils

import org.apache.http.util.TextUtils.isBlank

object NumberUtils {

    fun floatOf(string: String): Float = if (isBlank(string.trim())) 0.0f else string.trim().toFloat()

    fun intOf(string: String): Int = if (isBlank(string.trim())) 0 else string.trim().toInt()
}