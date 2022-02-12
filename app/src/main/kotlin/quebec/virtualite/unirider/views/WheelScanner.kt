package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.views.MainActivity

open class WheelScanner {
    open fun scan() {
        MainActivity.scanner.scan({ device ->
            val b = true
        })
    }
}
