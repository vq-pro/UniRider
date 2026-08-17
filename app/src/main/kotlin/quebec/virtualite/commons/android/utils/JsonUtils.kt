package quebec.virtualite.commons.android.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    private val objectMapper = ObjectMapper()

    fun serialize(responseBody: Any): String {
        return objectMapper.writeValueAsString(responseBody)
    }
}