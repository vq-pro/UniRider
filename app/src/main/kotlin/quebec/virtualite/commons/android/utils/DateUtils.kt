package quebec.virtualite.commons.android.utils

import java.lang.Integer.parseInt
import java.time.LocalDateTime

open class DateUtils {
    companion object {

        var simulatedNow: LocalDateTime? = null

        fun simulateNow(simulatedTime: String): Companion {
            simulatedNow = LocalDateTime.now()
                .withHour(parseInt(simulatedTime.substring(0, 2)))
                .withMinute(parseInt(simulatedTime.substring(3, 5)))

            return this
        }
    }

    open fun now(): LocalDateTime {
        return when (simulatedNow) {
            null -> LocalDateTime.now()
            else -> simulatedNow!!
        }
    }
}