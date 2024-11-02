package quebec.virtualite.unirider.views

import quebec.virtualite.unirider.database.WheelEntity
import java.util.Objects.equals

open class WheelValidator {

    // Parameters need to be nullable for unit testing - any()
    open fun canSave(updatedWheel: WheelEntity?, initialWheel: WheelEntity?): Boolean {
        if (equals(updatedWheel, initialWheel)
            || updatedWheel!!.name == ""
            || updatedWheel.wh == 0
            || updatedWheel.voltageMin == 0f
            || updatedWheel.voltageMax == 0f
            || updatedWheel.chargeRate == 0f
        )
            return false

        if (updatedWheel.voltageMin > updatedWheel.voltageMax ||
            updatedWheel.voltageFull < updatedWheel.voltageMin ||
            updatedWheel.voltageFull > updatedWheel.voltageMax
        )
            return false

        return (updatedWheel.chargeRate != initialWheel!!.chargeRate) ||
                (updatedWheel.chargerOffset != initialWheel.chargerOffset) ||
                (updatedWheel.distanceOffset != initialWheel.distanceOffset) ||
                (updatedWheel.isSold != initialWheel.isSold) ||
                (updatedWheel.mileage != initialWheel.mileage) ||
                (updatedWheel.name != initialWheel.name) ||
                (updatedWheel.premileage != initialWheel.premileage) ||
                (updatedWheel.voltageFull != initialWheel.voltageFull) ||
                (updatedWheel.voltageMax != initialWheel.voltageMax) ||
                (updatedWheel.voltageMin != initialWheel.voltageMin) ||
                (updatedWheel.wh != initialWheel.wh)
    }
}
