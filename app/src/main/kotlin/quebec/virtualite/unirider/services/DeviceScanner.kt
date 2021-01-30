package quebec.virtualite.unirider.services

import java.util.function.Consumer

interface DeviceScanner {

    fun scan(whenDetecting: Consumer<String>)
}