package quebec.virtualite.commons.android.utils

import org.apache.http.util.TextUtils

object NumberUtils {

    fun intOf(string: String): Int = if (TextUtils.isBlank(string.trim())) 0 else string.trim().toInt()
}