package quebec.virtualite.commons.android.rest

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import kotlin.text.Charsets.UTF_8

object BasicAuthentication
{
    fun token(user: String, password: String): String
    {
        val encodedKey = "$user:$password".toByteArray(UTF_8)
        return "Basic " + encodeToString(encodedKey, NO_WRAP)
    }
}